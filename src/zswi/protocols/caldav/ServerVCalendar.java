package zswi.protocols.caldav;

import net.fortuna.ical4j.model.Calendar;
import zswi.objects.dav.collections.CalendarCollection;

/**
 * Server VCalendar represents a vCalendar downloaded from the server.
 * 
 * @author Pascal Robert, Tomáš Balíček
 *
 */
public class ServerVCalendar {

  private Calendar vcalendar;
  private String eTag;
  private String path;
  private CalendarCollection parentCollection;

  public ServerVCalendar(Calendar vcalendar, String eTag, String path){
    this.vcalendar = vcalendar;
    this.eTag = eTag;
    this.path = path;
  }

  public ServerVCalendar(Calendar vcalendar, String eTag, String path, CalendarCollection parentCollection){
    this(vcalendar, eTag, path);
    this.parentCollection = parentCollection;
  }

  public String getPath() {
    return path;
  }

  public Calendar getVCalendar() {
    return vcalendar;
  }

  public void setVCalendar(Calendar vcalendar) {
    this.vcalendar = vcalendar;
  }

  public String geteTag() {
    return eTag;
  }

  public void seteTag(String eTag) {
    this.eTag = eTag;
  }
  
  public CalendarCollection getParentCollection() {
    return parentCollection;
  }
}
