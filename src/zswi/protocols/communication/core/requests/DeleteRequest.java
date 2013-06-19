package zswi.protocols.communication.core.requests;

import java.net.URI;

import org.apache.http.client.methods.HttpDelete;

/**
	Object of this class just extends HttpGet class and 
	represents http DELETE request.
	
	@author Jindřich Pouba	
*/
public class DeleteRequest extends HttpDelete{

	/**
	 * Creates a new DeleteRequest, which will delete object on targeted URI with targeted eTag. 
	 * 
	 * @param uri
	 * @param etag
	 */
	public DeleteRequest(URI uri, String etag){
		super();
		this.setURI(uri);
		this.addHeader("If-Match",etag);
	}
	
}
