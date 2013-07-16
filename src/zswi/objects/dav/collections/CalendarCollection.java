package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import zswi.protocols.caldav.CalendarsGetter;
import zswi.protocols.caldav.ServerCalendar;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.schemas.dav.allprop.SupportedCalendarComponentSet;

public class CalendarCollection extends AbstractNotPrincipalCollection implements ICalDavSupported {

  java.net.URI addMember;
  java.net.URI resourceId;
  net.fortuna.ical4j.model.Calendar calendarTimezone;
  java.util.Date minDateTime;
  java.util.Date maxDateTime;
  String scheduleCalendarTransp;
  String calendarColor;
  BigInteger calendarOrder;
  ArrayList<String> supportedCalendarComponentSet;
  //{caldav}supported-collation-set
  DefaultHttpClient httpClient;

  public CalendarCollection(DefaultHttpClient _httpClient) {
    httpClient = _httpClient;
  }

  private String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  } 

  public java.net.URI getAddMember() {
    return addMember;
  }

  protected void setAddMember(java.net.URI addMember) {
    this.addMember = addMember;
  }

  public java.net.URI getResourceId() {
    return resourceId;
  }

  protected void setResourceId(java.net.URI resourceId) {
    this.resourceId = resourceId;
  }

  public net.fortuna.ical4j.model.Calendar getCalendarTimezone() {
    return calendarTimezone;
  }

  protected void setCalendarTimezone(net.fortuna.ical4j.model.Calendar calendarTimezone) {
    this.calendarTimezone = calendarTimezone;
  }

  public java.util.Date getMinDateTime() {
    return minDateTime;
  }

  protected void setMinDateTime(java.util.Date minDateTime) {
    this.minDateTime = minDateTime;
  }

  public java.util.Date getMaxDateTime() {
    return maxDateTime;
  }

  protected void setMaxDateTime(java.util.Date maxDateTime) {
    this.maxDateTime = maxDateTime;
  }

  public String getScheduleCalendarTransp() {
    return scheduleCalendarTransp;
  }

  protected void setScheduleCalendarTransp(String scheduleCalendarTransp) {
    this.scheduleCalendarTransp = scheduleCalendarTransp;
  }

  public String getCalendarColor() {
    return calendarColor;
  }

  protected void setCalendarColor(String calendarColor) {
    this.calendarColor = calendarColor;
  }

  public BigInteger getCalendarOrder() {
    return calendarOrder;
  }

  protected void setCalendarOrder(BigInteger calendarOrder) {
    this.calendarOrder = calendarOrder;
  }

  @Override
  public ArrayList<String> getSupportedCalendarComponentSet() {
    if (supportedCalendarComponentSet == null) {
      supportedCalendarComponentSet = new ArrayList<String>();
    }
    return supportedCalendarComponentSet;
  }

  @Override
  public void setSupportedCalendarComponentSet(ArrayList<String> supportedCalendarComponentSet) {
    this.supportedCalendarComponentSet = supportedCalendarComponentSet;
  }

}
