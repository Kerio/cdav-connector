package zswi.protocols.communication.core;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public class NotRedirectStrategy implements RedirectStrategy {

  @Override
  public HttpUriRequest getRedirect(HttpRequest httprequest, HttpResponse httpresponse, HttpContext httpcontext) throws ProtocolException {
    return null;
  }

  @Override
  public boolean isRedirected(HttpRequest httprequest, HttpResponse httpresponse, HttpContext httpcontext) throws ProtocolException {
    return false;
  }

}
