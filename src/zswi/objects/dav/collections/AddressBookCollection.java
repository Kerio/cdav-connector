package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import zswi.protocols.carddav.ServerVCard;
import zswi.protocols.communication.core.HTTPConnectionManager;
import zswi.protocols.communication.core.DavStore;
import zswi.protocols.communication.core.DavStore.DavStoreException;
import zswi.protocols.communication.core.DavStore.NotImplemented;
import zswi.protocols.communication.core.Utilities;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.PutRequest;
import zswi.schemas.carddav.multiget.AddressbookMultiget;
import zswi.schemas.carddav.multiget.ObjectFactory;

public class AddressBookCollection extends AbstractNotPrincipalCollection {

  /** http://tools.ietf.org/html/rfc6352#section-6.2.2 */
  java.util.List<String> supportedAddressData;
  
  HTTPConnectionManager connectionManager;

  public AddressBookCollection(HTTPConnectionManager _connectionManager) {
    connectionManager = _connectionManager;
  }
  
  public java.util.List<String> getSupportedAddressData() {
    if (supportedAddressData == null)
      supportedAddressData = new ArrayList<String>();
    return supportedAddressData;
  }
  
  /**
   * Get all vCards from an addressbook collection.
   * 
   * @param collection
   * @return
   * @throws DavStoreException
   */
  public List<ServerVCard> getVCards(AddressBookCollection collection) throws DavStoreException {

    String reportResponse = "";
    try {
      reportResponse = DavStore.report(connectionManager, "addressbook-query-request.xml", collection.getUri(), 1);
    }
    catch (NotImplemented e1) {
      try {
        return fetchVCardsByMultiget(collection);
      }
      catch (NotImplemented e) {
        throw new DavStoreException(e);
      }
    }
    List<ServerVCard> result = new ArrayList<ServerVCard>();

    if (reportResponse.length() > 0) {
      JAXBContext jc;
      try {
        jc = JAXBContext.newInstance("zswi.schemas.carddav.objects");
        Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
        StringReader reader = new StringReader(reportResponse);
        zswi.schemas.carddav.objects.Multistatus multistatus = (zswi.schemas.carddav.objects.Multistatus)userInfounmarshaller.unmarshal(reader);

        for (zswi.schemas.carddav.objects.Response xmlResponse: multistatus.getResponse()) {
          String hrefForObject = xmlResponse.getHref();
          for (zswi.schemas.carddav.objects.Propstat propstat: xmlResponse.getPropstat()) {
            if (DavStore.PROPSTAT_OK.equals(propstat.getStatus())) {
              String sin = propstat.getProp().getAddressData();
              VCardEngine builder = new VCardEngine();
              VCard cardData = builder.parse(sin);
              ServerVCard calendarObject = new ServerVCard(cardData, propstat.getProp().getGetetag(), hrefForObject);
              result.add(calendarObject);
            }
          }
        }
      }
      catch (JAXBException e) {
        throw new DavStoreException(e);
      }
      catch (IOException e) {
        throw new DavStoreException(e);
      }
    }

    return result;
  }

