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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import zswi.objects.dav.collections.AddressBookCollection;
import zswi.objects.dav.collections.AddressBookHomeSet;
import zswi.objects.dav.collections.CalendarCollection;
import zswi.objects.dav.collections.CalendarHomeSet;
import zswi.objects.dav.collections.PrincipalCollection;
import zswi.objects.dav.enums.DavFeature;
import zswi.protocols.caldav.ServerVCalendar;
import zswi.protocols.caldav.ServerVEvent;
import zswi.protocols.carddav.ServerVCard;
import zswi.protocols.communication.core.requests.DeleteRequest;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.PutRequest;
import zswi.protocols.communication.core.requests.ReportRequest;
import zswi.protocols.communication.core.requests.UpdateRequest;
import zswi.schemas.carddav.multiget.AddressbookMultiget;
import zswi.schemas.carddav.multiget.ObjectFactory;
import zswi.schemas.dav.discovery.PrincipalURL;
import zswi.schemas.dav.icalendarobjects.Response;

/**
 * Connect to a CalDAV/CardDAV server by auto-discovery or by a specific URL.
 * 
 * @author Pascal Robert
 *
 */
public class DavStore {

  protected String _username;
  protected String _password;
  protected String _domain;
  protected String _serverName;
  protected String _path;
  protected Integer _port;
  protected DefaultHttpClient _httpClient;
  protected boolean _isSecure;
  protected ArrayList<DavFeature> _supportedFeatures;
  protected HttpHost _targetHost;
  private PrincipalCollection _principalCollection;
  public static final String PROPSTAT_OK = "HTTP/1.1 200 OK";
  public static final String TYPE_CALENDAR = "text/calendar; charset=utf-8";
  public static final String TYPE_VCARD = "text/vcard; charset=utf-8";
  
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
    _supportedFeatures = new ArrayList<DavFeature>();
    _username = username;
    _password = password;
    _isSecure = false;
    extractUserDetails(username);
    try {
      checkSrvRecords();
    } catch (NamingException namingEx) {
      throw new DavStoreException("Couldn't find SRV records for the host " + _serverName);
    }
    try {
      checkTxtRecords();
    } catch (NamingException namingEx) {
      logger.info("We didn't find any TXT records, will try well_know URLs");
      try {
        checkWellKnownUrl();
      } catch (NoRedirectFoundException e) {
        logger.info(e.getMessage());
      }
    } 
    
    /* After we have found the well known URL, we clear the current httpClient. Why? Because the host returned by the well known URL might be different. */
    _httpClient.getConnectionManager().shutdown();
    _httpClient = null;
    
    fetchPrincipalsCollection("/");
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

    _username = username;
    _password = password;
    _isSecure = false;
    extractUserDetails(url);

    try {
      _isSecure = (url.startsWith("https")) ? true : false;
      checkWellKnownUrl();
    }
    catch (NoRedirectFoundException e) {
      e.printStackTrace();
    } finally {
      _httpClient.getConnectionManager().shutdown();
      _httpClient = null;
    }
    
    try {
      rootUrl = new URL(url);
      _isSecure = (rootUrl.getProtocol().equals("https")) ? true : false;
    }
    catch (MalformedURLException e) {
      throw new DavStoreException(e);
    }
    
