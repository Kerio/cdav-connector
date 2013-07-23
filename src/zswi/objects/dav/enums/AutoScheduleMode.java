package zswi.objects.dav.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AutoScheduleMode {

  DEFAULT("default"),
  NONE("none"),
  ACCEPT_ALWAYS("accept-always"),
  ACCEPT_IF_FREE("accept-if-free"),
  DECLINE_ALWAYS("decline-always"),
  DECLINE_IF_BUSY("decline-if-busy"),
  AUTOMATIC("automatic");
    
  private String mode;
  
  private static final Map<String,AutoScheduleMode> modesLookup = new HashMap<String,AutoScheduleMode>();

  static {
    for(AutoScheduleMode s : EnumSet.allOf(AutoScheduleMode.class)) {
      modesLookup.put(s.mode(), s);
    }
  }
  
  private AutoScheduleMode(String _mode) {
    mode = _mode;
  }
  
  public String mode() {
    return mode;
  }
  
  public static AutoScheduleMode getByMode(String mode) { 
    return modesLookup.get(mode); 
  }
  
}
