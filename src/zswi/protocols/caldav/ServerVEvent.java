package zswi.protocols.caldav;

import net.fortuna.ical4j.model.component.VEvent;

/**
 * Server VEvent represents a vEvent downloaded from the server.
 * 
 * @author Tomáš Balíček
 *
 */
public class ServerVEvent {

	private VEvent vevent;
	private	final String eTag;
	private String path;
	
	public ServerVEvent(VEvent vevent, String eTag, String path){
		this.vevent = vevent;
		this.eTag = eTag;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public VEvent getVevent() {
		return vevent;
	}

	public void setVevent(VEvent vevent) {
		this.vevent = vevent;
	}

	public String geteTag() {
		return eTag;
	}
}
