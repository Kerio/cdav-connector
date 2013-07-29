package zswi.protocols.communication.core.requests;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * Object of MoveRequest class represents a HTTP MOVE request.
 * 
 * @author Pascal Robert
 *
 */
public class MoveRequest extends HttpEntityEnclosingRequestBase {
	
	/**
	 * Creates a new PropfindRequest with provided URI and Depth.
	 * @param uri targeted URI
	 * @param depth depth of the request
	 */
	public MoveRequest(URI uri, String destinationPath) {
		super();
		this.setURI(uri);
		this.addHeader("Destination", (destinationPath) );
	}
	
	@Override
	public String getMethod() {
		return "MOVE";
	}

}