    fetchPrincipalsCollection(_path);
  }

  /**
   * This method will extract the username and domain from a HTTP(s) URL or an email address.
   * 
   * @param urlAsString
   */
  protected void extractUserDetails(String urlAsString) {
    try {
      URL urlForUser = new URL(urlAsString);
      _domain = urlForUser.getHost();
      _serverName = _domain;
      _path = urlForUser.getPath();
      if (urlForUser.getUserInfo() != null) {
        _username = urlForUser.getUserInfo();
      }
      _port = urlForUser.getPort();
    }
    catch (MalformedURLException e) {
      logger.warning("Couldn't transform the username to a URL : " + e.getMessage());
      if (urlAsString.contains("@")) {
        String[] emailComponents = urlAsString.split("@");
        if (emailComponents.length == 2) {
          _username = emailComponents[0];
          _domain = emailComponents[1];
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
      results = DNSUtilities.doSRVLookup("_caldavs._tcp." + _domain);
      _isSecure = true;
    }
    catch (NamingException e) {
      results = DNSUtilities.doSRVLookup("_caldav._tcp." + _domain);
    }    
    _port  = Integer.valueOf(results.get("port"));
    _serverName = results.get("host");
  }

  /**
   * Check if we can find a URL to the user's DAV principals in a TXT DNS record, with the URL in a "path" key.
   * 
   * @throws NamingException
   */
  protected void checkTxtRecords() throws NamingException {
    HashMap<String, String> results = new HashMap<String, String>();
    if (_isSecure) {
      results = DNSUtilities.doTXTLookup("_caldavs._tcp." + _domain);
    } else {
      results = DNSUtilities.doTXTLookup("_caldav._tcp." + _domain);
    }
    _path = results.get("path");
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
      URL urlForRequest = new URL(httpScheme(), _serverName, _port, "/.well-known/caldav");
      req = new PropfindRequest(urlForRequest.toURI(), 0);
      InputStream is = ClassLoader.getSystemResourceAsStream("well-known-request.xml");

      StringEntity se = new StringEntity(convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse response = httpClient().execute(req);
      int status = response.getStatusLine().getStatusCode();
      if (status >= 300 && status < 400) {
        Header[] location = response.getHeaders("Location");
        String urlFromHeader = location[0].getValue();
        URL rootUrl;
        // Kerio returns a relative URL
        try {
          rootUrl = new URL(urlFromHeader);
        } catch (MalformedURLException e) {
          rootUrl = initUri(urlFromHeader).toURL();
        }
        _path = rootUrl.getPath();
        _isSecure = (rootUrl.getProtocol().equals("https")) ? true : false;
        _serverName = rootUrl.getHost();
      } else {
        throw new NoRedirectFoundException("No redirection found");
      }

      EntityUtils.consume(response.getEntity());
    }
    catch (URISyntaxException e) {
      throw new DavStoreException("Couldn't build a URL for " + httpScheme() + _serverName +  _port + "/.well-known/caldav");
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
  protected void fetchPrincipalsCollection(String path) throws DavStoreException {
    PropfindRequest req;
    try {
      URI urlForRequest = initUri(path);
      req = new PropfindRequest(urlForRequest, 0);
      InputStream is = ClassLoader.getSystemResourceAsStream("well-known-request.xml");

      StringEntity se = new StringEntity(convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = httpClient().execute(req);

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
        CalendarHomeSet calHomeSet = new CalendarHomeSet(httpClient(), principals, initUri(principals.getUri()));
        fetchFeatures(calHomeSet.getUri());
        AddressBookHomeSet addressbookHomeSet = new AddressBookHomeSet(httpClient(), principals, initUri(principals.getUri()));
        _principalCollection = calHomeSet.getOwner();
      } else {
        PrincipalCollection principals = new PrincipalCollection(this, initUri(currentUserPrincipal), false, true);
        CalendarHomeSet calHomeSet = new CalendarHomeSet(httpClient(), principals, initUri(principals.getCalendarHomeSetUrl().getPath()));
        fetchFeatures(calHomeSet.getUri());
        AddressBookHomeSet addressbookHomeSet = new AddressBookHomeSet(httpClient(), principals, initUri(principals.getAddressbookHomeSetUrl().getPath()));
        _principalCollection = calHomeSet.getOwner();
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
    return Utilities.initUri(path, httpScheme(), _serverName, _port, _username, _password);
  }

  public String convertStreamToString(java.io.InputStream is) {
    return Utilities.convertStreamToString(is);
  } 

  protected String httpScheme() {
    return (_isSecure) ? "https" : "http";
  }

  public DefaultHttpClient httpClient() {
    if (_httpClient == null) {

      _httpClient = new DefaultHttpClient();

      if (_isSecure) {
        X509TrustManager tm = new X509TrustManager() {

          public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
          }

          public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
          }

          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }
        };

        SSLContext ctx;
        try {
          ctx = SSLContext.getInstance("TLS");
          ctx.init(null, new TrustManager[]{tm}, null);
          ClientConnectionManager ccm = _httpClient.getConnectionManager();
          SchemeRegistry sr = ccm.getSchemeRegistry();
          SSLSocketFactory ssf = new SSLSocketFactory(ctx);
          sr.register(new Scheme("https", ssf, 443));
          _httpClient = new DefaultHttpClient(ccm);
        }
        catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
        }
        catch (KeyManagementException e) {
          e.printStackTrace();
        }
      }

      _targetHost = new HttpHost(_serverName, _port, (_isSecure) ? "https" : "http");

      _httpClient.getCredentialsProvider().setCredentials(new AuthScope(_targetHost.getHostName(), _targetHost.getPort()), new UsernamePasswordCredentials(_username, _password));
      _httpClient.setRedirectStrategy(new LaxRedirectStrategy());
      AuthCache authCache = new BasicAuthCache();
      DigestScheme digestAuth = new DigestScheme();
      digestAuth.overrideParamter("realm", "whatever");
      digestAuth.overrideParamter("nonce", "whatever");
      authCache.put(_targetHost, digestAuth);

      BasicHttpContext localcontext = new BasicHttpContext();
      localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
    }
    return _httpClient;
  }

  /**
   * 
   * @return A list of enums that details which DAV/CalDAV/CardDAV features this server implements.
   */
  public ArrayList<DavFeature> supportedFeatures() {
    return _supportedFeatures;
  }

  /**
   * Get the list of DAV/CalDAV/CardDAV features that this server supports. This is done by looking at 
   * the "DAV" header of the response (done by a OPTIONS request).
   * 
   * @param path URL path to the calendar home set or a calendar collection.
   */
  public void fetchFeatures(String path) {
    _supportedFeatures = new ArrayList<DavFeature>();
    try {
      HttpOptions headersMethod = new HttpOptions(new URL(httpScheme(), _serverName, _port, _path).toURI());
      HttpResponse response = httpClient().execute(headersMethod);
      Header[] davHeaders = response.getHeaders("DAV");
      EntityUtils.consume(response.getEntity());
      
      for (int davIndex = 0; davIndex < davHeaders.length; davIndex++) {
        Header header = davHeaders[davIndex];
        String[] featuresAsString = header.getValue().split(",");
        for (int featureIndex = 0; featureIndex < featuresAsString.length; featureIndex++) {
          DavFeature feature = DavFeature.getByFeatureName(featuresAsString[featureIndex].trim());
          _supportedFeatures.add(feature);
        }
      }
    }
    catch (ClientProtocolException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * @deprecated Use getVCalendars instead
   * 
   */
  public List<ServerVEvent> getVEvents(CalendarCollection calendar) throws DavStoreException {

    ArrayList<ServerVEvent> result = new ArrayList<ServerVEvent>();
    List<ServerVCalendar> calendarObjects = getVCalendars(calendar);
    
    for (ServerVCalendar vCalendar: calendarObjects) {
      Object event = vCalendar.getVCalendar().getComponent(Component.VEVENT);
      if (event != null) {
        ServerVEvent calendarObject = new ServerVEvent((VEvent)event, vCalendar.geteTag(), vCalendar.getPath());
        result.add(calendarObject);
      }
    }
    
    return result;
  }
  
  /**
   * Get the list of all iCalendar objects from a calendar collection.
   * 
   * @param calendar
   * @return
   * @throws DavStoreException
   */
  public List<ServerVCalendar> getVCalendars(CalendarCollection calendar) throws DavStoreException {
    ArrayList<ServerVCalendar> result = new ArrayList<ServerVCalendar>();
    
    String path = calendar.getUri();

    String response = "";
    try {
      response = this.report("rep_events.txt", path, 1);
    }
    catch (NotImplemented e1) {
      // TODO if it fails with 501 Implemented, it should try to fetch the vCards with a PROPFIND followed by a calendar-multiget
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

    return result;
  }

  
  /**
   * @deprecated You should use addVCalendar instead
   * @param collection
   * @param event
   * @return
   * @throws DavStoreException
   */
  public ServerVEvent addVEvent(CalendarCollection collection, VEvent event) throws DavStoreException {
    Calendar calendarForEvent = new Calendar();
    calendarForEvent.getComponents().add(event);
    
    ServerVCalendar vCalendar = addVCalendar(collection,calendarForEvent);
    return new ServerVEvent(event, vCalendar.geteTag(), vCalendar.getPath());
  }
  
  /**
   * TODO It should verify if the component type is accepted for the collection
   * TODO It should raise only one type of exception
   * TODO It should check if all components have the same Uid, or else it should be rejected
   * TODO It should reject the object if the base properties (SUMMARY, DTSTART, etc.) are not present
   * 
   * @param collection
   * @param calendar
   * @return
   * @throws DavStoreException
   */
  public ServerVCalendar addVCalendar(CalendarCollection collection, Calendar calendar) throws DavStoreException {
    StringEntity se;
    try {
      se = new StringEntity(calendar.toString());
      se.setContentType(TYPE_CALENDAR);

      Component calComponent = (Component)calendar.getComponents().get(0);
      String uid = calComponent.getProperty(Property.UID).getValue();
      
      URI urlForRequest = initUri(collection.getUri() + uid + ".ics");
      PutRequest putReq = new PutRequest(urlForRequest);
      putReq.setEntity(se);
      
      HttpResponse resp = httpClient().execute(putReq);
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
        
        ServerVCalendar vcalendar = new ServerVCalendar(calendar, etag, path);
        // TODO if Location and ETag are not present, we should do a PROPFIND at the same URL to get the value of the getetag property
        return vcalendar;
      } else {
        throw new DavStoreException("Can't create the calendar object, returned status code is " + resp.getStatusLine().getStatusCode());
      }
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }
  }
  
  /**
   * @deprecated Use updateVCalendar instead
   * @param event
   * @return
   * @throws DavStoreException
   */
  public void updateVEvent(ServerVEvent event) throws DavStoreException {
    // vEvent must be enclosed in Calendar, otherwise is not added
    Calendar calendarForEvent = new Calendar();
    calendarForEvent.getComponents().add(event.getVevent());

    StringEntity se = null;
    try {
      se = new StringEntity(calendarForEvent.toString());
      se.setContentType(TYPE_CALENDAR);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }

    String newEtag = this.updateObject(se, event.geteTag(), event.getPath());
    if (newEtag != null) {
      event.seteTag(newEtag);
    }
  }
  
  /**
   * Update an iCalendar object by pushing it (PUT request) to the server.
   * 
   * @param calendar
   * @throws DavStoreException
   */
  public void updateVCalendar(ServerVCalendar calendar) throws DavStoreException {
    // vEvent must be enclosed in Calendar, otherwise is not added
    StringEntity se = null;
    try {
      se = new StringEntity(calendar.getVCalendar().toString());
      se.setContentType(TYPE_CALENDAR);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }

    String newEtag = this.updateObject(se, calendar.geteTag(), calendar.getPath());
    if (newEtag != null) {
      calendar.seteTag(newEtag);
    }
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
      reportResponse = this.report("addressbook-query-request.xml", collection.getUri(), 1);
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
            if (PROPSTAT_OK.equals(propstat.getStatus())) {
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
      req = new PropfindRequest(initUri(collection.getUri()), 1);
      InputStream is = ClassLoader.getSystemResourceAsStream("propfind-etag-request.xml");

      StringEntity se = new StringEntity(convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse response = httpClient().execute(req);
      int status = response.getStatusLine().getStatusCode();
      if (status < 300) {
        JAXBContext jc = JAXBContext.newInstance("zswi.schemas.dav.propfind.etag");
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        zswi.schemas.dav.propfind.etag.Multistatus unmarshal = (zswi.schemas.dav.propfind.etag.Multistatus)unmarshaller.unmarshal(response.getEntity().getContent());

        EntityUtils.consume(response.getEntity());
        
        List<zswi.schemas.dav.propfind.etag.Response> responses = unmarshal.getResponse();
        for (zswi.schemas.dav.propfind.etag.Response xmlResponse: responses) {
          String href = xmlResponse.getHref();
          if (PROPSTAT_OK.equals(xmlResponse.getPropstat().getStatus())) {
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
      throw new DavStoreException("Couldn't build a URL for " + httpScheme() + _serverName +  _port + "/.well-known/caldav");
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
          
      String response = this.report(sw, collection.getUri(), 1);
      
      jc = JAXBContext.newInstance("zswi.schemas.carddav.multiget.response");
      Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
      StringReader reader = new StringReader(response);
      zswi.schemas.carddav.multiget.response.Multistatus multistatus = (zswi.schemas.carddav.multiget.response.Multistatus)userInfounmarshaller.unmarshal(reader);
      for (zswi.schemas.carddav.multiget.response.Response xmlResponse: multistatus.getResponse()) {
        String hrefForObject = xmlResponse.getHref();
        for (zswi.schemas.carddav.multiget.response.Propstat propstat: xmlResponse.getPropstat()) {
          if (PROPSTAT_OK.equals(propstat.getStatus())) {
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
      se.setContentType(TYPE_VCARD);
      
      URI urlForRequest = initUri(collection.getUri() + card.getUID().getUID() + ".ics");
      PutRequest putReq = new PutRequest(urlForRequest);
      putReq.setEntity(se);
      HttpResponse resp = httpClient().execute(putReq);
      
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
      se.setContentType(TYPE_VCARD);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }

    String newEtag = this.updateObject(se, card.geteTag(), card.getPath());
    if (newEtag != null) {
      card.seteTag(newEtag);
    }
  }
  
  protected String updateObject(HttpEntity entity, String etag, String path) throws DavStoreException {
    UpdateRequest updateReq;
    try {
      updateReq = new UpdateRequest(initUri(path), etag);
      updateReq.setEntity(entity);
      HttpResponse resp = httpClient().execute(updateReq);

      EntityUtils.consume(resp.getEntity());
      
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
        Header[] headers = resp.getHeaders("Etag");
        if (headers.length == 1) {
          return headers[0].getValue();
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
    return null;
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
    return this.delete(card.getPath(), card.geteTag());
  }
  
  /**
   * @deprecated Use deleteVCalendar instead
   */
  public boolean deleteVEvent(ServerVEvent event) throws ClientProtocolException, URISyntaxException, IOException {
    return this.delete(event.getPath(), event.geteTag());
  }
  
  /**
   * Delete a iCalendar object by doing a DELETE request on the path of the iCalendar object on the server.
   * 
   * @param calendar
   * @return
   * @throws ClientProtocolException
   * @throws URISyntaxException
   * @throws IOException
   */
  public boolean deleteVCalendar(ServerVCalendar calendar) throws ClientProtocolException, URISyntaxException, IOException {
    return this.delete(calendar.getPath(), calendar.geteTag());
  }
  
  protected boolean delete(String path, String etag) throws URISyntaxException, ClientProtocolException, IOException{
    DeleteRequest del = new DeleteRequest(initUri(path), etag);
    HttpResponse resp = httpClient().execute(del);
    EntityUtils.consume(resp.getEntity());
    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) return true;
    else return false;
  }
  
  protected String report(StringEntity body, String path, int depth) throws DavStoreException, NotImplemented {
    ReportRequest req;
    String response = "";

    try {
      req = new ReportRequest(initUri(path), depth);
      body.setContentType("text/xml");
      req.setEntity(body);

      HttpResponse resp = _httpClient.execute(req);

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
  
  protected String report(StringWriter body, String path, int depth) throws DavStoreException, NotImplemented {
    String response = "";
    StringEntity se;
    try {
      se = new StringEntity(body.toString());
      response = report(se, path, depth);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    return response;
  }
  
  protected String report(String filename, String path, int depth) throws DavStoreException, NotImplemented {
    String response = "";
    InputStream is = ClassLoader.getSystemResourceAsStream(filename);
    StringEntity se;
    try {
      se = new StringEntity(convertStreamToString(is));
      response = report(se, path, depth);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    return response;
  }
  
  public class DavStoreException extends Exception {
    
    public DavStoreException(String reason) {
      super(reason);
    }
    
    public DavStoreException(Throwable throwable) {
      super(throwable);
    }
  }
  
  public class NotImplemented extends Exception {
    
    public NotImplemented(String reason) {
      super(reason);
    }
    
    public NotImplemented(Throwable throwable) {
      super(throwable);
    }
  }
  
  public class NoRedirectFoundException extends Exception {
    
    public NoRedirectFoundException(String reason) {
      super(reason);
    }
    
    public NoRedirectFoundException(Throwable throwable) {
      super(throwable);
    }
  }

}
