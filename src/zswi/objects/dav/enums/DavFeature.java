package zswi.objects.dav.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DavFeature {

  ACCESS_CONTROL("access-control"),
  CALENDAR_ACCESS("calendar-access"),
  CALENDAR_SCHEDULE("calendar-schedule"),
  CALENDAR_AUTO_SCHEDULE("calendar-auto-schedule"),
  CALENDAR_AVAILABILITY("calendar-availability"),
  INBOX_AVAILABILITY("inbox-availability"),
  CALENDAR_PROXY("calendar-proxy"),
  CALENDARSERVER_PRIVATE_EVENTS("calendarserver-private-events"),
  CALENDARSERVER_PRIVATE_COMMENTS("calendarserver-private-comments"),
  CALENDARSERVER_SHARING("calendarserver-sharing"),
  CALENDARSERVER_SHARING_NO_SCHEDULING("calendarserver-sharing-no-scheduling"),
  CALENDAR_QUERY_EXTENDED("calendar-query-extended"),
  CALENDAR_DEFAULT_ALARMS("calendar-default-alarms"),
  ADDRESSBOOK("addressbook"),
  CALENDARSERVER_PRINCIPAL_PROPERTY_SEARCH("calendarserver-principal-property-search"), 
  CALENDARSERVER_PRINCIPAL_SEARCH("calendarserver-principal-search"),
  EXTENDED_MKCOL("extended-mkcol");
  
  private String _featureName;

  private static final Map<String,DavFeature> featureNameLookup = new HashMap<String,DavFeature>();

  static {
    for(DavFeature s : EnumSet.allOf(DavFeature.class)) {
      featureNameLookup.put(s.featureName(), s);
    }
  }
  
  private DavFeature(String featureName) {
    _featureName = featureName;
  }
  
  public static DavFeature getByFeatureName(String zimbraValue) { 
    return featureNameLookup.get(zimbraValue); 
  }

  public String featureName() {
    return _featureName;
  }
  
}
