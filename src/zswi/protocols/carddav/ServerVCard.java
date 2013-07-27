package zswi.protocols.carddav;

import net.sourceforge.cardme.vcard.VCard;

/**
 * ServerVCard represents a vCard downloaded from the server.
 * 
 * @author Marek Šimůnek
 *
 */
public class ServerVCard {
	private VCard vcard;
	private	String eTag;
	private String path;
	
	public ServerVCard(VCard vcard, String eTag, String path){
		this.vcard = vcard;
		this.eTag = eTag;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public VCard getVcard() {
		return vcard;
	}

	public void setVcard(VCard vcard) {
		this.vcard = vcard;
	}

	public String geteTag() {
		return eTag;
	}

  public void seteTag(String eTag) {
    this.eTag = eTag;
  }

}
