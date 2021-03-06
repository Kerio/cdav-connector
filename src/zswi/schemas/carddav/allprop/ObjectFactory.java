
package zswi.schemas.carddav.allprop;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.carddav.allprop package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _QuotaUsedBytes_QNAME = new QName("DAV:", "quota-used-bytes");
    private final static QName _Getcontenttype_QNAME = new QName("DAV:", "getcontenttype");
    private final static QName _QuotaAvailableBytes_QNAME = new QName("DAV:", "quota-available-bytes");
    private final static QName _ResourceClass_QNAME = new QName("http://twistedmatrix.com/xml_namespace/dav/", "resource-class");
    private final static QName _Getlastmodified_QNAME = new QName("DAV:", "getlastmodified");
    private final static QName _Href_QNAME = new QName("DAV:", "href");
    private final static QName _Displayname_QNAME = new QName("DAV:", "displayname");
    private final static QName _Getetag_QNAME = new QName("DAV:", "getetag");
    private final static QName _SyncToken_QNAME = new QName("DAV:", "sync-token");
    private final static QName _Status_QNAME = new QName("DAV:", "status");
    private final static QName _Getctag_QNAME = new QName("http://calendarserver.org/ns/", "getctag");
    private final static QName _MaxResources_QNAME = new QName("http://calendarserver.org/ns/", "max-resources");
    private final static QName _Pushkey_QNAME = new QName("http://calendarserver.org/ns/", "pushkey");
    private final static QName _MaxResourceSize_QNAME = new QName("urn:ietf:params:xml:ns:carddav", "max-resource-size");
    private final static QName _Creationdate_QNAME = new QName("DAV:", "creationdate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.carddav.allprop
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Supportedlock }
     * 
     */
    public Supportedlock createSupportedlock() {
        return new Supportedlock();
    }

    /**
     * Create an instance of {@link Lockentry }
     * 
     */
    public Lockentry createLockentry() {
        return new Lockentry();
    }

    /**
     * Create an instance of {@link Lockscope }
     * 
     */
    public Lockscope createLockscope() {
        return new Lockscope();
    }

    /**
     * Create an instance of {@link Exclusive }
     * 
     */
    public Exclusive createExclusive() {
        return new Exclusive();
    }

    /**
     * Create an instance of {@link Shared }
     * 
     */
    public Shared createShared() {
        return new Shared();
    }

    /**
     * Create an instance of {@link Locktype }
     * 
     */
    public Locktype createLocktype() {
        return new Locktype();
    }

    /**
     * Create an instance of {@link Write }
     * 
     */
    public Write createWrite() {
        return new Write();
    }

    /**
     * Create an instance of {@link SupportedReportSet }
     * 
     */
    public SupportedReportSet createSupportedReportSet() {
        return new SupportedReportSet();
    }

    /**
     * Create an instance of {@link SupportedReport }
     * 
     */
    public SupportedReport createSupportedReport() {
        return new SupportedReport();
    }

    /**
     * Create an instance of {@link Report }
     * 
     */
    public Report createReport() {
        return new Report();
    }

    /**
     * Create an instance of {@link AclPrincipalPropSet }
     * 
     */
    public AclPrincipalPropSet createAclPrincipalPropSet() {
        return new AclPrincipalPropSet();
    }

    /**
     * Create an instance of {@link ExpandProperty }
     * 
     */
    public ExpandProperty createExpandProperty() {
        return new ExpandProperty();
    }

    /**
     * Create an instance of {@link PrincipalMatch }
     * 
     */
    public PrincipalMatch createPrincipalMatch() {
        return new PrincipalMatch();
    }

    /**
     * Create an instance of {@link PrincipalPropertySearch }
     * 
     */
    public PrincipalPropertySearch createPrincipalPropertySearch() {
        return new PrincipalPropertySearch();
    }

    /**
     * Create an instance of {@link SyncCollection }
     * 
     */
    public SyncCollection createSyncCollection() {
        return new SyncCollection();
    }

    /**
     * Create an instance of {@link CalendarserverPrincipalSearch }
     * 
     */
    public CalendarserverPrincipalSearch createCalendarserverPrincipalSearch() {
        return new CalendarserverPrincipalSearch();
    }

    /**
     * Create an instance of {@link CalendarMultiget }
     * 
     */
    public CalendarMultiget createCalendarMultiget() {
        return new CalendarMultiget();
    }

    /**
     * Create an instance of {@link CalendarQuery }
     * 
     */
    public CalendarQuery createCalendarQuery() {
        return new CalendarQuery();
    }

    /**
     * Create an instance of {@link FreeBusyQuery }
     * 
     */
    public FreeBusyQuery createFreeBusyQuery() {
        return new FreeBusyQuery();
    }

    /**
     * Create an instance of {@link AddressbookMultiget }
     * 
     */
    public AddressbookMultiget createAddressbookMultiget() {
        return new AddressbookMultiget();
    }

    /**
     * Create an instance of {@link AddressbookQuery }
     * 
     */
    public AddressbookQuery createAddressbookQuery() {
        return new AddressbookQuery();
    }

    /**
     * Create an instance of {@link Grant }
     * 
     */
    public Grant createGrant() {
        return new Grant();
    }

    /**
     * Create an instance of {@link Privilege }
     * 
     */
    public Privilege createPrivilege() {
        return new Privilege();
    }

    /**
     * Create an instance of {@link All }
     * 
     */
    public All createAll() {
        return new All();
    }

    /**
     * Create an instance of {@link Bind }
     * 
     */
    public Bind createBind() {
        return new Bind();
    }

    /**
     * Create an instance of {@link Read }
     * 
     */
    public Read createRead() {
        return new Read();
    }

    /**
     * Create an instance of {@link ReadAcl }
     * 
     */
    public ReadAcl createReadAcl() {
        return new ReadAcl();
    }

    /**
     * Create an instance of {@link ReadCurrentUserPrivilegeSet }
     * 
     */
    public ReadCurrentUserPrivilegeSet createReadCurrentUserPrivilegeSet() {
        return new ReadCurrentUserPrivilegeSet();
    }

    /**
     * Create an instance of {@link Unbind }
     * 
     */
    public Unbind createUnbind() {
        return new Unbind();
    }

    /**
     * Create an instance of {@link Unlock }
     * 
     */
    public Unlock createUnlock() {
        return new Unlock();
    }

    /**
     * Create an instance of {@link WriteAcl }
     * 
     */
    public WriteAcl createWriteAcl() {
        return new WriteAcl();
    }

    /**
     * Create an instance of {@link WriteContent }
     * 
     */
    public WriteContent createWriteContent() {
        return new WriteContent();
    }

    /**
     * Create an instance of {@link WriteProperties }
     * 
     */
    public WriteProperties createWriteProperties() {
        return new WriteProperties();
    }

    /**
     * Create an instance of {@link ReadFreeBusy }
     * 
     */
    public ReadFreeBusy createReadFreeBusy() {
        return new ReadFreeBusy();
    }

    /**
     * Create an instance of {@link Resourcetype }
     * 
     */
    public Resourcetype createResourcetype() {
        return new Resourcetype();
    }

    /**
     * Create an instance of {@link Collection }
     * 
     */
    public Collection createCollection() {
        return new Collection();
    }

    /**
     * Create an instance of {@link Addressbook }
     * 
     */
    public Addressbook createAddressbook() {
        return new Addressbook();
    }

    /**
     * Create an instance of {@link Principal }
     * 
     */
    public Principal createPrincipal() {
        return new Principal();
    }

    /**
     * Create an instance of {@link Description }
     * 
     */
    public Description createDescription() {
        return new Description();
    }

    /**
     * Create an instance of {@link SupportedPrivilegeSet }
     * 
     */
    public SupportedPrivilegeSet createSupportedPrivilegeSet() {
        return new SupportedPrivilegeSet();
    }

    /**
     * Create an instance of {@link SupportedPrivilege }
     * 
     */
    public SupportedPrivilege createSupportedPrivilege() {
        return new SupportedPrivilege();
    }

    /**
     * Create an instance of {@link CurrentUserPrincipal }
     * 
     */
    public CurrentUserPrincipal createCurrentUserPrincipal() {
        return new CurrentUserPrincipal();
    }

    /**
     * Create an instance of {@link Propstat }
     * 
     */
    public Propstat createPropstat() {
        return new Propstat();
    }

    /**
     * Create an instance of {@link Prop }
     * 
     */
    public Prop createProp() {
        return new Prop();
    }

    /**
     * Create an instance of {@link Acl }
     * 
     */
    public Acl createAcl() {
        return new Acl();
    }

    /**
     * Create an instance of {@link Ace }
     * 
     */
    public Ace createAce() {
        return new Ace();
    }

    /**
     * Create an instance of {@link Protected }
     * 
     */
    public Protected createProtected() {
        return new Protected();
    }

    /**
     * Create an instance of {@link Inherited }
     * 
     */
    public Inherited createInherited() {
        return new Inherited();
    }

    /**
     * Create an instance of {@link Inheritable }
     * 
     */
    public Inheritable createInheritable() {
        return new Inheritable();
    }

    /**
     * Create an instance of {@link AclRestrictions }
     * 
     */
    public AclRestrictions createAclRestrictions() {
        return new AclRestrictions();
    }

    /**
     * Create an instance of {@link AddMember }
     * 
     */
    public AddMember createAddMember() {
        return new AddMember();
    }

    /**
     * Create an instance of {@link CurrentUserPrivilegeSet }
     * 
     */
    public CurrentUserPrivilegeSet createCurrentUserPrivilegeSet() {
        return new CurrentUserPrivilegeSet();
    }

    /**
     * Create an instance of {@link Getcontentlength }
     * 
     */
    public Getcontentlength createGetcontentlength() {
        return new Getcontentlength();
    }

    /**
     * Create an instance of {@link InheritedAclSet }
     * 
     */
    public InheritedAclSet createInheritedAclSet() {
        return new InheritedAclSet();
    }

    /**
     * Create an instance of {@link Owner }
     * 
     */
    public Owner createOwner() {
        return new Owner();
    }

    /**
     * Create an instance of {@link PrincipalCollectionSet }
     * 
     */
    public PrincipalCollectionSet createPrincipalCollectionSet() {
        return new PrincipalCollectionSet();
    }

    /**
     * Create an instance of {@link ResourceId }
     * 
     */
    public ResourceId createResourceId() {
        return new ResourceId();
    }

    /**
     * Create an instance of {@link AllowedSharingModes }
     * 
     */
    public AllowedSharingModes createAllowedSharingModes() {
        return new AllowedSharingModes();
    }

    /**
     * Create an instance of {@link CanBeShared }
     * 
     */
    public CanBeShared createCanBeShared() {
        return new CanBeShared();
    }

    /**
     * Create an instance of {@link Invite }
     * 
     */
    public Invite createInvite() {
        return new Invite();
    }

    /**
     * Create an instance of {@link SharedUrl }
     * 
     */
    public SharedUrl createSharedUrl() {
        return new SharedUrl();
    }

    /**
     * Create an instance of {@link DefaultAddressbookURL }
     * 
     */
    public DefaultAddressbookURL createDefaultAddressbookURL() {
        return new DefaultAddressbookURL();
    }

    /**
     * Create an instance of {@link SupportedAddressData }
     * 
     */
    public SupportedAddressData createSupportedAddressData() {
        return new SupportedAddressData();
    }

    /**
     * Create an instance of {@link AddressDataType }
     * 
     */
    public AddressDataType createAddressDataType() {
        return new AddressDataType();
    }

    /**
     * Create an instance of {@link Multistatus }
     * 
     */
    public Multistatus createMultistatus() {
        return new Multistatus();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "quota-used-bytes")
    public JAXBElement<BigInteger> createQuotaUsedBytes(BigInteger value) {
        return new JAXBElement<BigInteger>(_QuotaUsedBytes_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "getcontenttype")
    public JAXBElement<String> createGetcontenttype(String value) {
        return new JAXBElement<String>(_Getcontenttype_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "quota-available-bytes")
    public JAXBElement<BigInteger> createQuotaAvailableBytes(BigInteger value) {
        return new JAXBElement<BigInteger>(_QuotaAvailableBytes_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://twistedmatrix.com/xml_namespace/dav/", name = "resource-class")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createResourceClass(String value) {
        return new JAXBElement<String>(_ResourceClass_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "getlastmodified")
    public JAXBElement<String> createGetlastmodified(String value) {
        return new JAXBElement<String>(_Getlastmodified_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "href")
    public JAXBElement<String> createHref(String value) {
        return new JAXBElement<String>(_Href_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "displayname")
    public JAXBElement<String> createDisplayname(String value) {
        return new JAXBElement<String>(_Displayname_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "getetag")
    public JAXBElement<String> createGetetag(String value) {
        return new JAXBElement<String>(_Getetag_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "sync-token")
    public JAXBElement<String> createSyncToken(String value) {
        return new JAXBElement<String>(_SyncToken_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "status")
    public JAXBElement<String> createStatus(String value) {
        return new JAXBElement<String>(_Status_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://calendarserver.org/ns/", name = "getctag")
    public JAXBElement<String> createGetctag(String value) {
        return new JAXBElement<String>(_Getctag_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://calendarserver.org/ns/", name = "max-resources")
    public JAXBElement<String> createMaxResources(String value) {
        return new JAXBElement<String>(_MaxResources_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://calendarserver.org/ns/", name = "pushkey")
    public JAXBElement<String> createPushkey(String value) {
        return new JAXBElement<String>(_Pushkey_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:carddav", name = "max-resource-size")
    public JAXBElement<String> createMaxResourceSize(String value) {
        return new JAXBElement<String>(_MaxResourceSize_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "creationdate")
    public JAXBElement<XMLGregorianCalendar> createCreationdate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_Creationdate_QNAME, XMLGregorianCalendar.class, null, value);
    }

}
