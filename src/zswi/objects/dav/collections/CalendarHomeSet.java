package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.schemas.dav.allprop.Resourcetype;
import zswi.schemas.dav.allprop.ScheduleCalendarTransp;

public class CalendarHomeSet extends AbstractHomeSetCollection {

  //{calserver}xmpp-heartbeat-uri
  //{cal server}xmpp-server
  //{cal server}xmpp-uri
  net.fortuna.ical4j.model.component.VAlarm defaultAlarmVeventDate;
  net.fortuna.ical4j.model.component.VAlarm defaultAlarmVeventDatetime;
  java.util.List supportedCalendarComponentSets;
  java.util.ArrayList<CalendarCollection> calendarCollections;
  
  public CalendarHomeSet(DefaultHttpClient _httpClient, PrincipalCollection principals, URI uriForRequest) throws JAXBException, ClientProtocolException, IOException {
    PropfindRequest req = new PropfindRequest(uriForRequest, 1);
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
          
          /** If URL is the same, it's the calendar-home-set */
          if (response.getHref().equals(uriForRequest.getPath())) {
            
            setOwner(principals);
            //setQuotaAvailableBytes(quotaAvailableBytes);
            //setQuotaUsedBytes(quotaUsedBytes);
            //setSyncToken(uriForRequest);
            setGetctag(propstat.getProp().getGetctag());
            setDefaultAlarmVeventDate(convertStringToValarm(propstat.getProp().getDefaultAlarmVeventDate()));
            setDefaultAlarmVeventDatetime(convertStringToValarm(propstat.getProp().getDefaultAlarmVeventDatetime()));
            //setSupportedCalendarComponentSets(supportedCalendarComponentSets);
            setDisplayName(propstat.getProp().getDisplayname());
            setUri(response.getHref());
            principals.setCalendarHomeSet(this);
            
          } else {
            
            Resourcetype collectionType = propstat.getProp().getResourcetype();
            if (collectionType.getCalendar() != null) {
              CalendarCollection collection = new CalendarCollection();
              collection.setCalendarColor(propstat.getProp().getCalendarColor());
              collection.setCalendarOrder(propstat.getProp().getCalendarOrder());
              collection.setDisplayName(propstat.getProp().getDisplayname());
              collection.setGetctag(propstat.getProp().getGetctag());
              collection.setOwner(principals);
              collection.setUri(response.getHref());
              ScheduleCalendarTransp transparency = propstat.getProp().getScheduleCalendarTransp();
              if (transparency.getOpaque() != null) {
                collection.setScheduleCalendarTransp("OPAQUE");                
              } else {
                collection.setScheduleCalendarTransp("TRANSPARENT");
              }
              //collection.setQuotaAvailableBytes(quotaAvailableBytes);
              //collection.setQuotaUsedBytes(quotaUsedBytes);
              //collection.setSyncToken(uriForRequest);
              //((CalendarCollection)collection).setCalendarTimezone(null);
              //((CalendarCollection)collection).setMaxDateTime(creationDate);
              //((CalendarCollection)collection).setMinDateTime(creationDate);
              //((CalendarCollection)collection).setResourceId(uriForRequest);
              
              getCalendarCollections().add(collection);
            }
          }
        }
      }
    }
    
    EntityUtils.consume(resp.getEntity());
  }
  
  public VAlarm convertStringToValarm(String alarm) {
    String defaultAlarmTime = alarm;
    if (defaultAlarmTime != null) {
      VAlarm vAlarm = new VAlarm();
      String[] alarmProperties = defaultAlarmTime.split("\\n");
      for (int tokenIterator = 0; tokenIterator < alarmProperties.length; tokenIterator++) {
        String[] keyAndValue = alarmProperties[tokenIterator].split(":");
        if (Property.ACTION.equals(keyAndValue[0])) {
          vAlarm.getProperties().add(new Action(keyAndValue[1]));
        } else if (keyAndValue[0].startsWith(Property.TRIGGER)) {
          ParameterList paramList = new ParameterList();
          paramList.add(Value.DATE_TIME);
          vAlarm.getProperties().add(new Trigger(paramList,keyAndValue[1]));              
        } else if (Property.UID.equals(keyAndValue[0])) {
          vAlarm.getProperties().add(new Uid(keyAndValue[1]));
        } else if ("X-WR-ALARMUID".equals(keyAndValue[0])) {
          vAlarm.getProperties().add(new XProperty(keyAndValue[0],keyAndValue[1]));
        }
      }
      return vAlarm;
    }
    return null;
  }
  
  private String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  } 

  public net.fortuna.ical4j.model.component.VAlarm getDefaultAlarmVeventDate() {
    return defaultAlarmVeventDate;
  }

  protected void setDefaultAlarmVeventDate(net.fortuna.ical4j.model.component.VAlarm defaultAlarmVeventDate) {
    this.defaultAlarmVeventDate = defaultAlarmVeventDate;
  }

  public net.fortuna.ical4j.model.component.VAlarm getDefaultAlarmVeventDatetime() {
    return defaultAlarmVeventDatetime;
  }

  protected void setDefaultAlarmVeventDatetime(net.fortuna.ical4j.model.component.VAlarm defaultAlarmVeventDatetime) {
    this.defaultAlarmVeventDatetime = defaultAlarmVeventDatetime;
  }

  public java.util.List getSupportedCalendarComponentSets() {
    return supportedCalendarComponentSets;
  }

  protected void setSupportedCalendarComponentSets(java.util.List supportedCalendarComponentSets) {
    this.supportedCalendarComponentSets = supportedCalendarComponentSets;
  }

  public java.util.ArrayList<CalendarCollection> getCalendarCollections() {
    if (calendarCollections == null) {
      calendarCollections = new ArrayList<CalendarCollection>();
    }
    return calendarCollections;
  }
  
}
