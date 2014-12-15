package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import zswi.objects.dav.enums.AutoScheduleMode;
import zswi.objects.dav.enums.RecordType;
import zswi.protocols.communication.core.DavStore;
import zswi.protocols.communication.core.Utilities;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.ReportRequest;
import zswi.schemas.dav.userinfo.AddressbookHomeSet;
import zswi.schemas.dav.userinfo.Multistatus;
import zswi.schemas.dav.userinfo.Propstat;
import zswi.schemas.dav.userinfo.ScheduleInboxURL;
import zswi.schemas.dav.userinfo.ScheduleOutboxURL;

public class PrincipalCollection extends AbstractDavCollection {

  /** http://tools.ietf.org/html/rfc4791#section-6.2.1 */
  CalendarHomeSet calendarHomeSet;
  java.net.URI calendarHomeSetUrl;
  
  /** http://tools.ietf.org/html/rfc6638#section-2.4.1 */
  java.util.List<PrincipalCollection> calendarUserAddressSet;
  
  /** http://tools.ietf.org/html/rfc6638#section-2.4.2 */
  net.fortuna.ical4j.model.parameter.CuType calendarUserType;
  
  /** http://tools.ietf.org/html/rfc6638#section-2.2.1 */
  InboxCollection scheduleInbox;

  /** http://tools.ietf.org/html/rfc6638#section-2.1.1 */
  OutboxCollection scheduleOutbox;
  java.net.URI scheduleOutboxURL;

  /** http://tools.ietf.org/html/rfc6352#section-7.1.1 */
  AddressBookHomeSet addressbookHomeSet;
  java.net.URI addressbookHomeSetUrl;

  /** namespace: http://calendarserver.org/ns/ */
  boolean autoSchedule;

  /** namespace: http://calendarserver.org/ns/ */
  AutoScheduleMode autoScheduleMode;
  
  /** http://svn.calendarserver.org/repository/calendarserver/CalendarServer/trunk/doc/Extensions/caldav-proxy.txt */
  java.util.List<PrincipalCollection> calendarProxyReadFor;

  /** http://svn.calendarserver.org/repository/calendarserver/CalendarServer/trunk/doc/Extensions/caldav-proxy.txt */
  java.util.List<PrincipalCollection> calendarProxyWriteFor;
  
  /** http://calendarserver.org/ns/ */
  DropboxCollection dropboxCollection;
  
  /** http://svn.calendarserver.org/repository/calendarserver/CalendarServer/trunk/doc/Extensions/caldav-sharing.txt */
  String firstName;
  
  /** http://svn.calendarserver.org/repository/calendarserver/CalendarServer/trunk/doc/Extensions/caldav-sharing.txt */
  String lastName;
  
  /** http://svn.calendarserver.org/repository/calendarserver/CalendarServer/trunk/doc/Extensions/caldav-sharing.txt */
  NotificationCollection notificationCollection;
  
  /** namespace: http://calendarserver.org/ns/ */
  RecordType recordType;
  
  /** http://tools.ietf.org/html/rfc3744#section-4.2 */
  java.net.URI principalURL;
    
  /*
  email-address-set
  expanded-group-member-set
  java.util.List<java.net.URI> expandedGroupMembership;
  java.util.List<java.net.URI> directoryGateway;
  java.util.List<java.net.URI> alternateURISet;
  java.util.List<java.net.URI> groupMemberSset;
  java.util.List<java.net.URI> groupMembership;
  */
  
