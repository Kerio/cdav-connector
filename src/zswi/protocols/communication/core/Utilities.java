package zswi.protocols.communication.core;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class Utilities {

  public static URI initUri(String path, String scheme, String serverName, int port, String username, String password) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder();
    String encodedPath = path;
    try {
      encodedPath = java.net.URLDecoder.decode(path, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      System.out.println(e);
    }
    uriBuilder.setScheme(scheme).setHost(serverName).setPath(encodedPath).setPort(port);

    if (username != null) {
      uriBuilder.setUserInfo(username, password);
    }

    return uriBuilder.build();
  }

  public static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  } 

}
