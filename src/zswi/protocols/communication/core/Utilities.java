package zswi.protocols.communication.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class Utilities {

  public static URI initUri(String path, String scheme, String serverName, int port, String username, String password) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder();
    uriBuilder.setScheme(scheme).setHost(serverName).setPath(path).setPort(port);

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
