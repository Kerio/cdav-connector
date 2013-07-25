package zswi.protocols.communication.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
import javax.xml.bind.Unmarshaller;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;

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

import zswi.objects.dav.collections.CalendarCollection;
import zswi.objects.dav.collections.CalendarHomeSet;
import zswi.objects.dav.collections.PrincipalCollection;
import zswi.objects.dav.enums.DavFeature;
import zswi.protocols.caldav.ServerVCalendar;
import zswi.protocols.caldav.ServerVEvent;
import zswi.protocols.communication.core.requests.DeleteRequest;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.PutRequest;
import zswi.protocols.communication.core.requests.ReportRequest;
import zswi.protocols.communication.core.requests.UpdateRequest;
import zswi.schemas.dav.discovery.PrincipalURL;
import zswi.schemas.dav.icalendarobjects.Response;

/**
 * Connect to a CalDAV/CardDAV server by auto-discovery
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
  
  static final Logger logger = Logger.getLogger(DavStore.class.getName());
  
  static {     
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
  }

  /**
   * Connect to a DAV store with credentials. The username must be an email address or a fully qualified http(s) URL, 
   * because the domain will be extracted from the email address or from the HTTP(S) URL.
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

  protected void checkTxtRecords() throws NamingException {
    HashMap<String, String> results = new HashMap<String, String>();
    if (_isSecure) {
      results = DNSUtilities.doTXTLookup("_caldavs._tcp." + _domain);
    } else {
      results = DNSUtilities.doTXTLookup("_caldav._tcp." + _domain);
    }
    _path = results.get("path");
  }

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
        _principalCollection = calHomeSet.getOwner();
      } else {
        PrincipalCollection principals = new PrincipalCollection(this, initUri(currentUserPrincipal), false, true);
        CalendarHomeSet calHomeSet = new CalendarHomeSet(httpClient(), principals, initUri(principals.getCalendarHomeSetUrl().getPath()));
        fetchFeatures(calHomeSet.getUri());
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

  public ArrayList<DavFeature> supportedFeatures() {
    return _supportedFeatures;
  }

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
  
  public List<ServerVCalendar> getVCalendars(CalendarCollection calendar) throws DavStoreException {
    ArrayList<ServerVCalendar> result = new ArrayList<ServerVCalendar>();
    
    String path = calendar.getUri();

    String response = this.report("rep_events.txt", path, 1);

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
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
        Header[] headers = resp.getHeaders("Etag");
        String etag = "";
        if (headers.length == 1) {
          etag = headers[0].getValue();
        }
        ServerVCalendar vcalendar = new ServerVCalendar(calendar, etag, urlForRequest.getPath());
        // TODO should check if the Location header is present, if yes, should update the path with that value
        // TODO if Location is not present, we should do a PROPFIND at the same URL to get the value of the getetag property
        return vcalendar;
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
   * @deprecated Use updateVCalendar instead
   * @param event
   * @return
   * @throws DavStoreException
   */
  public boolean updateVEvent(ServerVEvent event) throws DavStoreException {
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

    return this.updateVCard(se, event.geteTag(), event.getPath());
  }
  
  public boolean updateVCalendar(ServerVCalendar calendar) throws DavStoreException {
    // vEvent must be enclosed in Calendar, otherwise is not added
    StringEntity se = null;
    try {
      se = new StringEntity(calendar.getVCalendar().toString());
      se.setContentType(TYPE_CALENDAR);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }

    return this.updateVCard(se, calendar.geteTag(), calendar.getPath());
  }
  
  protected boolean updateVCard(HttpEntity entity, String etag, String path) throws DavStoreException {
    UpdateRequest updateReq;
    try {
      updateReq = new UpdateRequest(initUri(path), etag);
      updateReq.setEntity(entity);
      HttpResponse resp = httpClient().execute(updateReq);

      EntityUtils.consume(resp.getEntity());
      if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) return true;
      else return false;
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
  
  /**
   * @deprecated Use deleteVCalendar instead
   */
  public boolean deleteVEvent(ServerVEvent event) throws ClientProtocolException, URISyntaxException, IOException {
    return this.delete(event.getPath(), event.geteTag());
  }
  
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
  
  protected String report(String filename, String path, int depth) throws DavStoreException {
    ReportRequest req;
    String response = "";

    try {
      req = new ReportRequest(initUri(path), depth);
      InputStream is = ClassLoader.getSystemResourceAsStream(filename);

      StringEntity se = new StringEntity(convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = _httpClient.execute(req);

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
  
  public class DavStoreException extends Exception {
    
    public DavStoreException(String reason) {
      super(reason);
    }
    
    public DavStoreException(Throwable throwable) {
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
