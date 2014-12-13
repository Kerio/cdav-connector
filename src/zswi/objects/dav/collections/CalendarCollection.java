package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import zswi.protocols.caldav.ServerVCalendar;
import zswi.protocols.caldav.ServerVEvent;
import zswi.protocols.carddav.ServerVCard;
import zswi.protocols.communication.core.DavStore;
import zswi.protocols.communication.core.DavStore.DateNotUtc;
import zswi.protocols.communication.core.DavStore.DavStoreException;
import zswi.protocols.communication.core.DavStore.NotImplemented;
import zswi.protocols.communication.core.DavStore.NotSupportedComponent;
import zswi.protocols.communication.core.DavStore.UidConflict;
import zswi.protocols.communication.core.HTTPConnectionManager;
import zswi.protocols.communication.core.Utilities;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.ProppatchRequest;
import zswi.protocols.communication.core.requests.PutRequest;
import zswi.protocols.communication.core.requests.ReportRequest;
import zswi.schemas.caldav.proppatch.ScheduleCalendarTransp;
import zswi.schemas.caldav.query.CalendarQuery;
import zswi.schemas.dav.icalendarobjects.Response;

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
  HTTPConnectionManager connectionManager;
  String calendarDescription;

  public CalendarCollection(String uri) {
    setUri(uri);
  }
  
  public CalendarCollection(HTTPConnectionManager _connectionManager) {
    connectionManager = _connectionManager;
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

  public void setCalendarTimezone(net.fortuna.ical4j.model.Calendar calendarTimezone) {
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

  public void setScheduleCalendarTransp(String scheduleCalendarTransp) {
    this.scheduleCalendarTransp = scheduleCalendarTransp;
  }

  public String getCalendarColor() {
    return calendarColor;
  }

  public void setCalendarColor(String calendarColor) {
    this.calendarColor = calendarColor;
  }

  public BigInteger getCalendarOrder() {
    return calendarOrder;
  }

  public void setCalendarOrder(BigInteger calendarOrder) {
    this.calendarOrder = calendarOrder;
  }

  public String getCalendarDescription() {
    return calendarDescription;
  }

  public void setCalendarDescription(String calendarDescription) {
    this.calendarDescription = calendarDescription;
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
  
  /**
   * @deprecated Use getVCalendars instead
   * 
   */
  public List<ServerVEvent> getVEvents() throws DavStoreException {

    ArrayList<ServerVEvent> result = new ArrayList<ServerVEvent>();
    List<ServerVCalendar> calendarObjects = getVCalendars();

    for (ServerVCalendar vCalendar: calendarObjects) {
      Object event = vCalendar.getVCalendar().getComponent(Component.VEVENT);
      if (event != null) {
        ServerVEvent calendarObject = new ServerVEvent((VEvent)event, vCalendar.geteTag(), vCalendar.getPath());
        result.add(calendarObject);
      }
    }

    return result;
  }

  /**
   * Get the list of all iCalendar objects from a calendar collection.
   * 
   * @param calendar
   * @return
   * @throws DavStoreException
   */
  public List<ServerVCalendar> getVCalendars() throws DavStoreException {
    ArrayList<ServerVCalendar> result = new ArrayList<ServerVCalendar>();

    String path = getUri();

    String response = "";
    try {
      response = DavStore.report(connectionManager, "rep_events.txt", path, 1);
    }
    catch (NotImplemented e1) {
      e1.printStackTrace();
    }

    JAXBContext jc;
    try {
      jc = JAXBContext.newInstance("zswi.schemas.dav.icalendarobjects");
      Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
      StringReader reader = new StringReader(response);
      zswi.schemas.dav.icalendarobjects.Multistatus multistatus = (zswi.schemas.dav.icalendarobjects.Multistatus)userInfounmarshaller.unmarshal(reader);

      for (Response xmlResponse: multistatus.getResponse()) {
        String hrefForObject = xmlResponse.getHref();
        for (zswi.schemas.dav.icalendarobjects.Propstat propstat: xmlResponse.getPropstat()) {
          if (DavStore.PROPSTAT_OK.equals(propstat.getStatus())) {
            StringReader sin = new StringReader(propstat.getProp().getCalendarData());
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendarData = builder.build(sin);
            ServerVCalendar calendarObject = new ServerVCalendar(calendarData, propstat.getProp().getGetetag(), hrefForObject);
            result.add(calendarObject);
          }
        }
      }
    }
    catch (JAXBException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (IOException e) {
      throw new DavStoreException(e.getMessage());
    }
    catch (ParserException e) {
      throw new DavStoreException(e.getMessage());
    }

    return result;
  }


  /**
   * @deprecated You should use addVCalendar instead
   * @param collection
   * @param event
   * @return
   * @throws DavStoreException
   * @throws NotSupportedComponent 
   * @throws UidConflict 
   * @throws ValidationException 
   */
  public ServerVEvent addVEvent(VEvent event) throws DavStoreException, UidConflict, NotSupportedComponent, ValidationException {
    Calendar calendarForEvent = new Calendar();
    calendarForEvent.getComponents().add(event);

    ServerVCalendar vCalendar = addVCalendar(calendarForEvent);
    return new ServerVEvent(event, vCalendar.geteTag(), vCalendar.getPath());
  }

  /**
   * 
   * @param collection
   * @param calendar
   * @return
   * @throws DavStoreException
   * @throws UidConflict 
   * @throws NotSupportedComponent 
   * @throws ValidationException 
   */
  public ServerVCalendar addVCalendar(Calendar calendar) throws DavStoreException, UidConflict, NotSupportedComponent, ValidationException {
    StringEntity se;
    try {
      se = new StringEntity(calendar.toString());
      se.setContentType(DavStore.TYPE_CALENDAR);

      String uid = Utilities.checkComponentsValidity(this, calendar);

      URI urlForRequest = connectionManager.initUri(getUri() + uid + ".ics");
      PutRequest putReq = new PutRequest(urlForRequest);
      putReq.setEntity(se);

      HttpResponse resp = connectionManager.getHttpClient().execute(putReq);
      EntityUtils.consume(resp.getEntity());

      String path = urlForRequest.getPath();

      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {

        Header[] headers = resp.getHeaders("Etag");
        String etag = "";
        if (headers.length == 1) {
          etag = headers[0].getValue();
        }

        Header[] locations = resp.getHeaders("Location");
        if (locations.length == 1) {
          try {
            URL locationUrl = new URL(locations[0].getValue());
            path = locationUrl.getPath();
          } catch (MalformedURLException urle) {
            // It might be just a path, so let's take this instead
            if (locations[0].getValue().length() > 0)
              path = locations[0].getValue();
          }
        }

        if (etag == null || "".equals(etag)) {
          try {
            URL propfindUrl = new URL(connectionManager.httpScheme(), connectionManager.getServerName(), connectionManager.getPort(), path);
            PropfindRequest propFindRequest = new PropfindRequest(propfindUrl.toURI(), 0);
            InputStream is = ClassLoader.getSystemResourceAsStream("propfind-etag-request.xml");

            StringEntity propFindBoby = new StringEntity(Utilities.convertStreamToString(is));

            propFindBoby.setContentType("text/xml");
            propFindRequest.setEntity(propFindBoby);

            HttpResponse response = connectionManager.getHttpClient().execute(propFindRequest);
            int status = response.getStatusLine().getStatusCode();
            if (status < 300) {
              JAXBContext jc;
              try {
                jc = JAXBContext.newInstance("zswi.schemas.dav.propfind.etag");
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                zswi.schemas.dav.propfind.etag.Multistatus unmarshal = (zswi.schemas.dav.propfind.etag.Multistatus)unmarshaller.unmarshal(response.getEntity().getContent());

                EntityUtils.consume(response.getEntity());

                List<zswi.schemas.dav.propfind.etag.Response> responses = unmarshal.getResponse();
                for (zswi.schemas.dav.propfind.etag.Response xmlResponse: responses) {
                  if (DavStore.PROPSTAT_OK.equals(xmlResponse.getPropstat().getStatus())) {
                    etag = xmlResponse.getPropstat().getProp().getGetetag();
                  }
                }            
              } catch (JAXBException e) {
                e.printStackTrace();
              }
            } else {
              EntityUtils.consume(response.getEntity());
            }

          }
          catch (MalformedURLException e1) {
            e1.printStackTrace();
          }
          catch (URISyntaxException e1) {
            e1.printStackTrace();
          }
          catch (IOException e) {
            e.printStackTrace();
          }
        }        

        ServerVCalendar vcalendar = new ServerVCalendar(calendar, etag, path, this);
        
        return vcalendar;
      } else {
        throw new DavStoreException("Can't create the calendar object, returned status code is " + resp.getStatusLine().getStatusCode());
      }
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }
  }

  /**
   * @deprecated Use updateVCalendar instead
   * @param event
   * @return
   * @throws DavStoreException
   */
  public void updateVEvent(ServerVEvent event) throws DavStoreException {
    // vEvent must be enclosed in Calendar, otherwise is not added
    Calendar calendarForEvent = new Calendar();
    calendarForEvent.getComponents().add(event.getVevent());

    StringEntity se = null;
    try {
      se = new StringEntity(calendarForEvent.toString());
      se.setContentType(DavStore.TYPE_CALENDAR);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }

    String newEtag = this.updateObject(connectionManager, se, event.geteTag(), event.getPath());
    if (newEtag != null) {
      event.seteTag(newEtag);
    }
  }

  /**
   * Update an iCalendar object by pushing it (PUT request) to the server.
   * 
   * @param calendar
   * @throws DavStoreException
   * @throws ValidationException 
   * @throws UidConflict 
   * @throws NotSupportedComponent 
   */
  public void updateVCalendar(ServerVCalendar calendar) throws DavStoreException, NotSupportedComponent, UidConflict, ValidationException {
    // vEvent must be enclosed in Calendar, otherwise is not added

    Utilities.checkComponentsValidity(calendar.getParentCollection(), calendar.getVCalendar());

    StringEntity se = null;
    try {
      se = new StringEntity(calendar.getVCalendar().toString());
      se.setContentType(DavStore.TYPE_CALENDAR);
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }

    String newEtag = this.updateObject(connectionManager, se, calendar.geteTag(), calendar.getPath());
    if (newEtag != null) {
      calendar.seteTag(newEtag);
    }
  }

  /**
   * @deprecated Use deleteVCalendar instead
   */
  public boolean deleteVEvent(ServerVEvent event) throws ClientProtocolException, URISyntaxException, IOException {
    return this.delete(connectionManager, event.getPath(), event.geteTag());
  }

  /**
   * Delete a iCalendar object by doing a DELETE request on the path of the iCalendar object on the server.
   * 
   * @param calendar
   * @return
   * @throws ClientProtocolException
   * @throws URISyntaxException
   * @throws IOException
   */
  public boolean deleteVCalendar(ServerVCalendar calendar) throws ClientProtocolException, URISyntaxException, IOException {
    return this.delete(connectionManager, calendar.getPath(), calendar.geteTag());
  }

  /**
   * This method will do a calendar-query for the specified time range. It's a common query that many people do.
   * 
   * @param collection
   * @param startTime
   * @param endTime
   * @return
   * @throws DateNotUtc This exception will be raised if the start or end datetime are not set in UTC (date.setUtc(true))
   */
  public List<ServerVCalendar> getEventsForTimePeriod(DateTime startTime, DateTime endTime) throws DateNotUtc {

    if ((!startTime.isUtc()) || (!endTime.isUtc())) {
      throw new DateNotUtc("Dates must be set to UTC");
    }

    List<ServerVCalendar> result = new ArrayList<ServerVCalendar>();

    zswi.schemas.caldav.query.Cprop compSummary = new zswi.schemas.caldav.query.Cprop();
    compSummary.setName("SUMMARY");

    zswi.schemas.caldav.query.Cprop compUid = new zswi.schemas.caldav.query.Cprop();
    compUid.setName("UID");

    zswi.schemas.caldav.query.Comp compVevent = new zswi.schemas.caldav.query.Comp();
    compVevent.setName(Component.VEVENT);
    compVevent.getCompOrCprop().add(compSummary);
    compVevent.getCompOrCprop().add(compUid);

    zswi.schemas.caldav.query.Cprop version = new zswi.schemas.caldav.query.Cprop();
    version.setName("VERSION");

    zswi.schemas.caldav.query.Comp comp = new zswi.schemas.caldav.query.Comp();
    comp.setName("VCALENDAR");
    comp.getCompOrCprop().add(version);
    comp.getCompOrCprop().add(compVevent);

    zswi.schemas.caldav.query.CalendarData calData = new zswi.schemas.caldav.query.CalendarData();
    calData.setComp(comp);

    zswi.schemas.caldav.query.Prop prop = new zswi.schemas.caldav.query.Prop();
    prop.setCalendarData(calData);

    zswi.schemas.caldav.query.TimeRange timeRangeFilter = new zswi.schemas.caldav.query.TimeRange();
    timeRangeFilter.setEnd(startTime.toString());
    timeRangeFilter.setStart(endTime.toString());

    zswi.schemas.caldav.query.CompFilter eventFilter = new zswi.schemas.caldav.query.CompFilter();
    eventFilter.setName(Component.VEVENT);
    eventFilter.setTimeRange(timeRangeFilter);

    zswi.schemas.caldav.query.CompFilter calFilter = new zswi.schemas.caldav.query.CompFilter();
    calFilter.setName("VCALENDAR");
    calFilter.setCompFilter(eventFilter);

    zswi.schemas.caldav.query.Filter filter = new zswi.schemas.caldav.query.Filter();
    filter.setCompFilter(calFilter);

    CalendarQuery query = new CalendarQuery();
    query.setProp(prop);
    query.setFilter(filter);

    try {
      result = doCalendarQuery(query);
    }
    catch (NotImplemented e1) {
      e1.printStackTrace();
    }
    catch (JAXBException e) {
      e.printStackTrace();
    }
    catch (DavStoreException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void update() throws JAXBException, DavStoreException {
    zswi.schemas.caldav.proppatch.ObjectFactory factory = new zswi.schemas.caldav.proppatch.ObjectFactory();

    zswi.schemas.caldav.proppatch.ScheduleCalendarTransp transparency = new ScheduleCalendarTransp();

    zswi.schemas.caldav.proppatch.Prop prop = new zswi.schemas.caldav.proppatch.Prop();

    prop.setCalendarColor(getCalendarColor());
    prop.setCalendarOrder(getCalendarOrder());
    prop.setCalendarDescription(getCalendarDescription());
    prop.setDisplayname(getDisplayName());

    String transparencyAsStr = getScheduleCalendarTransp();
    if ("OPAQUE".equals(transparencyAsStr)) 
      transparency.setOpaque(factory.createOpaque());
    else
      transparency.setTransparent(factory.createTransparent());
    prop.setScheduleCalendarTransp(transparency);

    Calendar timezone = getCalendarTimezone();
    if (timezone != null)
      prop.setCalendarTimezone(timezone.toString());

    zswi.schemas.caldav.proppatch.Set set = new zswi.schemas.caldav.proppatch.Set();
    set.setProp(prop);

    zswi.schemas.caldav.proppatch.Propertyupdate propUpdate = factory.createPropertyupdate();
    propUpdate.setSet(set);

    StringWriter sw = new StringWriter();

    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.proppatch");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
    marshaller.marshal(propUpdate, sw);

    StringEntity se;
    try {
      se = new StringEntity(sw.toString());
      String bodyOfResponse = "";
      ProppatchRequest req = new ProppatchRequest(connectionManager.initUri(getUri()), 0);
      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = connectionManager.getHttpClient().execute(req);

      bodyOfResponse += EntityUtils.toString(resp.getEntity());

      int statusCode = resp.getStatusLine().getStatusCode();

      if (statusCode == HttpStatus.SC_FORBIDDEN) {
        jc = JAXBContext.newInstance("zswi.schemas.caldav.errors");
        Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
        StringReader reader = new StringReader(bodyOfResponse);
        zswi.schemas.caldav.errors.Error error = (zswi.schemas.caldav.errors.Error)userInfounmarshaller.unmarshal(reader);

        EntityUtils.consume(resp.getEntity());

        if (error.getErrorDescription() != null) {
          throw new DavStoreException(error.getErrorDescription());
        }
      } else {
        EntityUtils.consume(resp.getEntity());
        if (!(statusCode == HttpStatus.SC_MULTI_STATUS)) {
          throw new DavStoreException("We couldn't update the calendar collection, the server have sent : " + resp.getStatusLine().getReasonPhrase());
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (ParseException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }

  }
  
  protected List<ServerVCalendar> doCalendarQuery(CalendarQuery query) throws JAXBException, DavStoreException, NotImplemented {
    StringWriter sw = new StringWriter();
    List<ServerVCalendar> result = new ArrayList<ServerVCalendar>();

    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.query");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
    marshaller.marshal(query, sw);

    StringEntity se;
    try {
      se = new StringEntity(sw.toString());
      String bodyOfResponse = "";
      ReportRequest req = new ReportRequest(connectionManager.initUri(getUri()), 0);
      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = connectionManager.getHttpClient().execute(req);

      bodyOfResponse += EntityUtils.toString(resp.getEntity());

      int statusCode = resp.getStatusLine().getStatusCode();

      if (statusCode == HttpStatus.SC_MULTI_STATUS) {
        jc = JAXBContext.newInstance("zswi.schemas.caldav.query.response");
        Unmarshaller userInfounmarshaller = jc.createUnmarshaller();

        zswi.schemas.caldav.query.response.Multistatus multistatus = (zswi.schemas.caldav.query.response.Multistatus)userInfounmarshaller.unmarshal(new StringReader(bodyOfResponse));
        EntityUtils.consume(resp.getEntity());

        for (zswi.schemas.caldav.query.response.Response response: multistatus.getResponse()) {
          String href = response.getHref();
          for (zswi.schemas.caldav.query.response.Propstat propstat: response.getPropstat()) {
            if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
              String eTag = propstat.getProp().getGetetag();

              StringReader sin = new StringReader(propstat.getProp().getCalendarData());
              CalendarBuilder builder = new CalendarBuilder();
              Calendar calendarData = builder.build(sin);

              ServerVCalendar calendarObject = new ServerVCalendar(calendarData, eTag, href, this);
              result.add(calendarObject);
            }
          }
        } 
      } else {
        if (statusCode == HttpStatus.SC_FORBIDDEN) {
          jc = JAXBContext.newInstance("zswi.schemas.caldav.errors");
          Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
          StringReader reader = new StringReader(bodyOfResponse);
          zswi.schemas.caldav.errors.Error error = (zswi.schemas.caldav.errors.Error)userInfounmarshaller.unmarshal(reader);

          EntityUtils.consume(resp.getEntity());

          if (error.getErrorDescription() != null) {
            throw new DavStoreException(error.getErrorDescription());
          }
          if (error.getSupportedCalendarData() != null) {
            throw new DavStoreException("The attributes \"content-type\" and \"version\" of the CALDAV:calendar-data XML element specify a media type supported by the server for calendar object resources.");
          }
          if (error.getValidFilter() != null) {
            throw new DavStoreException("The CALDAV:filter XML element specified in the REPORT request MUST be valid.");
          }            
          if (error.getValidCalendarData() != null) {
            throw new DavStoreException("The time zone specified in the REPORT request MUST be a valid iCalendar object containing a single valid VTIMEZONE component.");
          }
          if (error.getMinDateTime() != null) {
            throw new DavStoreException("The time-range values are greater than or equal to the value of the CALDAV:min-date-time property of the collection.");
          }
          if (error.getMaxDateTime() != null) {
            throw new DavStoreException("The time-range values are less than or equal to the value of the CALDAV:max-date-time property of the collection.");
          }
          if (error.getSupportedCollation() != null) {
            throw new DavStoreException("Any XML attribute specifying a collation MUST specify a collation supported by the server as described in Section 7.5");
          }
          if (error.getNumberOfMatchesWithinLimits() != null) {
            throw new DavStoreException("The number of matching calendar object resources must fall within server-specific, predefined limits.");
          }
          // TODO supported-filter is missing from the list.
        }

        EntityUtils.consume(resp.getEntity());
        throw new DavStoreException("We couldn't create the calendar collection, the server have sent : " + resp.getStatusLine().getReasonPhrase());
      } 
    }
    catch (UnsupportedEncodingException e) {
      throw new DavStoreException(e);
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (ParseException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }
    catch (ParserException e) {
      throw new DavStoreException(e);
    }

    return result;
  }


   
}
