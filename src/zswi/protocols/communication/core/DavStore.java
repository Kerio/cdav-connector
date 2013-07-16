package zswi.protocols.communication.core;

import java.io.IOException;
import java.io.InputStream;
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
import javax.xml.parsers.ParserConfigurationException;

import net.fortuna.ical4j.data.ParserException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
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
import org.xml.sax.SAXException;

import zswi.objects.dav.collections.CalendarCollection;
import zswi.objects.dav.collections.CalendarHomeSet;
import zswi.objects.dav.collections.PrincipalCollection;
import zswi.objects.dav.enums.DavFeature;
import zswi.protocols.caldav.CalendarsGetter;
import zswi.protocols.caldav.ServerCalendar;
import zswi.protocols.caldav.ServerVEvent;
import zswi.protocols.caldav.VEvent_XML_Parser;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.ReportRequest;

/**
 * Connect to a CalDAV/CardDAV server by auto-discovery
 * 
 * @author probert
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

  static final Logger logger = Logger.getLogger(DavStore.class.getName());

  /**
   * Connect to a DAV store with credentials. The username must be an email address or a fully qualified http(s) URL, 
   * because the domain will be extracted from the email address or from the HTTP(S) URL.
   * 
   * @param username
   * @param password
   * @throws NamingException Throw this exception if we can't find a SRV DNS record to locate the CalDAV server for the user.
   * @throws IOException 
   * @throws URISyntaxException 
   * @throws ClientProtocolException 
   * @throws JAXBException 
   * @throws ParserException 
   */
  public DavStore(String username, String password) throws NamingException, ClientProtocolException, URISyntaxException, IOException, JAXBException, ParserException {
    _supportedFeatures = new ArrayList<DavFeature>();
    _username = username;
    _password = password;
    _isSecure = false;
    extractUserDetails(username);
    checkSrvRecords();
    try {
      checkTxtRecords();
    } catch (NamingException namingEx) {
      logger.info("We didn't find any TXT records, will try well_know URLs");
      checkWellKnownUrl();
    } 
    
    /* After we have found the well known URL, we clear the current httpClient. Why? Because the host returned by the well known URL might be different. */
    _httpClient.getConnectionManager().shutdown();
    _httpClient = null;
    
    fetchFeatures();
    fetchPrincipalsCollection();
  }

  /**
   * Connect to a DAV store with credentials and a URL. The URL must be the location of 
   * the user's principals (e.g. http://mydomain.com/principals/users/myuser or something
   * similar).
   * 
   * @param username
   * @param password
   * @param url
   */
  public DavStore(String username, String password, String url) {
    _username = username;
    _password = password;
    extractUserDetails(url);
    try {
      checkWellKnownUrl();
    }
    catch (ClientProtocolException e) {
      e.printStackTrace();
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    fetchFeatures();
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

  protected void checkWellKnownUrl() throws URISyntaxException, ClientProtocolException, IOException {
    httpClient().setRedirectStrategy(new NotRedirectStrategy());

    PropfindRequest req = new PropfindRequest(new URL(httpScheme(), _serverName, _port, "/.well-known/caldav").toURI(), 0);
    InputStream is = ClassLoader.getSystemResourceAsStream("well-known-request.xml");

    StringEntity se = new StringEntity(convertStreamToString(is));

    se.setContentType("text/xml");
    req.setEntity(se);

    HttpResponse response = httpClient().execute(req);
    int status = response.getStatusLine().getStatusCode();
    if (status >= 300 && status < 400) {
      Header[] location = response.getHeaders("Location");
      String urlFromHeader = location[0].getValue();
      URL rootUrl = new URL(urlFromHeader);
      _path = rootUrl.getPath();
      _isSecure = (rootUrl.getProtocol().equals("https")) ? true : false;
      _serverName = rootUrl.getHost();
    } else {
      throw new IOException("No redirection found");
    }

    EntityUtils.consume(response.getEntity());
  }
  
  protected void fetchPrincipalsCollection() throws URISyntaxException, JAXBException, ClientProtocolException, IOException, ParserException {
    PropfindRequest req = new PropfindRequest(initUri("/"), 0);
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
      if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
        currentUserPrincipal = propstat.getProp().getCurrentUserPrincipal().getHref();        
      }
    }

    EntityUtils.consume(resp.getEntity());

    PrincipalCollection principals = new PrincipalCollection(this, initUri(currentUserPrincipal));

    CalendarHomeSet calHomeSet = new CalendarHomeSet(httpClient(), principals, initUri(principals.getCalendarHomeSetUrl().getPath()));
    _principalCollection = calHomeSet.getOwner();
    
  }
  
  public PrincipalCollection principalCollection() {
    return _principalCollection;
  }

  public URI initUri(String path) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder();
    uriBuilder.setScheme(httpScheme()).setHost(_serverName).setPath(path).setPort(_port);

    if (_username != null) {
      uriBuilder.setUserInfo(_username, _password);
    }

    return uriBuilder.build();
  }

  public String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
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

  public void fetchFeatures() {
    _supportedFeatures = new ArrayList<DavFeature>();
    try {
      HttpHead headersMethod = new HttpHead(new URL(httpScheme(), _serverName, _port, _path).toURI());
      HttpResponse response = httpClient().execute(headersMethod);
      Header[] davHeaders = response.getHeaders("DAV");
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
  
  public List<ServerVEvent> getVEvents(CalendarCollection calendar) throws ClientProtocolException, IOException, URISyntaxException, ParserException {

    String path = calendar.getUri();

    String response = this.report("rep_events.txt", path, 1);

    VEvent_XML_Parser parser = new VEvent_XML_Parser();
    List<ServerVEvent> result = parser.parseMultiVEvent(response);

    return result;
  }
  
  private String report(String filename, String path, int depth) throws ClientProtocolException, IOException, URISyntaxException {
    ReportRequest req = new ReportRequest(initUri(path), depth);

    InputStream is = ClassLoader.getSystemResourceAsStream(filename);

    StringEntity se = new StringEntity(convertStreamToString(is));
    
    se.setContentType("text/xml");
    req.setEntity(se);

    HttpResponse resp = _httpClient.execute(req);

    String response = "";
    response += EntityUtils.toString(resp.getEntity());

    EntityUtils.consume(resp.getEntity());

    return response;
  }

}
