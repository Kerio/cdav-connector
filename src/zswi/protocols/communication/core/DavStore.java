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
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.params.AuthPolicy;
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

import zswi.objects.dav.collections.AbstractDavCollection;
import zswi.objects.dav.collections.AbstractNotPrincipalCollection;
import zswi.objects.dav.collections.AddressBookCollection;
import zswi.objects.dav.collections.AddressBookHomeSet;
import zswi.objects.dav.collections.CalendarCollection;
import zswi.objects.dav.collections.CalendarHomeSet;
import zswi.objects.dav.collections.PrincipalCollection;
import zswi.objects.dav.enums.DavFeature;
import zswi.protocols.caldav.ServerVCalendar;
import zswi.protocols.caldav.ServerVEvent;
import zswi.protocols.carddav.ServerVCard;
import zswi.protocols.communication.core.requests.CopyRequest;
import zswi.protocols.communication.core.requests.DeleteRequest;
import zswi.protocols.communication.core.requests.MkCalendarRequest;
import zswi.protocols.communication.core.requests.MoveRequest;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.ProppatchRequest;
import zswi.protocols.communication.core.requests.PutRequest;
import zswi.protocols.communication.core.requests.ReportRequest;
import zswi.protocols.communication.core.requests.UpdateRequest;
import zswi.schemas.caldav.mkcalendar.Mkcalendar;
import zswi.schemas.caldav.principalSearch.bytype.AutoSchedule;
import zswi.schemas.caldav.principalSearch.bytype.CalendarUserType;
import zswi.schemas.caldav.principalSearch.bytype.Displayname;
import zswi.schemas.caldav.principalSearch.bytype.Prop;
import zswi.schemas.caldav.principalSearch.bytype.PropertySearch;
import zswi.schemas.caldav.principalSearch.bytype.RecordType;
import zswi.schemas.caldav.proppatch.ScheduleCalendarTransp;
import zswi.schemas.caldav.query.CalendarQuery;
import zswi.schemas.carddav.multiget.AddressbookMultiget;
import zswi.schemas.carddav.multiget.ObjectFactory;
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
    
    fetchPrincipalsCollection("/", false);
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
    
    fetchPrincipalsCollection(_path, false);
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
  protected void fetchPrincipalsCollection(String path, boolean secondTry) throws DavStoreException {
    PropfindRequest req;
    try {
      URI urlForRequest = initUri(path);
      req = new PropfindRequest(urlForRequest, 0);
      InputStream is = ClassLoader.getSystemResourceAsStream("well-known-request.xml");

      StringEntity se = new StringEntity(convertStreamToString(is));

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
          CalendarHomeSet calHomeSet = new CalendarHomeSet(httpClient(), principals, initUri(principals.getUri()));
          fetchFeatures(calHomeSet);
          AddressBookHomeSet addressBookSet = new AddressBookHomeSet(httpClient(), principals, initUri(principals.getUri()));
          fetchFeatures(addressBookSet);
          _principalCollection = calHomeSet.getOwner();
        } else {
          PrincipalCollection principals = new PrincipalCollection(this, initUri(currentUserPrincipal), false, true);
          if (principals.getCalendarHomeSetUrl() != null) {
            CalendarHomeSet calHomeSet = new CalendarHomeSet(httpClient(), principals, initUri(principals.getCalendarHomeSetUrl().getPath()));
            fetchFeatures(calHomeSet);
            _principalCollection = calHomeSet.getOwner();
          }
          if (principals.getAddressbookHomeSetUrl() != null) {
            AddressBookHomeSet addressBookSet = new AddressBookHomeSet(httpClient(), principals, initUri(principals.getAddressbookHomeSetUrl().getPath()));
            fetchFeatures(addressBookSet);
          }
        }      
      } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
        // The problem might be that the server is requesting an username in the user@domain.com format (iCloud is an example). 
        // So let's again with this format.
        if (!secondTry) {
          EntityUtils.consume(resp.getEntity());
          _username = _username + "@" + _domain;
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

      List<String> authpref = new ArrayList<String>();
      authpref.add(AuthPolicy.DIGEST);
      authpref.add(AuthPolicy.BASIC);
      _httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
      
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
   * @throws NotSupportedComponent 
   * @throws UidConflict 
   * @throws ValidationException 
   */
  public ServerVEvent addVEvent(CalendarCollection collection, VEvent event) throws DavStoreException, UidConflict, NotSupportedComponent, ValidationException {
    Calendar calendarForEvent = new Calendar();
    calendarForEvent.getComponents().add(event);
    
    ServerVCalendar vCalendar = addVCalendar(collection, calendarForEvent);
    return new ServerVEvent(event, vCalendar.geteTag(), vCalendar.getPath());
  }
  
  /**
   * 
   * @param collection
   * @param calendar
   * @return
   * @throws DavStoreException
   * @throws UidConflict 
   * @throws NotSupportedComponent 
   * @throws ValidationException 
   */
  public ServerVCalendar addVCalendar(CalendarCollection collection, Calendar calendar) throws DavStoreException, UidConflict, NotSupportedComponent, ValidationException {
    StringEntity se;
    try {
      se = new StringEntity(calendar.toString());
      se.setContentType(TYPE_CALENDAR);
      
      String uid = Utilities.checkComponentsValidity(collection, calendar);
      
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
        
        ServerVCalendar vcalendar = new ServerVCalendar(calendar, etag, path, collection);
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
   * @throws ValidationException 
   * @throws UidConflict 
   * @throws NotSupportedComponent 
   */
  public void updateVCalendar(ServerVCalendar calendar) throws DavStoreException, NotSupportedComponent, UidConflict, ValidationException {
    // vEvent must be enclosed in Calendar, otherwise is not added

    Utilities.checkComponentsValidity(calendar.getParentCollection(), calendar.getVCalendar());

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

      HttpResponse resp = _httpClient.execute(req);

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
  
  public List<ServerVCalendar> doCalendarQuery(CalendarCollection collection, CalendarQuery query) throws JAXBException, DavStoreException, NotImplemented {
    StringWriter sw = new StringWriter();
    List<ServerVCalendar> result = new ArrayList<ServerVCalendar>();
    
    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.query");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
    marshaller.marshal(query, sw);
        
    StringEntity se;
    try {
      se = new StringEntity(sw.toString());
      String bodyOfResponse = "";
      ReportRequest req = new ReportRequest(initUri(collection.getUri()), 0);
      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = _httpClient.execute(req);

      bodyOfResponse += EntityUtils.toString(resp.getEntity());

      int statusCode = resp.getStatusLine().getStatusCode();

      if (statusCode == HttpStatus.SC_MULTI_STATUS) {
        jc = JAXBContext.newInstance("zswi.schemas.caldav.query.response");
        Unmarshaller userInfounmarshaller = jc.createUnmarshaller();

        zswi.schemas.caldav.query.response.Multistatus multistatus = (zswi.schemas.caldav.query.response.Multistatus)userInfounmarshaller.unmarshal(new StringReader(bodyOfResponse));
        EntityUtils.consume(resp.getEntity());

        for (zswi.schemas.caldav.query.response.Response response: multistatus.getResponse()) {
          String href = response.getHref();
          for (zswi.schemas.caldav.query.response.Propstat propstat: response.getPropstat()) {
            if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
              String eTag = propstat.getProp().getGetetag();
              
              StringReader sin = new StringReader(propstat.getProp().getCalendarData());
              CalendarBuilder builder = new CalendarBuilder();
              Calendar calendarData = builder.build(sin);
              
              ServerVCalendar calendarObject = new ServerVCalendar(calendarData, eTag, href, collection);
              result.add(calendarObject);
            }
          }
        } 
      } else {
        if (statusCode == HttpStatus.SC_FORBIDDEN) {
          jc = JAXBContext.newInstance("zswi.schemas.caldav.errors");
          Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
          StringReader reader = new StringReader(bodyOfResponse);
          zswi.schemas.caldav.errors.Error error = (zswi.schemas.caldav.errors.Error)userInfounmarshaller.unmarshal(reader);

          EntityUtils.consume(resp.getEntity());
          
          if (error.getErrorDescription() != null) {
            throw new DavStoreException(error.getErrorDescription());
          }
          if (error.getSupportedCalendarData() != null) {
            throw new DavStoreException("The attributes \"content-type\" and \"version\" of the CALDAV:calendar-data XML element specify a media type supported by the server for calendar object resources.");
          }
          if (error.getValidFilter() != null) {
            throw new DavStoreException("The CALDAV:filter XML element specified in the REPORT request MUST be valid.");
          }            
          if (error.getValidCalendarData() != null) {
            throw new DavStoreException("The time zone specified in the REPORT request MUST be a valid iCalendar object containing a single valid VTIMEZONE component.");
          }
          if (error.getMinDateTime() != null) {
            throw new DavStoreException("The time-range values are greater than or equal to the value of the CALDAV:min-date-time property of the collection.");
          }
          if (error.getMaxDateTime() != null) {
            throw new DavStoreException("The time-range values are less than or equal to the value of the CALDAV:max-date-time property of the collection.");
          }
          if (error.getSupportedCollation() != null) {
            throw new DavStoreException("Any XML attribute specifying a collation MUST specify a collation supported by the server as described in Section 7.5");
          }
          if (error.getNumberOfMatchesWithinLimits() != null) {
            throw new DavStoreException("The number of matching calendar object resources must fall within server-specific, predefined limits.");
          }
          // TODO supported-filter is missing from the list.
        }
        
        EntityUtils.consume(resp.getEntity());
        throw new DavStoreException("We couldn't create the calendar collection, the server have sent : " + resp.getStatusLine().getReasonPhrase());
      } 
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (ParseException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }
    catch (ParserException e) {
      throw new DavStoreException(e);
    }
    
    return result;
  }
  
  /**
   * This method will do a calendar-query for the specified time range. It's a common query that many people do.
   * 
   * @param collection
   * @param startTime
   * @param endTime
   * @return
   * @throws DateNotUtc This exception will be raised if the start or end datetime are not set in UTC (date.setUtc(true))
   */
  public List<ServerVCalendar> getEventsForTimePeriod(CalendarCollection collection, DateTime startTime, DateTime endTime) throws DateNotUtc {
    
    if ((!startTime.isUtc()) || (!endTime.isUtc())) {
      throw new DateNotUtc("Dates must be set to UTC");
    }
    
    List<ServerVCalendar> result = new ArrayList<ServerVCalendar>();
    
    zswi.schemas.caldav.query.Cprop compSummary = new zswi.schemas.caldav.query.Cprop();
    compSummary.setName("SUMMARY");
    
    zswi.schemas.caldav.query.Cprop compUid = new zswi.schemas.caldav.query.Cprop();
    compUid.setName("UID");
    
    zswi.schemas.caldav.query.Comp compVevent = new zswi.schemas.caldav.query.Comp();
    compVevent.setName(Component.VEVENT);
    compVevent.getCompOrCprop().add(compSummary);
    compVevent.getCompOrCprop().add(compUid);
    
    zswi.schemas.caldav.query.Cprop version = new zswi.schemas.caldav.query.Cprop();
    version.setName("VERSION");
    
    zswi.schemas.caldav.query.Comp comp = new zswi.schemas.caldav.query.Comp();
    comp.setName("VCALENDAR");
    comp.getCompOrCprop().add(version);
    comp.getCompOrCprop().add(compVevent);
    
    zswi.schemas.caldav.query.CalendarData calData = new zswi.schemas.caldav.query.CalendarData();
    calData.setComp(comp);
    
    zswi.schemas.caldav.query.Prop prop = new zswi.schemas.caldav.query.Prop();
    prop.setCalendarData(calData);
    
    zswi.schemas.caldav.query.TimeRange timeRangeFilter = new zswi.schemas.caldav.query.TimeRange();
    timeRangeFilter.setEnd(startTime.toString());
    timeRangeFilter.setStart(endTime.toString());
    
    zswi.schemas.caldav.query.CompFilter eventFilter = new zswi.schemas.caldav.query.CompFilter();
    eventFilter.setName(Component.VEVENT);
    eventFilter.setTimeRange(timeRangeFilter);
    
    zswi.schemas.caldav.query.CompFilter calFilter = new zswi.schemas.caldav.query.CompFilter();
    calFilter.setName("VCALENDAR");
    calFilter.setCompFilter(eventFilter);
    
    zswi.schemas.caldav.query.Filter filter = new zswi.schemas.caldav.query.Filter();
    filter.setCompFilter(calFilter);
    
    CalendarQuery query = new CalendarQuery();
    query.setProp(prop);
    query.setFilter(filter);
    
    try {
      result = doCalendarQuery(collection, query);
    }
    catch (NotImplemented e1) {
      e1.printStackTrace();
    }
    catch (JAXBException e) {
      e.printStackTrace();
    }
    catch (DavStoreException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  public void updateCalendarCollection(CalendarCollection collection) throws JAXBException, DavStoreException {
    zswi.schemas.caldav.proppatch.ObjectFactory factory = new zswi.schemas.caldav.proppatch.ObjectFactory();
   
    zswi.schemas.caldav.proppatch.ScheduleCalendarTransp transparency = new ScheduleCalendarTransp();

    zswi.schemas.caldav.proppatch.Prop prop = new zswi.schemas.caldav.proppatch.Prop();
    
    prop.setCalendarColor(collection.getCalendarColor());
    prop.setCalendarOrder(collection.getCalendarOrder());
    prop.setCalendarDescription(collection.getCalendarDescription());
    prop.setDisplayname(collection.getDisplayName());

    String transparencyAsStr = collection.getScheduleCalendarTransp();
    if ("OPAQUE".equals(transparencyAsStr)) 
      transparency.setOpaque(factory.createOpaque());
    else
      transparency.setTransparent(factory.createTransparent());
    prop.setScheduleCalendarTransp(transparency);

    Calendar timezone = collection.getCalendarTimezone();
    if (timezone != null)
      prop.setCalendarTimezone(timezone.toString());
    
    zswi.schemas.caldav.proppatch.Set set = new zswi.schemas.caldav.proppatch.Set();
    set.setProp(prop);
    
    zswi.schemas.caldav.proppatch.Propertyupdate propUpdate = factory.createPropertyupdate();
    propUpdate.setSet(set);
    
    StringWriter sw = new StringWriter();
    
    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.proppatch");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
    marshaller.marshal(propUpdate, sw);
    
    StringEntity se;
    try {
      se = new StringEntity(sw.toString());
      String bodyOfResponse = "";
      ProppatchRequest req = new ProppatchRequest(initUri(collection.getUri()), 0);
      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = _httpClient.execute(req);

      bodyOfResponse += EntityUtils.toString(resp.getEntity());

      int statusCode = resp.getStatusLine().getStatusCode();

      if (statusCode == HttpStatus.SC_FORBIDDEN) {
        jc = JAXBContext.newInstance("zswi.schemas.caldav.errors");
        Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
        StringReader reader = new StringReader(bodyOfResponse);
        zswi.schemas.caldav.errors.Error error = (zswi.schemas.caldav.errors.Error)userInfounmarshaller.unmarshal(reader);

        EntityUtils.consume(resp.getEntity());

        if (error.getErrorDescription() != null) {
          throw new DavStoreException(error.getErrorDescription());
        }
      } else {
        EntityUtils.consume(resp.getEntity());
        if (!(statusCode == HttpStatus.SC_MULTI_STATUS)) {
          throw new DavStoreException("We couldn't update the calendar collection, the server have sent : " + resp.getStatusLine().getReasonPhrase());
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (ParseException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
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
   */
  public ServerVCalendar getFreeBusyInformation(String uriToCalendarCollection, DateTime startTime, DateTime endTime) throws DavStoreException, DateNotUtc {

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
      
      HttpResponse resp = _httpClient.execute(req);

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
      
      HttpResponse resp = _httpClient.execute(req);
      
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
  
}
