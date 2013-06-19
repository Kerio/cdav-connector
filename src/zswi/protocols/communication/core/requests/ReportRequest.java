package zswi.protocols.communication.core.requests;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
	Object of this class just extends HttpEntityEnclosingRequestBase class
	and represents http PUT request. 
	@author Jan Ambroz
*/
public class ReportRequest extends HttpEntityEnclosingRequestBase {

	/**
		Overriden object constructor
		@param uri request URI
		@param depth depth of report request
	*/
	public ReportRequest(URI uri, int depth) {
		this.setURI(uri);
	}
	
	@Override
	public String getMethod() {
		return "REPORT";
	}

	
}