package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import zswi.protocols.communication.core.HTTPConnectionManager;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.schemas.carddav.allprop.Resourcetype;
import zswi.schemas.carddav.allprop.SupportedAddressData;

public class AddressBookHomeSet extends AbstractHomeSetCollection {

  AddressBookCollection defaultAddressbook;
  java.util.List<AddressBookCollection> addressBookCollections;

  public AddressBookHomeSet(HTTPConnectionManager connectionManager, PrincipalCollection principals, URI uriForRequest) throws JAXBException, ClientProtocolException, IOException, URISyntaxException {
    PropfindRequest req = new PropfindRequest(uriForRequest, 1);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("props-adressbookhomeset-request.xml");
//    InputStream is = ClassLoader.getSystemResourceAsStream("props-adressbookhomeset-request.xml");

    StringEntity se = new StringEntity(convertStreamToString(is));

    se.setContentType("text/xml");
    req.setEntity(se);

    HttpResponse resp = connectionManager.getHttpClient().execute(req);
    
    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.carddav.allprop");
    Unmarshaller appPropUnmarshaller = jc.createUnmarshaller();
    zswi.schemas.carddav.allprop.Multistatus unmarshalAllProp = (zswi.schemas.carddav.allprop.Multistatus)appPropUnmarshaller.unmarshal(resp.getEntity().getContent());
    
    for (zswi.schemas.carddav.allprop.Response response: unmarshalAllProp.getResponse()) {
      for (zswi.schemas.carddav.allprop.Propstat propstat: response.getPropstat()) {
        if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
          
          /** If URL is the same, it's the calendar-home-set */
          if (response.getHref().equals(uriForRequest.getPath())) {
            
            setOwner(principals);
            setQuotaAvailableBytes(propstat.getProp().getQuotaAvailableBytes());
            setQuotaUsedBytes(propstat.getProp().getQuotaUsedBytes());
            setGetctag(propstat.getProp().getGetctag());
            
            String syncToken = propstat.getProp().getSyncToken();
            if (syncToken != null)
              setSyncToken(new java.net.URI(syncToken));
                                    
            setDisplayName(propstat.getProp().getDisplayname());
            setUri(response.getHref());
            principals.setAddressBookHomeSet(this);
            
          } else {
            
            if (isAddressbookCollection(propstat.getProp())) {
              AddressBookCollection collection = new AddressBookCollection(connectionManager);
                            
              collection.setDisplayName(propstat.getProp().getDisplayname());
              collection.setGetctag(propstat.getProp().getGetctag());
              collection.setOwner(principals);
              collection.setUri(response.getHref());
                            
              collection.setQuotaAvailableBytes(propstat.getProp().getQuotaAvailableBytes());
              collection.setQuotaUsedBytes(propstat.getProp().getQuotaUsedBytes());
              
              String syncToken = propstat.getProp().getSyncToken();
              if (syncToken != null)
                collection.setSyncToken(new java.net.URI(syncToken));
                                       
              // TODO should use SupportedData instead
              SupportedAddressData set = propstat.getProp().getSupportedAddressData();
              if ((set != null) && (set.getAddressDataType() != null)) {
                collection.getSupportedAddressData().add(set.getAddressDataType().getContentType());
              }
              
              getAddressbookCollections().add(collection);
            }
          }
        }
      }
    }
    
    EntityUtils.consume(resp.getEntity());
  }
  
  protected boolean isAddressbookCollection(zswi.schemas.carddav.allprop.Prop properties) {
    Resourcetype collectionType = properties.getResourcetype();

    if (collectionType.getAddressbook() != null)
      return true;
              
    return false;
  }
  
  public List<AddressBookCollection> getAddressbookCollections() {
    if (addressBookCollections == null) {
      addressBookCollections = new ArrayList<AddressBookCollection>();
    }
    return addressBookCollections;
  }

}
