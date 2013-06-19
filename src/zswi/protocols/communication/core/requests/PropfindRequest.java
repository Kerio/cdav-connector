package zswi.protocols.communication.core.requests;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * Object of PropfindRequest class represents a HTTP PROPFIND request.
 * 
 * @author Jindřich Pouba
 *
 */
public class PropfindRequest extends HttpEntityEnclosingRequestBase {

	private static final int BASE_DEPTH = 1;
	
	/**
	 * Creates a new PropfindRequest with provided URI. Basic Depth of this request is 1.
	 * @param uri targeted URI of the request 
	 */
	public PropfindRequest(URI uri) {
		this(uri, BASE_DEPTH);
	}
	
	/**
	 * Creates a new PropfindRequest with provided URI and Depth.
	 * @param uri targeted URI
	 * @param depth depth of the request
	 */
	public PropfindRequest(URI uri, int depth) {
		super();
		this.setURI(uri);
		this.addHeader("Depth", (depth+"") );
	}
	
	@Override
	public String getMethod() {
		return "PROPFIND";
	}

}
