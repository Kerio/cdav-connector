package zswi.protocols.communication.core.requests;

import java.net.URI;

/**
 * Object of Copy class represents a HTTP MOVE request.
 * 
 * @author Pascal Robert
 *
 */
public class CopyRequest extends MoveRequest {
	
	/**
	 * Creates a new PropfindRequest with provided URI and Depth.
	 * @param uri targeted URI
	 * @param depth depth of the request
	 */
	public CopyRequest(URI uri, String destinationPath) {
		super(uri, destinationPath);
	}
	
	@Override
	public String getMethod() {
		return "COPY";
	}

}
