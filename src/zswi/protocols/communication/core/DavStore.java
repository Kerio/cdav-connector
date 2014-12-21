package zswi.protocols.communication.core;

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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import zswi.objects.dav.collections.AbstractDavCollection;
import zswi.objects.dav.collections.AbstractNotPrincipalCollection;
import zswi.objects.dav.collections.AddressBookHomeSet;
import zswi.objects.dav.collections.CalendarCollection;
import zswi.objects.dav.collections.CalendarHomeSet;
import zswi.objects.dav.collections.InboxCollection;
import zswi.objects.dav.collections.PrincipalCollection;
import zswi.objects.dav.enums.DavFeature;
import zswi.protocols.caldav.ServerVCalendar;
import zswi.protocols.communication.core.requests.CopyRequest;
import zswi.protocols.communication.core.requests.DeleteRequest;
import zswi.protocols.communication.core.requests.MkCalendarRequest;
import zswi.protocols.communication.core.requests.MoveRequest;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.ReportRequest;
import zswi.schemas.caldav.mkcalendar.Mkcalendar;
import zswi.schemas.dav.discovery.PrincipalURL;
import zswi.schemas.dav.icalendarobjects.Response;

/**
 * Connect to a CalDAV/CardDAV server by auto-discovery or by a specific URL.
 * 
 * @author Pascal Robert
 *
 * TODO Checking those requirements http://tools.ietf.org/html/rfc4791#section-5.3.2.1
 * TODO Sync changes from the server by checking the eTag values
 * TODO Implement external attachments http://tools.ietf.org/html/rfc4791#section-8.5
 * TODO Implement RFC 6638 http://tools.ietf.org/html/rfc6638
 * TODO Implement sharing http://svn.calendarserver.org/repository/calendarserver/CalendarServer/trunk/doc/Extensions/caldav-sharing.txt
 * TODO Implement RFC 6578 http://tools.ietf.org/html/rfc6578
 *
 */
public class DavStore {

  private PrincipalCollection _principalCollection;
  public static final String PROPSTAT_OK = "HTTP/1.1 200 OK";
  public static final String TYPE_CALENDAR = "text/calendar; charset=utf-8";
  public static final String TYPE_VCARD = "text/vcard; charset=utf-8";
  protected HTTPConnectionManager connectionManager;

  static final Logger logger = Logger.getLogger(DavStore.class.getName());

  /**
   * Static block so that iCal4j parser is less severe.
   */
  static {     
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
  }

  /**
   * Connect to a DAV store with credentials. The username must be an email address or a fully qualified http(s) URL, 
   * because the domain will be extracted from the email address or from the HTTP(S) URL.
   * 
   * It will works only if SRV records for _caldavs._tcp.yourdomain.com or _caldav._tcp.yourdomain.com
   * 
   * If you don't have any SRV records for your CalDAV/CardDAV service, please use the constructor with the url argument
   * 
   * @param username For auto-discovery, the username must contains the domain. You can either pass a email address (me@domain.com) or a HTTP url (https://me@domain.com).
   * @param password
   * @throws DavStoreException 
   */
  public DavStore(String username, String password) throws DavStoreException {
    connectionManager = new HTTPConnectionManager();
    connectionManager.setUsername(username);
    connectionManager.setPassword(password);
    connectionManager.setSecure(false);
    extractUserDetails(username);
    try {
      checkSrvRecords();
    } catch (NamingException namingEx) {
      throw new DavStoreException("Couldn't find SRV records for the host " + connectionManager.getServerName());
    }
    try {
      checkTxtRecords();
    } catch (NamingException namingEx) {
      logger.info("We didn't find any TXT records, will try well_know URLs");
      try {
        checkWellKnownUrl();
      } catch (NoRedirectFoundException e) {
        logger.info(e.getMessage());
        logger.info("Let's retry with username@domain");
        connectionManager.closeConnection();
        connectionManager.setUsername(connectionManager.getUsername() + "@" + connectionManager.getDomain());
        try {
          checkWellKnownUrl();
        } catch (NoRedirectFoundException e2) {
          logger.info(e2.getMessage());
        }
      }
    } 

    /* After we have found the well known URL, we clear the current httpClient. Why? Because the host returned by the well known URL might be different. */
    connectionManager.closeConnection();

    fetchPrincipalsCollection(connectionManager.getPath(), false);
  }