  /**
   * Some servers (hello CommuniGate Pro 5.4) don't support the addressbook-query request, even if it's a required of the spec.
   * If that's the case, we will try to find the vCards objects in the collection by doing a PROPFIND request to find all 
   * links (href) and eTag for the objects, and do to a adressbook-multiget request after.
   * 
   * @param collection
   * @return
   * @throws DavStoreException
   * @throws NotImplemented
   */
  protected List<ServerVCard> fetchVCardsByMultiget(AddressBookCollection collection) throws DavStoreException, NotImplemented {
    List<ServerVCard> result = new ArrayList<ServerVCard>();

    PropfindRequest req;
    try {
      req = new PropfindRequest(connectionManager.initUri(collection.getUri()), 1);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream is = classLoader.getResourceAsStream("propfind-etag-request.xml");

      StringEntity se = new StringEntity(Utilities.convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse response = connectionManager.getHttpClient().execute(req);
      int status = response.getStatusLine().getStatusCode();
      if (status < 300) {
        JAXBContext jc = JAXBContext.newInstance("zswi.schemas.dav.propfind.etag");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        zswi.schemas.dav.propfind.etag.Multistatus unmarshal = (zswi.schemas.dav.propfind.etag.Multistatus)unmarshaller.unmarshal(response.getEntity().getContent());

        EntityUtils.consume(response.getEntity());

        List<zswi.schemas.dav.propfind.etag.Response> responses = unmarshal.getResponse();
        for (zswi.schemas.dav.propfind.etag.Response xmlResponse: responses) {
          String href = xmlResponse.getHref();
          if (DavStore.PROPSTAT_OK.equals(xmlResponse.getPropstat().getStatus())) {
            String etag = xmlResponse.getPropstat().getProp().getGetetag();
            ServerVCard card = new ServerVCard(null, etag, href);
            result.add(card);
          }
        }
        result = doAddressBookMultiget(result, collection);
      } else {
        EntityUtils.consume(response.getEntity());
      }
    }
    catch (URISyntaxException e) {
      throw new DavStoreException("Couldn't build a URL for " + connectionManager.httpScheme() + connectionManager.getServerName() +  connectionManager.getPort() + "/.well-known/caldav");
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (IOException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (JAXBException e) {
      throw new DavStoreException(e.getMessage());
    }

    return result;
  }

  /**
   * Do the addressbook-multiget request (usually called from fetchVCardsByMultiget).
   * 
   * @param listFromPropfind
   * @param collection
   * @return
   * @throws DavStoreException
   * @throws NotImplemented
   */
  protected List<ServerVCard> doAddressBookMultiget(List<ServerVCard> listFromPropfind, AddressBookCollection collection) throws DavStoreException, NotImplemented {
    List<ServerVCard> filteredArray = new ArrayList<ServerVCard>();

    zswi.schemas.carddav.multiget.ObjectFactory factory = new ObjectFactory();

    zswi.schemas.carddav.multiget.Getetag etagProp = factory.createGetetag();
    zswi.schemas.carddav.multiget.AddressData addressDataProp = factory.createAddressData();
    zswi.schemas.carddav.multiget.Prop requestProp = factory.createProp();
    requestProp.setAddressData(addressDataProp);
    requestProp.setGetetag(etagProp);

    AddressbookMultiget multiget = factory.createAddressbookMultiget();
    multiget.setProp(requestProp);

    for (ServerVCard card: listFromPropfind) {
      multiget.getHref().add(card.getPath());
    }

    try {
      StringWriter sw = new StringWriter();
      JAXBContext jc = JAXBContext.newInstance("zswi.schemas.carddav.multiget");
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
      marshaller.marshal(multiget, sw);

      String response = DavStore.report(connectionManager, sw, collection.getUri(), 1);

      jc = JAXBContext.newInstance("zswi.schemas.carddav.multiget.response");
      Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
      StringReader reader = new StringReader(response);
      zswi.schemas.carddav.multiget.response.Multistatus multistatus = (zswi.schemas.carddav.multiget.response.Multistatus)userInfounmarshaller.unmarshal(reader);
      for (zswi.schemas.carddav.multiget.response.Response xmlResponse: multistatus.getResponse()) {
        String hrefForObject = xmlResponse.getHref();
        for (zswi.schemas.carddav.multiget.response.Propstat propstat: xmlResponse.getPropstat()) {
          if (DavStore.PROPSTAT_OK.equals(propstat.getStatus())) {
            String sin = propstat.getProp().getAddressData();
            VCardEngine builder = new VCardEngine();
            VCard cardData = builder.parse(sin);
            ServerVCard calendarObject = new ServerVCard(cardData, propstat.getProp().getGetetag(), hrefForObject);
            filteredArray.add(calendarObject);
          }
        }
      }
    }
    catch (JAXBException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }

    return filteredArray;
  }

  /**
   * Add a vCard object in the collection.
   * 
   * @param collection
   * @param card
   * @return
   * @throws DavStoreException
   */
  public ServerVCard addVCard(AddressBookCollection collection, VCard card) throws DavStoreException {
    StringEntity se;
    try {
      VCardWriter writer = new VCardWriter();
      writer.setVCard(card);
      se = new StringEntity(writer.buildVCardString());
      se.setContentType(DavStore.TYPE_VCARD);

      URI urlForRequest = connectionManager.initUri(collection.getUri() + card.getUID().getUID() + ".ics");
      PutRequest putReq = new PutRequest(urlForRequest);
      putReq.setEntity(se);
      HttpResponse resp = connectionManager.getHttpClient().execute(putReq);

      EntityUtils.consume(resp.getEntity());

      String path = urlForRequest.getPath();

      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {

        Header[] headers = resp.getHeaders("Etag");
        String etag = "";

        if (headers.length == 1) {
          etag = headers[0].getValue();
        }

        Header[] locations = resp.getHeaders("Location");
        if (locations.length == 1) {
          try {
            URL locationUrl = new URL(locations[0].getValue());
            path = locationUrl.getPath();
          } catch (MalformedURLException urle) {
            // It might be just a path, so let's take this instead
            if (locations[0].getValue().length() > 0)
              path = locations[0].getValue();
          }
        }

        ServerVCard vcard = new ServerVCard(card, etag, path);
        // TODO if Location is not present, we should do a PROPFIND at the same URL to get the value of the getetag property
        return vcard;
      } else {
        throw new DavStoreException("Can't create the calendar object, returned status code is " + resp.getStatusLine().getStatusCode());
      }
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (IOException e) {
      throw new DavStoreException(e.getMessage());
    }
  }

  /**
   * Update a vCard object in the collection.
   * 
   * @param card
   * @throws DavStoreException
   */
  public void updateVCard(ServerVCard card) throws DavStoreException {
    StringEntity se = null;
    try {
      VCardWriter writer = new VCardWriter();
      writer.setVCard(card.getVcard());
      se = new StringEntity(writer.buildVCardString());
      se.setContentType(DavStore.TYPE_VCARD);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }

    String newEtag = this.updateObject(connectionManager, se, card.geteTag(), card.getPath());
    if (newEtag != null) {
      card.seteTag(newEtag);
    }
  }
  
  /**
   * Delete a vCard object by doing a DELETE request on the path of the vCard object on the server.
   * 
   * @param card
   * @return
   * @throws ClientProtocolException
   * @throws URISyntaxException
   * @throws IOException
   */
  public boolean deleteVCard(ServerVCard card) throws ClientProtocolException, URISyntaxException, IOException {
    return this.delete(connectionManager, card.getPath(), card.geteTag());
  }

    
}
