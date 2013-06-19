package zswi.protocols.communication.core;
/**
 * Object of this class represents exception which was caused in installCert method.
 * 
 * @author Jan Ambrož
 *
 */
public class InstallCertException extends Exception {
	public InstallCertException(){
		super();
	}
	
	public InstallCertException(String s){
		super(s);
	}
}
