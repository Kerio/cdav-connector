package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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

  public CalendarCollection() {
  }

  public CalendarCollection(DefaultHttpClient _httpClient, URI uri) throws JAXBException, ClientProtocolException, IOException {
    PropfindRequest req = new PropfindRequest(uri, 1);
    InputStream is = ClassLoader.getSystemResourceAsStream("allprop-calendarhomeset-request.xml");

    StringEntity se = new StringEntity(convertStreamToString(is));

    se.setContentType("text/xml");
    req.setEntity(se);

    HttpResponse resp = _httpClient.execute(req);

    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.dav.allprop");
    Unmarshaller appPropUnmarshaller = jc.createUnmarshaller();
    zswi.schemas.dav.allprop.Multistatus unmarshalAllProp = (zswi.schemas.dav.allprop.Multistatus)appPropUnmarshaller.unmarshal(resp.getEntity().getContent());

    for (zswi.schemas.dav.allprop.Response response: unmarshalAllProp.getResponse()) {
      for (zswi.schemas.dav.allprop.Propstat propstat: response.getPropstat()) {
        if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
          /*
          List<?> properties = propstat.getProp().getCreationdateOrCurrentUserPrincipalOrDisplayname();
          for (Object property: properties) {
            if (property instanceof zswi.schemas.dav.allprop.Resourcetype) {
              Calendar calendarType = ((zswi.schemas.dav.allprop.Resourcetype)property).getCalendar();
              if (calendarType != null) {
                System.out.println("It's a calendar!");
              }
            }
          }
           */
        }
      }
    }

    EntityUtils.consume(resp.getEntity());
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
