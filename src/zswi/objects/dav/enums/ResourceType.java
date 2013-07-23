package zswi.objects.dav.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ResourceType {

  COLLECTION("collection"),
  CALENDAR("calendar"),
  DROPBOX_HOME("dropbox-home"),
  SCHEDULE_INBOX("schedule-inbox"),
  NOTIFICATION("notification"),
  SCHEDULE_OUTBOX("schedule-outbox");

  private String type;

  private static final Map<String,ResourceType> typesLookup = new HashMap<String,ResourceType>();

  static {
    for(ResourceType s : EnumSet.allOf(ResourceType.class)) {
      typesLookup.put(s.type(), s);
    }
  }

  private ResourceType(String _type) {
    type = _type;
  }

  public String type() {
    return type;
  }

  public static ResourceType getByType(String type) { 
    return typesLookup.get(type); 
  }

}
