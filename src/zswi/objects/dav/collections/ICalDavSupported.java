package zswi.objects.dav.collections;

import zswi.schemas.dav.allprop.SupportedCalendarComponentSet;

/**
 * Things that should only be into a calendar, inbox or outbox collection.
 * 
 * @author probert
 *
 */
public interface ICalDavSupported {

  public java.util.ArrayList<String> getSupportedCalendarComponentSet();
  public void setSupportedCalendarComponentSet(java.util.ArrayList<String> supportedCalendarComponentSet);
  // TODO supported-calendar-data
  
}
