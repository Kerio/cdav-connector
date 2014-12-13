package zswi.protocols.communication.core.requests;

import java.net.URI;

import org.apache.http.client.methods.HttpPut;

/**
 * Update request in this library is the same as PUT, but extended with If-Match header. 
 * 
 * @author Jindřich Pouba
 *
 */
public class UpdateRequest extends HttpPut{

	/**
	 * Creates a new UpdateRequest with targeted URI and eTag.
	 * 
	 * @param uri targeted URI
	 * @param etag eTag of the object to update
	 */
	public UpdateRequest(URI uri, String etag) {
		super(uri);
		if (etag != null && !"".equals(etag)) {
		  this.addHeader("If-Match", etag);
		}
	}
}