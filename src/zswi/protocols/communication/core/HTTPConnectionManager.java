package zswi.protocols.communication.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.AuthCache;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;

public class HTTPConnectionManager {
  
  protected String _username;
  protected String _password;
  protected String _domain;
  protected String _serverName;
  protected String _path;
  protected Integer _port;
  protected DefaultHttpClient _httpClient;
  protected boolean _isSecure;
  protected HttpHost _targetHost;

  public HTTPConnectionManager() {
    
  }

  public String getUsername() {
    return _username;
  }

  public void setUsername(String username) {
    _username = username;
  }

  public String getPassword() {
    return _password;
  }

  public void setPassword(String password) {
    _password = password;
  }

  public String getDomain() {
    return _domain;
  }

  public void setDomain(String domain) {
    _domain = domain;
  }

  public String getServerName() {
    return _serverName;
  }

  public void setServerName(String serverName) {
    _serverName = serverName;
  }

  public String getPath() {
    return _path;
  }

  public void setPath(String path) {
    _path = path;
  }

  public Integer getPort() {
    return _port;
  }

  public void setPort(Integer port) {
    _port = port;
  }

  public DefaultHttpClient getHttpClient() {
    if (_httpClient == null) {

      _httpClient = new DefaultHttpClient();

      if (isSecure()) {
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
          setHttpClient(new DefaultHttpClient(ccm));
        }
        catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
        }
        catch (KeyManagementException e) {
          e.printStackTrace();
        }
      }

      _httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials(getUsername(), getPassword()));
      _httpClient.setRedirectStrategy(new LaxRedirectStrategy());

      List<String> authpref = new ArrayList<String>();
      authpref.add(AuthPolicy.DIGEST);
      authpref.add(AuthPolicy.BASIC);
      _httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);

      AuthCache authCache = new BasicAuthCache();
      DigestScheme digestAuth = new DigestScheme();
      digestAuth.overrideParamter("realm", "whatever");
      digestAuth.overrideParamter("nonce", "whatever");
      authCache.put(getTargetHost(), digestAuth);

      BasicHttpContext localcontext = new BasicHttpContext();
      localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
    }
    return _httpClient;
  }

  public void setHttpClient(DefaultHttpClient httpClient) {
    _httpClient = httpClient;
  }

  public boolean isSecure() {
    return _isSecure;
  }

  public void setSecure(boolean isSecure) {
    _isSecure = isSecure;
  }

  public HttpHost getTargetHost() {
    if (_targetHost == null) {
      _targetHost = new HttpHost(_serverName, _port, httpScheme());
    }
    return _targetHost;
  }

  public void setTargetHost(HttpHost targetHost) {
    _targetHost = targetHost;
  }
  
  public void closeConnection() {
    _httpClient.getConnectionManager().shutdown();
    _httpClient = null;
  }
  
  public String httpScheme() {
    return (_isSecure) ? "https" : "http";
  }
  
  public URI initUri(String path) throws URISyntaxException {
    return Utilities.initUri(path, httpScheme(), _serverName, _port, _username, _password);
  }

}