  /**
   * Connect to a DAV store with credentials and a URL. The URL must be the location of 
   * the user's principals (e.g. http://mydomain.com/principals/users/myuser or something
   * similar).
   * 
   * For example, a URL for Google Calendar would like: https://www.google.com/calendar/dav/myuser%40gmail.com/user/
   * 
   * You need to pass the URL to the principals, NOT to a calendar or address book collection!
   * 
   * @param username
   * @param password
   * @param url
   * @throws DavStoreException 
   */
  public DavStore(String username, String password, String url) throws DavStoreException {
    URL rootUrl;

    connectionManager = new HTTPConnectionManager();
    connectionManager.setUsername(username);
    connectionManager.setPassword(password);
    connectionManager.setSecure(false);
    extractUserDetails(url);

    try {
      boolean _isSecure = (url.startsWith("https")) ? true : false;
      connectionManager.setSecure(_isSecure);
      checkWellKnownUrl();
    }
    catch (NoRedirectFoundException e) {
      e.printStackTrace();
    } finally {
      connectionManager.closeConnection();
    }

    try {
      rootUrl = new URL(url);
      boolean _isSecure = (rootUrl.getProtocol().equals("https")) ? true : false;
      connectionManager.setSecure(_isSecure);
    }
    catch (MalformedURLException e) {
      throw new DavStoreException(e);
    }

    fetchPrincipalsCollection(connectionManager.getPath(), false);
  }

  public void disconnect() {
    connectionManager.closeConnection();
  }

  /**
   * This method will extract the username and domain from a HTTP(s) URL or an email address.
   * 
   * @param urlAsString
   */
  protected void extractUserDetails(String urlAsString) {
    try {
      URL urlForUser = new URL(urlAsString);
      connectionManager.setDomain(urlForUser.getHost());
      connectionManager.setServerName(connectionManager.getDomain());
      connectionManager.setPath(urlForUser.getPath());
      if (urlForUser.getUserInfo() != null) {
        connectionManager.setUsername(urlForUser.getUserInfo());
      }
      connectionManager.setPort(urlForUser.getPort());
    }
    catch (MalformedURLException e) {
      logger.warning("Couldn't transform the username to a URL : " + e.getMessage());
      if (urlAsString.contains("@")) {
        String[] emailComponents = urlAsString.split("@");
        if (emailComponents.length == 2) {
          connectionManager.setUsername(emailComponents[0]);
          connectionManager.setDomain(emailComponents[1]);
        }
      }
    }
  }

  /**
   * Check if we can find a _caldavs._tcp. or _caldav._tcp. SRV record for the domain of the user.
   * 
   * @throws NamingException
   */
  protected void checkSrvRecords() throws NamingException {
    HashMap<String, String> results;
    try {
      results = DNSUtilities.doSRVLookup("_caldavs._tcp." + connectionManager.getDomain());
      connectionManager.setSecure(true);
    }
    catch (NamingException e) {
      results = DNSUtilities.doSRVLookup("_caldav._tcp." + connectionManager.getDomain());
    }
    connectionManager.setPort(Integer.valueOf(results.get("port")));
    connectionManager.setServerName(results.get("host"));
  }

  /**
   * Check if we can find a URL to the user's DAV principals in a TXT DNS record, with the URL in a "path" key.
   * 
   * @throws NamingException
   */
  protected void checkTxtRecords() throws NamingException {
    HashMap<String, String> results = new HashMap<String, String>();
    if (connectionManager.isSecure()) {
      results = DNSUtilities.doTXTLookup("_caldavs._tcp." + connectionManager.getDomain());
    } else {
      results = DNSUtilities.doTXTLookup("_caldav._tcp." + connectionManager.getDomain());
    }
    connectionManager.setPath(results.get("path"));
  }