  public PrincipalCollection(DavStore store, URI uri, boolean isFakePrincipals, boolean isFirstLevel) throws JAXBException, URISyntaxException, ClientProtocolException, IOException {
    
    PropfindRequest req = new PropfindRequest(uri, 0);
    InputStream is = ClassLoader.getSystemResourceAsStream("userinfo-request.xml");

    StringEntity se = new StringEntity(Utilities.convertStreamToString(is));

    se.setContentType("text/xml");
    req.setEntity(se);

    HttpResponse resp = store.httpClient().execute(req);
    
    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.dav.userinfo");
    Unmarshaller userInfounmarshaller = jc.createUnmarshaller();

    Multistatus multistatus = (Multistatus)userInfounmarshaller.unmarshal(resp.getEntity().getContent());
    EntityUtils.consume(resp.getEntity());
    
    for (Propstat propstat: multistatus.getResponse().getPropstat()) {
      if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
        
        if (isFakePrincipals) {
          setUri(uri.getPath());
        } else {
          setUri(propstat.getProp().getPrincipalURL().getHref());
        }
        
        Utilities.fetchFeatures(store.httpClient(), uri, this);
        
        displayName = propstat.getProp().getDisplayname();
        
        zswi.schemas.dav.userinfo.CalendarHomeSet calHomeSet = propstat.getProp().getCalendarHomeSet();
        if (calHomeSet != null)
          calendarHomeSetUrl = new java.net.URI(propstat.getProp().getCalendarHomeSet().getHref());
        
        AddressbookHomeSet adHomeSet = propstat.getProp().getAddressbookHomeSet();
        if (adHomeSet != null)
          addressbookHomeSetUrl = new java.net.URI(adHomeSet.getHref());
        
        if (isFirstLevel) {
          // TODO Check if feature is available
          HashMap<String, List<PrincipalCollection>> proxiedCollections = fetchProxiedCollections(store, isFakePrincipals);

          if (proxiedCollections.get("write") != null)
            for (PrincipalCollection writeForCollection: proxiedCollections.get("write"))
              getCalendarProxyWriteFor().add(writeForCollection);

          if (proxiedCollections.get("read") != null)
            for (PrincipalCollection readForCollection: proxiedCollections.get("read"))
              getCalendarProxyReadFor().add(readForCollection);
        }
        
        ScheduleInboxURL inboxUrl = propstat.getProp().getScheduleInboxURL();
        if (inboxUrl != null) {
          InboxCollection inbox = new InboxCollection(store.httpClient(), this, store.initUri(inboxUrl.getHref()));
          this.setInboxCollection(inbox);
        }
        
        ScheduleOutboxURL outboxUrl = propstat.getProp().getScheduleOutboxURL();
        if (outboxUrl != null) {
          OutboxCollection outbox = new OutboxCollection(store.httpClient(), this, store.initUri(outboxUrl.getHref()));
          this.setOutboxCollection(outbox);
        }
      }
    }
  }
  
  public HashMap<String, List<PrincipalCollection>> fetchProxiedCollections(DavStore store, boolean isFakePrincipals) {
    HashMap<String, List<PrincipalCollection>> collections = new HashMap<String, List<PrincipalCollection>>();
    
    ReportRequest req;
    try {
      req = new ReportRequest(store.initUri(uri), 0);
      InputStream is = ClassLoader.getSystemResourceAsStream("calendar-proxies-request.xml");

      StringEntity se = new StringEntity(Utilities.convertStreamToString(is));

      se.setContentType("text/xml");
      req.setEntity(se);

      HttpResponse resp = store.httpClient().execute(req);
      
      JAXBContext jc = JAXBContext.newInstance("zswi.schemas.dav.proxies");
      Unmarshaller userInfounmarshaller = jc.createUnmarshaller();
      
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_MULTI_STATUS) {
        zswi.schemas.dav.proxies.Multistatus multistatus = (zswi.schemas.dav.proxies.Multistatus)userInfounmarshaller.unmarshal(resp.getEntity().getContent());
        EntityUtils.consume(resp.getEntity());
        
        for (zswi.schemas.dav.proxies.Propstat propstat: multistatus.getResponse().getPropstat()) {
          if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
            for (zswi.schemas.dav.proxies.Prop prop: propstat.getProp()) {
              zswi.schemas.dav.proxies.CalendarProxyReadFor proxyRead = prop.getCalendarProxyReadFor();
              if (proxyRead != null) {
                ArrayList<PrincipalCollection> readCollections = parseProxyResponses(proxyRead.getResponse(), store, isFakePrincipals);
                collections.put("read", readCollections);
              }

              zswi.schemas.dav.proxies.CalendarProxyWriteFor proxyWrite = prop.getCalendarProxyWriteFor();
              if (proxyWrite != null) {
                ArrayList<PrincipalCollection> writeCollections = parseProxyResponses(proxyWrite.getResponse(), store, isFakePrincipals);
                collections.put("write", writeCollections);
              }
            }
          }
        }
      } else {
        EntityUtils.consume(resp.getEntity());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return collections;
  }
  
  protected ArrayList<PrincipalCollection> parseProxyResponses(List<zswi.schemas.dav.proxies.Response> responses, DavStore store, boolean isFakePrincipals) {
    ArrayList<PrincipalCollection> collections = new ArrayList<PrincipalCollection>();
    try {
      for (zswi.schemas.dav.proxies.Response response: responses) {
        String collectionHref = response.getHref();
        PrincipalCollection collection = new PrincipalCollection(store, store.initUri(collectionHref), isFakePrincipals, false);
        collections.add(collection);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return collections;
  }
  
  public CalendarHomeSet getCalendarHomeSet() {
    return calendarHomeSet;
  }
  
  protected void setCalendarHomeSet(CalendarHomeSet _calendarHomeSet) {
    calendarHomeSet = _calendarHomeSet;
  }
  
  public java.net.URI getCalendarHomeSetUrl() {
    return calendarHomeSetUrl;
  }
  
  public java.util.List<PrincipalCollection> getCalendarUserAddressSet() {
    return calendarUserAddressSet;
  }
  
  protected void setCalendarUserAddressSet(java.util.List<PrincipalCollection> _calendarUserAddressSet) {
    calendarUserAddressSet = _calendarUserAddressSet;
  }
  
  public net.fortuna.ical4j.model.parameter.CuType getCalendarUserType() {
    return calendarUserType;
  }
  
  protected void setCalendarUserType(net.fortuna.ical4j.model.parameter.CuType _calendarUserType) {
    calendarUserType = _calendarUserType;
  }
  
  public InboxCollection getScheduleInbox() {
    return scheduleInbox;
  }
  
  protected void setInboxCollection(InboxCollection _scheduleInbox) {
    this.scheduleInbox = _scheduleInbox;
  }
  
  public OutboxCollection getScheduleOutbox() {
    return scheduleOutbox;
  }
  
  protected void setOutboxCollection(OutboxCollection _scheduleOutbox) {
    this.scheduleOutbox = _scheduleOutbox;
  }
  
  public AddressBookHomeSet getAddressbookHomeSet() {
    return addressbookHomeSet;
  }
  
  protected void setAddressBookHomeSet(AddressBookHomeSet _addressbookHomeSet) {
    addressbookHomeSet = _addressbookHomeSet;
  }
  
  public java.net.URI getAddressbookHomeSetUrl() {
    return addressbookHomeSetUrl;
  }
  
  public boolean isAutoSchedule() {
    return autoSchedule;
  }
  
  public AutoScheduleMode getAutoScheduleMode() {
    return autoScheduleMode;
  }
  
  public java.util.List<PrincipalCollection> getCalendarProxyReadFor() {
    if (calendarProxyReadFor == null) {
      calendarProxyReadFor = new ArrayList<PrincipalCollection>();
    }
    return calendarProxyReadFor;
  }
  
  public java.util.List<PrincipalCollection> getCalendarProxyWriteFor() {
    if (calendarProxyWriteFor == null) {
      calendarProxyWriteFor = new ArrayList<PrincipalCollection>();
    }
    return calendarProxyWriteFor;
  }
  
  public DropboxCollection getDropboxCollection() {
    return dropboxCollection;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public NotificationCollection getNotificationCollection() {
    return notificationCollection;
  }
  
  public RecordType getRecordType() {
    return recordType;
  }
  
  public java.net.URI getPrincipalURL() {
    return principalURL;
  }

}
