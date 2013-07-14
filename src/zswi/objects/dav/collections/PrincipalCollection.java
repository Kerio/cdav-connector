package zswi.objects.dav.collections;

import java.io.InputStream;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import zswi.objects.dav.enums.AutoScheduleMode;
import zswi.objects.dav.enums.RecordType;
import zswi.schemas.dav.userinfo.Multistatus;
import zswi.schemas.dav.userinfo.Propstat;

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
  
  /** http://tools.ietf.org/html/rfc6352#section-7.1.1 */
  AddressBookHomeSet addressbookHomeSet;
  
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
  
  public PrincipalCollection(InputStream content) throws JAXBException, URISyntaxException {
    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.dav.userinfo");
    Unmarshaller userInfounmarshaller = jc.createUnmarshaller();

    Multistatus multistatus = (Multistatus)userInfounmarshaller.unmarshal(content);
    for (Propstat propstat: multistatus.getResponse().getPropstat()) {
      if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
        displayName = propstat.getProp().getDisplayname();
        calendarHomeSetUrl = new java.net.URI(propstat.getProp().getCalendarHomeSet().getHref());
        setUri(propstat.getProp().getPrincipalURL().getHref());
        System.out.println(propstat.getProp().getDropboxHomeURL().getHref());
        System.out.println(propstat.getProp().getEmailAddressSet());
        System.out.println(propstat.getProp().getResourceId().getHref());
      }
    }
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
  
  public OutboxCollection getScheduleOutbox() {
    return scheduleOutbox;
  }
  
  public AddressBookHomeSet getAddressbookHomeSet() {
    return addressbookHomeSet;
  }
  
  public boolean isAutoSchedule() {
    return autoSchedule;
  }
  
  public AutoScheduleMode getAutoScheduleMode() {
    return autoScheduleMode;
  }
  
  public java.util.List<PrincipalCollection> getCalendarProxyReadFor() {
    return calendarProxyReadFor;
  }
  
  public java.util.List<PrincipalCollection> getCalendarProxyWriteFor() {
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
