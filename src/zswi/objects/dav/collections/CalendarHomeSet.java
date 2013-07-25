package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
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
import zswi.schemas.dav.allprop.Comp;
import zswi.schemas.dav.allprop.ResourceId;
import zswi.schemas.dav.allprop.Resourcetype;
import zswi.schemas.dav.allprop.ScheduleCalendarTransp;
import zswi.schemas.dav.allprop.SupportedCalendarComponentSet;
import zswi.schemas.dav.allprop.SupportedCalendarComponentSets;

public class CalendarHomeSet extends AbstractHomeSetCollection {

  //{calserver}xmpp-heartbeat-uri
  //{cal server}xmpp-server
  //{cal server}xmpp-uri
  net.fortuna.ical4j.model.component.VAlarm defaultAlarmVeventDate;
  net.fortuna.ical4j.model.component.VAlarm defaultAlarmVeventDatetime;
  ArrayList<String> supportedCalendarComponentSets;
  java.util.ArrayList<CalendarCollection> calendarCollections;
  
  public CalendarHomeSet(DefaultHttpClient _httpClient, PrincipalCollection principals, URI uriForRequest) throws JAXBException, ClientProtocolException, IOException, URISyntaxException, ParserException {
    PropfindRequest req = new PropfindRequest(uriForRequest, 1);
    InputStream is = ClassLoader.getSystemResourceAsStream("props-calendarhomeset-request.xml");

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
            setQuotaAvailableBytes(propstat.getProp().getQuotaAvailableBytes());
            setQuotaUsedBytes(propstat.getProp().getQuotaUsedBytes());
            setGetctag(propstat.getProp().getGetctag());
            
            String syncToken = propstat.getProp().getSyncToken();
            if (syncToken != null)
              setSyncToken(new java.net.URI(syncToken));
                        
            String defaultAlarmDate = propstat.getProp().getDefaultAlarmVeventDate();
            if (defaultAlarmDate != null)
              setDefaultAlarmVeventDate(convertStringToValarm(defaultAlarmDate));
            
            String defaultAlarmDatetime = propstat.getProp().getDefaultAlarmVeventDatetime();
            if (defaultAlarmDatetime != null) 
              setDefaultAlarmVeventDatetime(convertStringToValarm(propstat.getProp().getDefaultAlarmVeventDatetime()));
            
            SupportedCalendarComponentSets componentSets = propstat.getProp().getSupportedCalendarComponentSets();
            if (componentSets != null) {
              for (SupportedCalendarComponentSet compSet: componentSets.getSupportedCalendarComponentSet()) {
                for (Comp component: compSet.getComp()) {
                  getSupportedCalendarComponentSets().add(component.getName());
                }
              }
            }
            
            setDisplayName(propstat.getProp().getDisplayname());
            setUri(response.getHref());
            principals.setCalendarHomeSet(this);
            
          } else {
            
            if (isCalendarCollection(propstat.getProp())) {
              CalendarCollection collection = new CalendarCollection(_httpClient);
              collection.setCalendarColor(propstat.getProp().getCalendarColor());
              
              String calendarOrder = propstat.getProp().getCalendarOrder();
              if (calendarOrder != null) {
                 BigInteger order = new BigInteger(calendarOrder);
                 collection.setCalendarOrder(order);
              }
              
              collection.setDisplayName(propstat.getProp().getDisplayname());
              collection.setGetctag(propstat.getProp().getGetctag());
              collection.setOwner(principals);
              collection.setUri(response.getHref());
              
              ScheduleCalendarTransp transparency = propstat.getProp().getScheduleCalendarTransp();
              if ((transparency != null) && (transparency.getTransparent() != null)) {
                collection.setScheduleCalendarTransp("TRANSPARENT");
              } else {
                collection.setScheduleCalendarTransp("OPAQUE");                
              }
              
              collection.setQuotaAvailableBytes(propstat.getProp().getQuotaAvailableBytes());
              collection.setQuotaUsedBytes(propstat.getProp().getQuotaUsedBytes());
              
              String syncToken = propstat.getProp().getSyncToken();
              if (syncToken != null)
                collection.setSyncToken(new java.net.URI(syncToken));
              
              String calendarTime = propstat.getProp().getCalendarTimezone();
              if ((calendarTime != null) && (calendarTime.length() > 0)) {
                StringReader sin = new StringReader(calendarTime);
                try {
                  CalendarBuilder builder = new CalendarBuilder();
                  Calendar calendarTimeZone = builder.build(sin);
                  collection.setCalendarTimezone(calendarTimeZone);
                } catch (net.fortuna.ical4j.data.ParserException e) {
                  /*
                   *  TODO implement logging
                   *  TODO if it's a VTIMEZONE root component, parse it and create a VCALENDAR object around it
                   *  The RFC (http://tools.ietf.org/html/rfc4791#section-5.2.2) says that the timezone should be 
                   *  a VCALENDAR component that includes a VTIMEZONE component, but some CalDAV servers (I'm looking 
                   *  at you CommuniGate Pro!) stores a standalone VTIMEZONE component instead.
                   */
                }
              }
              
              ResourceId resourceId = propstat.getProp().getResourceId();
              if (resourceId != null)
                collection.setResourceId(new java.net.URI(resourceId.getHref()));
              
              SupportedCalendarComponentSet set = propstat.getProp().getSupportedCalendarComponentSet();
              for (Comp component: set.getComp()) {
                collection.getSupportedCalendarComponentSet().add(component.getName());
              }
              
              getCalendarCollections().add(collection);
            }
          }
        }
      }
    }
    
    EntityUtils.consume(resp.getEntity());
  }
  
  protected boolean isCalendarCollection(zswi.schemas.dav.allprop.Prop properties) {
    Resourcetype collectionType = properties.getResourcetype();

    if (collectionType.getCalendar() != null)
      return true;
              
    return false;
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

  public java.util.ArrayList<String> getSupportedCalendarComponentSets() {
    if (supportedCalendarComponentSets == null) {
      supportedCalendarComponentSets = new ArrayList<String>();
    }
    return supportedCalendarComponentSets;
  }

  protected void setSupportedCalendarComponentSets(java.util.ArrayList<String> supportedCalendarComponentSets) {
    this.supportedCalendarComponentSets = supportedCalendarComponentSets;
  }

  public java.util.ArrayList<CalendarCollection> getCalendarCollections() {
    if (calendarCollections == null) {
      calendarCollections = new ArrayList<CalendarCollection>();
    }
    return calendarCollections;
  }
  
}
