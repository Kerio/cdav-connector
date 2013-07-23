package zswi.objects.dav.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RecordType {

  USERS("users"),
  GROUPS("groups"),
  LOCATIONS("locations"),
  RESOURCES("resources");
  
  private String type;
  
  private static final Map<String,RecordType> typesLookup = new HashMap<String,RecordType>();

  static {
    for(RecordType s : EnumSet.allOf(RecordType.class)) {
      typesLookup.put(s.type(), s);
    }
  }
  
  private RecordType(String _type) {
    type = _type;
  }
  
  public String type() {
    return type;
  }
  
  public static RecordType getByType(String type) { 
    return typesLookup.get(type); 
  }
  
}
