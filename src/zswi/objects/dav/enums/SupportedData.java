package zswi.objects.dav.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SupportedData {

  VCARD_3_0("address-data-type","text/vcard","3.0"),
  ICALENDAR_2_0("calendar-data","text/calendar","2.0");
  
  private String type;
  private String mimeType;
  private String version;

  private static final Map<String,SupportedData> typesLookup = new HashMap<String,SupportedData>();

  static {
    for(SupportedData s : EnumSet.allOf(SupportedData.class)) {
      typesLookup.put(s.type(), s);
    }
  }

  private SupportedData(String _type, String _mimeType, String _version) {
    type = _type;
    mimeType = _mimeType;
    version = _version;
  }
  
  public String type() {
    return type;
  }

  public String mimeType() {
    return mimeType;
  }
  
  public String version() {
    return version;
  }

  public static SupportedData get(String _type, String _mimeType, String version) { 
    return typesLookup.get(_type); 
  }


}
