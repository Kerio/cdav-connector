package zswi.protocols.communication.core.requests;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
	Represents a MKCALENDAR request 
	@author Pascal Robert
*/
public class MkCalendarRequest extends HttpEntityEnclosingRequestBase {
	
  /**
   */
  public MkCalendarRequest(URI uri) {
    super();
    this.setURI(uri);
  }
  
  @Override
  public String getMethod() {
    return "MKCALENDAR";
  }
}
