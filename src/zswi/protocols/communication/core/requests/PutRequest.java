package zswi.protocols.communication.core.requests;

import java.net.URI;

import org.apache.http.client.methods.HttpPut;

/**
	Object of this class just extends HttpPut class and 
	represents http PUT request. 
	@author Jan Ambroz
*/
public class PutRequest extends HttpPut{
	
	/**
		Overriden object constructor
		@param uri request URI
	*/
	public PutRequest(URI uri){
		super(uri);
	}
}