  /**
   * Check if the server have a /.well-known/caldav URL that will redirect us to the location of the user's DAV principals.
   * 
   * @throws DavStoreException
   * @throws NoRedirectFoundException
   */
  protected void checkWellKnownUrl() throws DavStoreException, NoRedirectFoundException {
    httpClient().setRedirectStrategy(new NotRedirectStrategy());

    PropfindRequest req;
    try {
      URL urlForRequest = new URL(httpScheme(), connectionManager.getServerName(), connectionManager.getPort(), "/.well-known/caldav");
      req = new PropfindRequest(urlForRequest.toURI(), 0);
      InputStream is = ClassLoader.getSystemResourceAsStream("well-known-request.xml");

      StringEntity se = new StringEntity(Utilities.convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse response = httpClient().execute(req);
      int status = response.getStatusLine().getStatusCode();
      if (status >= 300 && status < 400) {
        Header[] location = response.getHeaders("Location");
        String urlFromHeader = location[0].getValue();
        URL rootUrl;
        // Kerio Connect returns a relative URL
        try {
          rootUrl = new URL(urlFromHeader);
        } catch (MalformedURLException e) {
          rootUrl = initUri(urlFromHeader).toURL();
        }
        connectionManager.setPath(rootUrl.getPath());
        connectionManager.setSecure(false);
        boolean _isSecure = (rootUrl.getProtocol().equals("https")) ? true : false;
        connectionManager.setSecure(_isSecure);
        connectionManager.setServerName(rootUrl.getHost());
      } else {
        throw new NoRedirectFoundException("No redirection found");
      }

      EntityUtils.consume(response.getEntity());
    }
    catch (URISyntaxException e) {
      throw new DavStoreException("Couldn't build a URL for " + httpScheme() + connectionManager.getServerName() + connectionManager.getPort() + "/.well-known/caldav");
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (IOException e) {
      throw new DavStoreException(e.getMessage());
    }
  }

  /**
   * Query the path to see if it contains a link to the user's DAV principals. If we found the principals, 
   * we will fetch the calendar home set and the addressbook home set, and their associated collections.
   * 
   * @param path
   * @throws DavStoreException
   */
  protected void fetchPrincipalsCollection(String path, boolean secondTry) throws DavStoreException {
    PropfindRequest req;
    try {
      URI urlForRequest = initUri(path);
      req = new PropfindRequest(urlForRequest, 0);
      InputStream is = ClassLoader.getSystemResourceAsStream("well-known-request.xml");

      StringEntity se = new StringEntity(Utilities.convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = httpClient().execute(req);

      int statusCode = resp.getStatusLine().getStatusCode();

      if (statusCode == HttpStatus.SC_MULTI_STATUS) {
        String currentUserPrincipal = null;

        JAXBContext jc = JAXBContext.newInstance("zswi.schemas.dav.discovery");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        zswi.schemas.dav.discovery.Multistatus unmarshal = (zswi.schemas.dav.discovery.Multistatus)unmarshaller.unmarshal(resp.getEntity().getContent());
        for (zswi.schemas.dav.discovery.Propstat propstat: unmarshal.getResponse().getPropstat()) {
          if (PROPSTAT_OK.equals(propstat.getStatus())) {
            zswi.schemas.dav.discovery.CurrentUserPrincipal hrefUserPrincipal = propstat.getProp().getCurrentUserPrincipal();
            PrincipalURL principalUrl = propstat.getProp().getPrincipalURL();
            if ((hrefUserPrincipal != null) && (hrefUserPrincipal.getHref() != null)) {
              currentUserPrincipal = hrefUserPrincipal.getHref();
            } else if ((principalUrl != null) && (principalUrl.getHref() != null)) {
              currentUserPrincipal = principalUrl.getHref();
            } else {
              logger.warning("No URL found for principals at " + path);
            }
          }
        }

        EntityUtils.consume(resp.getEntity());

        /*
         *  Old and/or bad implementations like CommuniGate Pro 5.3.x don't have principals, so it's probably directly the calendar-home-set directly. 
         *  We will fake principals in those cases.
         */
        if (currentUserPrincipal == null) {
          PrincipalCollection principals = new PrincipalCollection(this, initUri(path), true, true);
          CalendarHomeSet calHomeSet = new CalendarHomeSet(connectionManager, principals, initUri(principals.getUri()));
          fetchFeatures(calHomeSet);
          AddressBookHomeSet addressBookSet = new AddressBookHomeSet(connectionManager, principals, initUri(principals.getUri()));
          fetchFeatures(addressBookSet);
          _principalCollection = calHomeSet.getOwner();
        } else {
          PrincipalCollection principals = new PrincipalCollection(this, initUri(currentUserPrincipal), false, true);
          if (principals.getCalendarHomeSetUrl() != null) {
            CalendarHomeSet calHomeSet = new CalendarHomeSet(connectionManager, principals, initUri(principals.getCalendarHomeSetUrl().getPath()));
            fetchFeatures(calHomeSet);
            _principalCollection = calHomeSet.getOwner();
          }
          if (principals.getAddressbookHomeSetUrl() != null) {
            AddressBookHomeSet addressBookSet = new AddressBookHomeSet(connectionManager, principals, initUri(principals.getAddressbookHomeSetUrl().getPath()));
            fetchFeatures(addressBookSet);
          }
        }      
      } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
        // The problem might be that the server is requesting an username in the user@domain.com format (iCloud is an example). 
        // So let's try again with this format.
        if (!secondTry) {
          EntityUtils.consume(resp.getEntity());
          connectionManager.setUsername(connectionManager.getUsername() + "@" + connectionManager.getDomain());
          fetchPrincipalsCollection(path,true);
        } else {
          throw new DavStoreException("Can't fetch principals, bad credentials.");
        }
      } else {
        EntityUtils.consume(resp.getEntity());
        throw new DavStoreException("Can't fetch principals, server is responding with " + statusCode);
      }
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    catch (ClientProtocolException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }
    catch (JAXBException e) {
      throw new DavStoreException(e);
    }
    catch (ParserException e) {
      throw new DavStoreException(e);
    }
  }

  /**
   * Return the user's DAV principals.
   * 
   * @return
   */
  public PrincipalCollection principalCollection() {
    return _principalCollection;
  }
  
  public URI initUri(String path) throws URISyntaxException {
    return connectionManager.initUri(path);
  }
  
  public String httpScheme() {
    return connectionManager.httpScheme();
  }

  public DefaultHttpClient httpClient() {
    return connectionManager.getHttpClient();
  }

  public void addCalendarCollection(CalendarCollection collection) throws DavStoreException {
    MkCalendarRequest req;

    try {
      req = new MkCalendarRequest(initUri(collection.getUri()));
      // FIXME it should work with Fruux
      if (!(this.principalCollection().getCalendarHomeSet().allowedMethods().contains(req.getMethod()))) {
        throw new DavStoreException("The calendar home-set doesn't allow the MKCALENDAR method");
      }

      zswi.schemas.caldav.mkcalendar.ObjectFactory factory = new zswi.schemas.caldav.mkcalendar.ObjectFactory();

      zswi.schemas.caldav.mkcalendar.SupportedCalendarComponentSet supportedCalendarCompSet = factory.createSupportedCalendarComponentSet();
      for (String componentType: collection.getSupportedCalendarComponentSet()) {
        zswi.schemas.caldav.mkcalendar.Comp component = factory.createComp();
        component.setName(componentType);
        supportedCalendarCompSet.getComp().add(component);
      }

      zswi.schemas.caldav.mkcalendar.Prop propElement = factory.createProp();
      propElement.setCalendarColor(collection.getCalendarColor());
      propElement.setCalendarOrder(collection.getCalendarOrder());
      if (collection.getCalendarTimezone() != null) {
        propElement.setCalendarTimezone(collection.getCalendarTimezone().toString());
      }
      propElement.setDisplayname(collection.getDisplayName());
      propElement.setSupportedCalendarComponentSet(supportedCalendarCompSet);

      zswi.schemas.caldav.mkcalendar.Set setElement = factory.createSet();
      setElement.setProp(propElement);

      Mkcalendar mkcalendarElement = factory.createMkcalendar();
      mkcalendarElement.setSet(setElement);

      StringWriter sw = new StringWriter();
      JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.mkcalendar");
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
      marshaller.marshal(mkcalendarElement, sw);

      StringEntity body = new StringEntity(sw.toString());
      body.setContentType("text/xml");
      req.setEntity(body);

      HttpResponse resp = connectionManager.getHttpClient().execute(req);

      int statusCode = resp.getStatusLine().getStatusCode();
      if ((statusCode != HttpStatus.SC_CREATED) && (statusCode != HttpStatus.SC_OK)) {
        String bodyOfResponse = EntityUtils.toString(resp.getEntity());

        EntityUtils.consume(resp.getEntity());

        if (statusCode == HttpStatus.SC_MULTI_STATUS) {
          /* 
           * TODO need to parse the error when status code is 207 Multistatus.
           * The collection creation MIGHT have worked. In my tests, Kerio Connect was sending back the following response, but the collection was indeed created.
           * So we must check if the collection was created before throwing a exception.
           */
          /*
          HTTP/1.1 207 Multistatus

          <?xml version="1.0" encoding="UTF-8"?>
          <a:multistatus xmlns:a="DAV:" xmlns:c="http://apple.com/ns/ical/" xmlns:d="urn:ietf:params:xml:ns:caldav" xmlns:b="xml:">
          <a:response>
            <a:href>/full-calendars/kerio.famillerobert.lan/admin/12348</a:href>
            <a:propstat>
              <a:status>HTTP/1.1 200 OK</a:status>
              <a:prop>
                <a:displayname>Sans titre</a:displayname>
              </a:prop>
            </a:propstat>
            <a:propstat>
              <a:status>HTTP/1.1 403 Forbidden</a:status>
              <a:prop><d:calendar-free-busy-set/></a:prop>
            </a:propstat>
          </a:response>
          </a:multistatus>
           */
          throw new DavStoreException("We couldn't create the calendar collection, the server have sent : " + resp.getStatusLine().getReasonPhrase());

        } else {

          if (statusCode == HttpStatus.SC_FORBIDDEN) {
            jc = JAXBContext.newInstance("zswi.schemas.caldav.errors");
            Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
            StringReader reader = new StringReader(bodyOfResponse);
            zswi.schemas.caldav.errors.Error error = (zswi.schemas.caldav.errors.Error)userInfounmarshaller.unmarshal(reader);
            if (error.getErrorDescription() != null) {
              throw new DavStoreException(error.getErrorDescription());
            }
            if (error.getResourceMustBeNull() != null) {
              throw new DavStoreException("A resource MUST NOT exist at the Request-URI");
            }
            if (error.getCalendarCollectionLocationOk() != null) {
              throw new DavStoreException("The Request-URI MUST identify a location where a calendar collection can be created");
            }            
            if (error.getValidCalendarData() != null) {
              throw new DavStoreException("The time zone specified in the CALDAV:calendar-timezone property MUST be a valid iCalendar object containing a single valid VTIMEZONE component");
            }
            if (error.getNeedsPrivilege() != null) {
              throw new DavStoreException("The DAV:bind privilege MUST be granted to the current user on the parent collection of the Request-URI.");
            }
            if (error.getInitializeCalendarCollection() != null) {
              throw new DavStoreException("A new calendar collection exists at the Request-URI.");
            }
          }

          throw new DavStoreException("We couldn't create the calendar collection, the server have sent : " + resp.getStatusLine().getReasonPhrase());
        }
      } else {
        EntityUtils.consume(resp.getEntity());
      }
    }
    catch (URISyntaxException e) {
      throw new DavStoreException("Couldn't build a URL for " + httpScheme() + connectionManager.getServerName() + connectionManager.getPort() + "/.well-known/caldav");
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
  }

  // TODO should check if DELETE method is allowed for this collection and throw a different exception if that's the case
  public void deleteCollection(AbstractNotPrincipalCollection collection) throws ClientProtocolException, URISyntaxException, IOException, DavStoreException {
    try {
      DeleteRequest del = new DeleteRequest(initUri(collection.getUri()), collection.getGetetag());
      HttpResponse resp = httpClient().execute(del);

      int statusCode = resp.getStatusLine().getStatusCode();
      if ((statusCode != HttpStatus.SC_NO_CONTENT) && (statusCode != HttpStatus.SC_OK)) {
        String bodyOfResponse = EntityUtils.toString(resp.getEntity());

        EntityUtils.consume(resp.getEntity());      

        if (statusCode == HttpStatus.SC_FORBIDDEN) {
          if ((bodyOfResponse != null) && (bodyOfResponse.length() > 1)) {
            JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.errors");
            Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
            StringReader reader = new StringReader(bodyOfResponse);
            zswi.schemas.caldav.errors.Error error = (zswi.schemas.caldav.errors.Error)userInfounmarshaller.unmarshal(reader);
            if (error.getErrorDescription() != null) {
              throw new DavStoreException(error.getErrorDescription());
            } 
          }
        }

        throw new DavStoreException("We couldn't delete the calendar collection, the server have sent : " + resp.getStatusLine().getReasonPhrase());
      } else {
        EntityUtils.consume(resp.getEntity());
      }   
    }       
    catch (URISyntaxException e) {
      throw new DavStoreException("Couldn't build a URL for " + collection.getUri());
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
  }

  protected void copyOrMoveCalendarToCollection(ServerVCalendar calendar, CalendarCollection destination, boolean isMove) throws DavStoreException {
    HttpEntityEnclosingRequestBase req;

    String[] pathSegments = calendar.getPath().split("/");

    try {
      if (pathSegments.length > 1) {
        String destinationPath = destination.getUri() + pathSegments[pathSegments.length - 1];

        if (isMove) {
          req = new MoveRequest(initUri(calendar.getPath()), destinationPath);
        } else {
          req = new CopyRequest(initUri(calendar.getPath()), destinationPath);
        }

        HttpResponse resp = httpClient().execute(req);

        int statusCode = resp.getStatusLine().getStatusCode();

        EntityUtils.consume(resp.getEntity());      

        if ((statusCode != HttpStatus.SC_CREATED) && (statusCode != HttpStatus.SC_OK) && (statusCode != HttpStatus.SC_NO_CONTENT)) {

          if (statusCode == HttpStatus.SC_FORBIDDEN) {
            throw new DavStoreException("The source and destination URIs are the same.");
          }
          if (statusCode == HttpStatus.SC_CONFLICT) {
            throw new DavStoreException("The server was unable to maintain the liveness of the properties listed in the propertybehavior XML element or the Overwrite header is F and the state of the destination resource is non-null.");
          }
          if (statusCode == HttpStatus.SC_LOCKED) {
            throw new DavStoreException("The source or the destination resource was locked.");
          }
          if (statusCode == HttpStatus.SC_CONFLICT) {
            throw new DavStoreException("This may occur when the destination is on another server and the destination server refuses to accept the resource.");
          }

          throw new DavStoreException("Return status code is " + statusCode);

        } 
      }
    }      
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (ClientProtocolException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }
  }

  public void moveCalendarToCollection(ServerVCalendar calendar, CalendarCollection destination) throws DavStoreException {
    copyOrMoveCalendarToCollection(calendar, destination, true);
  }

  public void copyCalendarToCollection(ServerVCalendar calendar, CalendarCollection destination) throws DavStoreException {
    copyOrMoveCalendarToCollection(calendar, destination, false);
  }

  public static String report(HTTPConnectionManager connectionManager, StringEntity body, String path, int depth) throws DavStoreException, NotImplemented {
    ReportRequest req;
    String response = "";

    try {
      req = new ReportRequest(connectionManager.initUri(path), depth);
      body.setContentType("text/xml");
      req.setEntity(body);

      HttpResponse resp = connectionManager.getHttpClient().execute(req);

      if (resp.getStatusLine().getStatusCode() == 501) {
        EntityUtils.consume(resp.getEntity());
        throw new NotImplemented(resp.getStatusLine().getReasonPhrase());
      } else if (resp.getStatusLine().getStatusCode() >= 400) {
        EntityUtils.consume(resp.getEntity());
        throw new DavStoreException(resp.getStatusLine().getReasonPhrase());
      }

      response += EntityUtils.toString(resp.getEntity());

      EntityUtils.consume(resp.getEntity());
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

    return response;

  }

  public static String report(HTTPConnectionManager connectionManager, StringWriter body, String path, int depth) throws DavStoreException, NotImplemented {
    String response = "";
    StringEntity se;
    try {
      se = new StringEntity(body.toString());
      response = report(connectionManager, se, path, depth);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    return response;
  }

  public static String report(HTTPConnectionManager connectionManager, String filename, String path, int depth) throws DavStoreException, NotImplemented {
    String response = "";
    InputStream is = ClassLoader.getSystemResourceAsStream(filename);
    StringEntity se;
    try {
      se = new StringEntity(Utilities.convertStreamToString(is));
      response = report(connectionManager, se, path, depth);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    return response;
  }

  /**
   * TODO this method should be called each a collection is called, and the features/allowed methods should be stored inside the collection data structure
   * 
   * Get the list of DAV/CalDAV/CardDAV features that this server supports. This is done by looking at 
   * the "DAV" header of the response (done by a OPTIONS request).
   * 
   * @param path URL path to the calendar home set or a calendar collection.
   */
  public void fetchFeatures(AbstractDavCollection collection) {
    try {
      Utilities.fetchFeatures(httpClient(), initUri(collection.getUri()), collection);
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /**
   * Implements free-busy-request (see http://tools.ietf.org/html/rfc4791#section-7.10)
   * 
   * @param uriToCalendarCollection Path to the calendar collection that you want to do a free-busy-query against. Example: /calendars/users/probert/calendar/
   * @param startTime
   * @param endTime
   * @return 
   * @throws DavStoreException
   * @throws DateNotUtc 
   * @throws NotFound 
   */
  public ServerVCalendar getFreeBusyInformation(String uriToCalendarCollection, DateTime startTime, DateTime endTime) throws DavStoreException, DateNotUtc, NotFound {

    if ((!startTime.isUtc()) || (!endTime.isUtc())) {
      throw new DateNotUtc("Dates must be set to UTC");
    }

    StringWriter sw = new StringWriter();

    zswi.schemas.caldav.freebusy.TimeRange timeRange = new zswi.schemas.caldav.freebusy.TimeRange();
    timeRange.setEnd(endTime.toString());
    timeRange.setStart(startTime.toString());
    zswi.schemas.caldav.freebusy.FreeBusyQuery query = new zswi.schemas.caldav.freebusy.FreeBusyQuery();
    query.setTimeRange(timeRange);

    String path = uriToCalendarCollection;

    try {
      JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.freebusy");
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
      marshaller.marshal(query, sw);

      StringEntity se;

      se = new StringEntity(sw.toString());
      ReportRequest req = new ReportRequest(initUri(path), 0);
      se.setContentType("text/xml");
      req.setEntity(se);    

      HttpResponse resp = connectionManager.getHttpClient().execute(req);

      String response = EntityUtils.toString(resp.getEntity());

      int statusCode = resp.getStatusLine().getStatusCode();

      if (statusCode == 200) {
        StringReader sin = new StringReader(response);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendarData = builder.build(sin);

        String eTag = null;
        Header[] eTags = resp.getHeaders("ETag");
        if (eTags.length == 1) {
          eTag = eTags[0].getValue();
        }

        EntityUtils.consume(resp.getEntity());

        ServerVCalendar calendarObject = new ServerVCalendar(calendarData, eTag, null);
        return calendarObject;
      } else {
        EntityUtils.consume(resp.getEntity());
        if (statusCode == HttpStatus.SC_NOT_FOUND) {
          throw new NotFound("Couldn't find requested URI");
        }
        throw new DavStoreException("Couldn't do free-busy-query, server returned a " + statusCode + " code.");
      }
    } 
    catch (IOException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (ParserException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (JAXBException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e.getMessage());
    }
  }

  // TODO Should this method simply returns a empty list if the feature is not supported?
  /**
   * Find other users/resources/groups/rooms by doing a REPORT by type, as defined by CuType (INDIVIDUAL, ROOM, etc.). 
   * The server must support rfc6638 for this to work. 
   * 
   * @param type The type of principals you are looking for. Could be INDIVIDUAL, GROUP, ROOM or RESOURCE.
   * @param displayname Optional. If you wish to find all principals of a give type, pass null as the value.
   * @return
   * @throws DavStoreException
   * @throws NotImplementedException Throw if the server don't support rfc6638 ("calendar-auto-schedule" feature)
   */
  public List<PrincipalCollection> findPrincipalsByType(CuType type, String displayname) throws DavStoreException, NotImplementedException {
    if (!principalCollection().supportedFeatures().contains(DavFeature.CALENDAR_AUTO_SCHEDULE)) {
      throw new NotImplementedException("This CalDAV store don't support the calendar-auto-schedule feature, you cannot find principals by type");
    }

    List<PrincipalCollection> result = new ArrayList<PrincipalCollection>();

    zswi.schemas.caldav.principalSearch.bytype.PrincipalPropertySearch principalPropSearch = new zswi.schemas.caldav.principalSearch.bytype.PrincipalPropertySearch();
    principalPropSearch.setType(type.getValue());

    zswi.schemas.caldav.principalSearch.bytype.Prop userTypeProp = new zswi.schemas.caldav.principalSearch.bytype.Prop();
    userTypeProp.setCalendarUserType(new zswi.schemas.caldav.principalSearch.bytype.CalendarUserType());
    zswi.schemas.caldav.principalSearch.bytype.PropertySearch propType = new zswi.schemas.caldav.principalSearch.bytype.PropertySearch();
    propType.setMatch(type.getValue());
    propType.setProp(userTypeProp);

    principalPropSearch.getPropertySearch().add(propType);

    if ((displayname != null) && (displayname.length() > 0)) {
      zswi.schemas.caldav.principalSearch.bytype.Prop displaynameProp = new zswi.schemas.caldav.principalSearch.bytype.Prop();
      displaynameProp.setDisplayname(new zswi.schemas.caldav.principalSearch.bytype.Displayname());
      zswi.schemas.caldav.principalSearch.bytype.PropertySearch propSearchDisplayname = new zswi.schemas.caldav.principalSearch.bytype.PropertySearch();
      propSearchDisplayname.setMatch(displayname);
      propSearchDisplayname.setProp(displaynameProp);
      principalPropSearch.getPropertySearch().add(propSearchDisplayname);
      principalPropSearch.setTest("allof");
    } else {
      principalPropSearch.setTest("anyof");
    }

    StringWriter sw = new StringWriter();

    String path = principalCollection().getUri();

    try {
      JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.principalSearch.bytype");
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
      marshaller.marshal(principalPropSearch, sw);

      StringEntity se;

      se = new StringEntity(sw.toString());
      ReportRequest req = new ReportRequest(initUri(path), 0);
      se.setContentType("text/xml");
      req.setEntity(se);    

      HttpResponse resp = connectionManager.getHttpClient().execute(req);

      int statusCode = resp.getStatusLine().getStatusCode();

      if (statusCode == HttpStatus.SC_MULTI_STATUS) {
        jc = JAXBContext.newInstance("zswi.schemas.caldav.principalSearch.bytype.response");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        zswi.schemas.caldav.principalSearch.bytype.response.Multistatus unmarshal = (zswi.schemas.caldav.principalSearch.bytype.response.Multistatus)unmarshaller.unmarshal(resp.getEntity().getContent());

        EntityUtils.consume(resp.getEntity());

        for (zswi.schemas.caldav.principalSearch.bytype.response.Response responseElement: unmarshal.getResponse()) {
          String status = responseElement.getStatus();
          if ("HTTP/1.1 200 OK".equals(status)) {
            String uriForPrincipals = responseElement.getHref();
            if (uriForPrincipals != null) {
              PrincipalCollection principals = new PrincipalCollection(this, initUri(uriForPrincipals), false, true);
              if (principals != null) {
                result.add(principals);
              }
            }
          }
        }

        return result;
      } else {
        EntityUtils.consume(resp.getEntity());
        if (statusCode == HttpStatus.SC_OK) {
          return result;
        }
        if (statusCode == HttpStatus.SC_NOT_FOUND) {
          return result;
        }
        throw new DavStoreException("Couldn't return the list of principals, the server returned " + statusCode);
      }
    } 
    catch (IOException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (JAXBException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e.getMessage());
    }
  }

  // FIXME Almost the same code as getVCalendars, should merge them.
  public List<ServerVCalendar> getInvitations() throws DavStoreException {
    ArrayList<ServerVCalendar> result = new ArrayList<ServerVCalendar>();

    InboxCollection inbox = principalCollection().getScheduleInbox();

    if (inbox != null) {
      String path = inbox.getUri();

      String response = "";
      try {
        response = report(connectionManager, "rep_events.txt", path, 1);
      }
      catch (NotImplemented e1) {
        e1.printStackTrace();
      }

      JAXBContext jc;
      try {
        jc = JAXBContext.newInstance("zswi.schemas.dav.icalendarobjects");
        Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
        StringReader reader = new StringReader(response);
        zswi.schemas.dav.icalendarobjects.Multistatus multistatus = (zswi.schemas.dav.icalendarobjects.Multistatus)userInfounmarshaller.unmarshal(reader);

        for (Response xmlResponse: multistatus.getResponse()) {
          String hrefForObject = xmlResponse.getHref();
          for (zswi.schemas.dav.icalendarobjects.Propstat propstat: xmlResponse.getPropstat()) {
            if (PROPSTAT_OK.equals(propstat.getStatus())) {
              StringReader sin = new StringReader(propstat.getProp().getCalendarData());
              CalendarBuilder builder = new CalendarBuilder();
              Calendar calendarData = builder.build(sin);
              ServerVCalendar calendarObject = new ServerVCalendar(calendarData, propstat.getProp().getGetetag(), hrefForObject);
              result.add(calendarObject);
            }
          }
        }
      }
      catch (JAXBException e) {
        throw new DavStoreException(e.getMessage());
      }
      catch (IOException e) {
        throw new DavStoreException(e.getMessage());
      }
      catch (ParserException e) {
        throw new DavStoreException(e.getMessage());
      }
    }

    return result;
  }

  public static class DavStoreException extends Exception {

    public DavStoreException(String reason) {
      super(reason);
    }

    public DavStoreException(Throwable throwable) {
      super(throwable);
    }
  }

  public static class NotImplemented extends Exception {

    public NotImplemented(String reason) {
      super(reason);
    }

    public NotImplemented(Throwable throwable) {
      super(throwable);
    }
  }

  public static class UidConflict extends Exception {

    public UidConflict(String reason) {
      super(reason);
    }

    public UidConflict(Throwable throwable) {
      super(throwable);
    }
  }

  public static class NotSupportedComponent extends Exception {

    public NotSupportedComponent(String reason) {
      super(reason);
    }

    public NotSupportedComponent(Throwable throwable) {
      super(throwable);
    }
  }

  public static class NoRedirectFoundException extends Exception {

    public NoRedirectFoundException(String reason) {
      super(reason);
    }

    public NoRedirectFoundException(Throwable throwable) {
      super(throwable);
    }
  }

  public static class DateNotUtc extends Exception {

    public DateNotUtc(String reason) {
      super(reason);
    }

    public DateNotUtc(Throwable throwable) {
      super(throwable);
    }
  }

  public static class NotFound extends Exception {

    public NotFound(String reason) {
      super(reason);
    }

    public NotFound(Throwable throwable) {
      super(throwable);
    }
  }

}
