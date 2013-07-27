
package zswi.schemas.carddav.allprop;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{DAV}acl"/>
 *           &lt;element ref="{DAV}acl-restrictions"/>
 *         &lt;/sequence>
 *         &lt;element ref="{DAV}add-member" minOccurs="0"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{DAV}creationdate"/>
 *           &lt;element ref="{DAV}current-user-principal"/>
 *           &lt;element ref="{DAV}current-user-privilege-set"/>
 *         &lt;/sequence>
 *         &lt;element ref="{DAV}displayname" minOccurs="0"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{DAV}getcontentlength"/>
 *           &lt;element ref="{DAV}getcontenttype"/>
 *           &lt;element ref="{DAV}getetag"/>
 *           &lt;element ref="{DAV}getlastmodified"/>
 *           &lt;element ref="{DAV}inherited-acl-set"/>
 *           &lt;element ref="{DAV}owner"/>
 *           &lt;element ref="{DAV}principal-collection-set"/>
 *           &lt;element ref="{DAV}quota-available-bytes"/>
 *           &lt;element ref="{DAV}quota-used-bytes"/>
 *         &lt;/sequence>
 *         &lt;element ref="{DAV}resource-id" minOccurs="0"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{DAV}resourcetype"/>
 *           &lt;element ref="{DAV}supported-privilege-set"/>
 *           &lt;element ref="{DAV}supported-report-set"/>
 *           &lt;element ref="{DAV}supportedlock"/>
 *           &lt;element ref="{DAV}sync-token"/>
 *         &lt;/sequence>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{http://calendarserver.org/ns/}allowed-sharing-modes"/>
 *           &lt;element ref="{http://calendarserver.org/ns/}getctag"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://calendarserver.org/ns/}invite" minOccurs="0"/>
 *         &lt;element ref="{http://calendarserver.org/ns/}max-resources" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://calendarserver.org/ns/}shared-url"/>
 *           &lt;sequence>
 *             &lt;element ref="{http://calendarserver.org/ns/}pushkey"/>
 *             &lt;element ref="{http://twistedmatrix.com/xml_namespace/dav/}resource-class"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element ref="{urn:ietf:params:xml:ns:carddav}default-addressbook-URL"/>
 *           &lt;sequence>
 *             &lt;element ref="{urn:ietf:params:xml:ns:carddav}max-resource-size"/>
 *             &lt;element ref="{urn:ietf:params:xml:ns:carddav}supported-address-data"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "acl",
    "aclRestrictions",
    "addMember",
    "creationdate",
    "currentUserPrincipal",
    "currentUserPrivilegeSet",
    "displayname",
    "getcontentlength",
    "getcontenttype",
    "getetag",
    "getlastmodified",
    "inheritedAclSet",
    "owner",
    "principalCollectionSet",
    "quotaAvailableBytes",
    "quotaUsedBytes",
    "resourceId",
    "resourcetype",
    "supportedPrivilegeSet",
    "supportedReportSet",
    "supportedlock",
    "syncToken",
    "allowedSharingModes",
    "getctag",
    "invite",
    "maxResources",
    "sharedUrl",
    "pushkey",
    "resourceClass",
    "defaultAddressbookURL",
    "maxResourceSize",
    "supportedAddressData"
})
@XmlRootElement(name = "prop")
public class Prop {

    protected Acl acl;
    @XmlElement(name = "acl-restrictions")
    protected AclRestrictions aclRestrictions;
    @XmlElement(name = "add-member")
    protected AddMember addMember;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationdate;
    @XmlElement(name = "current-user-principal")
    protected CurrentUserPrincipal currentUserPrincipal;
    @XmlElement(name = "current-user-privilege-set")
    protected CurrentUserPrivilegeSet currentUserPrivilegeSet;
    protected String displayname;
    protected Getcontentlength getcontentlength;
    protected String getcontenttype;
    protected String getetag;
    protected String getlastmodified;
    @XmlElement(name = "inherited-acl-set")
    protected InheritedAclSet inheritedAclSet;
    protected Owner owner;
    @XmlElement(name = "principal-collection-set")
    protected PrincipalCollectionSet principalCollectionSet;
    @XmlElement(name = "quota-available-bytes")
    protected BigInteger quotaAvailableBytes;
    @XmlElement(name = "quota-used-bytes")
    protected BigInteger quotaUsedBytes;
    @XmlElement(name = "resource-id")
    protected ResourceId resourceId;
    protected Resourcetype resourcetype;
    @XmlElement(name = "supported-privilege-set")
    protected SupportedPrivilegeSet supportedPrivilegeSet;
    @XmlElement(name = "supported-report-set")
    protected SupportedReportSet supportedReportSet;
    protected Supportedlock supportedlock;
    @XmlElement(name = "sync-token")
    @XmlSchemaType(name = "anyURI")
    protected String syncToken;
    @XmlElement(name = "allowed-sharing-modes", namespace = "http://calendarserver.org/ns/")
    protected AllowedSharingModes allowedSharingModes;
    @XmlElement(namespace = "http://calendarserver.org/ns/")
    protected String getctag;
    @XmlElement(namespace = "http://calendarserver.org/ns/")
    protected Invite invite;
    @XmlElement(name = "max-resources", namespace = "http://calendarserver.org/ns/")
    protected String maxResources;
    @XmlElement(name = "shared-url", namespace = "http://calendarserver.org/ns/")
    protected SharedUrl sharedUrl;
    @XmlElement(namespace = "http://calendarserver.org/ns/")
    protected String pushkey;
    @XmlElement(name = "resource-class", namespace = "http://twistedmatrix.com/xml_namespace/dav/")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String resourceClass;
    @XmlElement(name = "default-addressbook-URL", namespace = "urn:ietf:params:xml:ns:carddav")
    protected DefaultAddressbookURL defaultAddressbookURL;
    @XmlElement(name = "max-resource-size", namespace = "urn:ietf:params:xml:ns:carddav")
    protected String maxResourceSize;
    @XmlElement(name = "supported-address-data", namespace = "urn:ietf:params:xml:ns:carddav")
    protected SupportedAddressData supportedAddressData;

    /**
     * Gets the value of the acl property.
     * 
     * @return
     *     possible object is
     *     {@link Acl }
     *     
     */
    public Acl getAcl() {
        return acl;
    }

    /**
     * Sets the value of the acl property.
     * 
     * @param value
     *     allowed object is
     *     {@link Acl }
     *     
     */
    public void setAcl(Acl value) {
        this.acl = value;
    }

    /**
     * Gets the value of the aclRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link AclRestrictions }
     *     
     */
    public AclRestrictions getAclRestrictions() {
        return aclRestrictions;
    }

    /**
     * Sets the value of the aclRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link AclRestrictions }
     *     
     */
    public void setAclRestrictions(AclRestrictions value) {
        this.aclRestrictions = value;
    }

    /**
     * Gets the value of the addMember property.
     * 
     * @return
     *     possible object is
     *     {@link AddMember }
     *     
     */
    public AddMember getAddMember() {
        return addMember;
    }

    /**
     * Sets the value of the addMember property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddMember }
     *     
     */
    public void setAddMember(AddMember value) {
        this.addMember = value;
    }

    /**
     * Gets the value of the creationdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationdate() {
        return creationdate;
    }

    /**
     * Sets the value of the creationdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationdate(XMLGregorianCalendar value) {
        this.creationdate = value;
    }

    /**
     * Gets the value of the currentUserPrincipal property.
     * 
     * @return
     *     possible object is
     *     {@link CurrentUserPrincipal }
     *     
     */
    public CurrentUserPrincipal getCurrentUserPrincipal() {
        return currentUserPrincipal;
    }

    /**
     * Sets the value of the currentUserPrincipal property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrentUserPrincipal }
     *     
     */
    public void setCurrentUserPrincipal(CurrentUserPrincipal value) {
        this.currentUserPrincipal = value;
    }

    /**
     * Gets the value of the currentUserPrivilegeSet property.
     * 
     * @return
     *     possible object is
     *     {@link CurrentUserPrivilegeSet }
     *     
     */
    public CurrentUserPrivilegeSet getCurrentUserPrivilegeSet() {
        return currentUserPrivilegeSet;
    }

    /**
     * Sets the value of the currentUserPrivilegeSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrentUserPrivilegeSet }
     *     
     */
    public void setCurrentUserPrivilegeSet(CurrentUserPrivilegeSet value) {
        this.currentUserPrivilegeSet = value;
    }

    /**
     * Gets the value of the displayname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayname() {
        return displayname;
    }

    /**
     * Sets the value of the displayname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayname(String value) {
        this.displayname = value;
    }

    /**
     * Gets the value of the getcontentlength property.
     * 
     * @return
     *     possible object is
     *     {@link Getcontentlength }
     *     
     */
    public Getcontentlength getGetcontentlength() {
        return getcontentlength;
    }

    /**
     * Sets the value of the getcontentlength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Getcontentlength }
     *     
     */
    public void setGetcontentlength(Getcontentlength value) {
        this.getcontentlength = value;
    }

    /**
     * Gets the value of the getcontenttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetcontenttype() {
        return getcontenttype;
    }

    /**
     * Sets the value of the getcontenttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetcontenttype(String value) {
        this.getcontenttype = value;
    }

    /**
     * Gets the value of the getetag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetetag() {
        return getetag;
    }

    /**
     * Sets the value of the getetag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetetag(String value) {
        this.getetag = value;
    }

    /**
     * Gets the value of the getlastmodified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetlastmodified() {
        return getlastmodified;
    }

    /**
     * Sets the value of the getlastmodified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetlastmodified(String value) {
        this.getlastmodified = value;
    }

    /**
     * Gets the value of the inheritedAclSet property.
     * 
     * @return
     *     possible object is
     *     {@link InheritedAclSet }
     *     
     */
    public InheritedAclSet getInheritedAclSet() {
        return inheritedAclSet;
    }

    /**
     * Sets the value of the inheritedAclSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link InheritedAclSet }
     *     
     */
    public void setInheritedAclSet(InheritedAclSet value) {
        this.inheritedAclSet = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link Owner }
     *     
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Owner }
     *     
     */
    public void setOwner(Owner value) {
        this.owner = value;
    }

    /**
     * Gets the value of the principalCollectionSet property.
     * 
     * @return
     *     possible object is
     *     {@link PrincipalCollectionSet }
     *     
     */
    public PrincipalCollectionSet getPrincipalCollectionSet() {
        return principalCollectionSet;
    }

    /**
     * Sets the value of the principalCollectionSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrincipalCollectionSet }
     *     
     */
    public void setPrincipalCollectionSet(PrincipalCollectionSet value) {
        this.principalCollectionSet = value;
    }

    /**
     * Gets the value of the quotaAvailableBytes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQuotaAvailableBytes() {
        return quotaAvailableBytes;
    }

    /**
     * Sets the value of the quotaAvailableBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQuotaAvailableBytes(BigInteger value) {
        this.quotaAvailableBytes = value;
    }

    /**
     * Gets the value of the quotaUsedBytes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQuotaUsedBytes() {
        return quotaUsedBytes;
    }

    /**
     * Sets the value of the quotaUsedBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQuotaUsedBytes(BigInteger value) {
        this.quotaUsedBytes = value;
    }

    /**
     * Gets the value of the resourceId property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceId }
     *     
     */
    public ResourceId getResourceId() {
        return resourceId;
    }

    /**
     * Sets the value of the resourceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceId }
     *     
     */
    public void setResourceId(ResourceId value) {
        this.resourceId = value;
    }

    /**
     * Gets the value of the resourcetype property.
     * 
     * @return
     *     possible object is
     *     {@link Resourcetype }
     *     
     */
    public Resourcetype getResourcetype() {
        return resourcetype;
    }

    /**
     * Sets the value of the resourcetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Resourcetype }
     *     
     */
    public void setResourcetype(Resourcetype value) {
        this.resourcetype = value;
    }

    /**
     * Gets the value of the supportedPrivilegeSet property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedPrivilegeSet }
     *     
     */
    public SupportedPrivilegeSet getSupportedPrivilegeSet() {
        return supportedPrivilegeSet;
    }

    /**
     * Sets the value of the supportedPrivilegeSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedPrivilegeSet }
     *     
     */
    public void setSupportedPrivilegeSet(SupportedPrivilegeSet value) {
        this.supportedPrivilegeSet = value;
    }

    /**
     * Gets the value of the supportedReportSet property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedReportSet }
     *     
     */
    public SupportedReportSet getSupportedReportSet() {
        return supportedReportSet;
    }

    /**
     * Sets the value of the supportedReportSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedReportSet }
     *     
     */
    public void setSupportedReportSet(SupportedReportSet value) {
        this.supportedReportSet = value;
    }

    /**
     * Gets the value of the supportedlock property.
     * 
     * @return
     *     possible object is
     *     {@link Supportedlock }
     *     
     */
    public Supportedlock getSupportedlock() {
        return supportedlock;
    }

    /**
     * Sets the value of the supportedlock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Supportedlock }
     *     
     */
    public void setSupportedlock(Supportedlock value) {
        this.supportedlock = value;
    }

    /**
     * Gets the value of the syncToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSyncToken() {
        return syncToken;
    }

    /**
     * Sets the value of the syncToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSyncToken(String value) {
        this.syncToken = value;
    }

    /**
     * Gets the value of the allowedSharingModes property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedSharingModes }
     *     
     */
    public AllowedSharingModes getAllowedSharingModes() {
        return allowedSharingModes;
    }

    /**
     * Sets the value of the allowedSharingModes property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedSharingModes }
     *     
     */
    public void setAllowedSharingModes(AllowedSharingModes value) {
        this.allowedSharingModes = value;
    }

    /**
     * Gets the value of the getctag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetctag() {
        return getctag;
    }

    /**
     * Sets the value of the getctag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetctag(String value) {
        this.getctag = value;
    }

    /**
     * Gets the value of the invite property.
     * 
     * @return
     *     possible object is
     *     {@link Invite }
     *     
     */
    public Invite getInvite() {
        return invite;
    }

    /**
     * Sets the value of the invite property.
     * 
     * @param value
     *     allowed object is
     *     {@link Invite }
     *     
     */
    public void setInvite(Invite value) {
        this.invite = value;
    }

    /**
     * Gets the value of the maxResources property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxResources() {
        return maxResources;
    }

    /**
     * Sets the value of the maxResources property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxResources(String value) {
        this.maxResources = value;
    }

    /**
     * Gets the value of the sharedUrl property.
     * 
     * @return
     *     possible object is
     *     {@link SharedUrl }
     *     
     */
    public SharedUrl getSharedUrl() {
        return sharedUrl;
    }

    /**
     * Sets the value of the sharedUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link SharedUrl }
     *     
     */
    public void setSharedUrl(SharedUrl value) {
        this.sharedUrl = value;
    }

    /**
     * Gets the value of the pushkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPushkey() {
        return pushkey;
    }

    /**
     * Sets the value of the pushkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPushkey(String value) {
        this.pushkey = value;
    }

    /**
     * Gets the value of the resourceClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceClass() {
        return resourceClass;
    }

    /**
     * Sets the value of the resourceClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceClass(String value) {
        this.resourceClass = value;
    }

    /**
     * Gets the value of the defaultAddressbookURL property.
     * 
     * @return
     *     possible object is
     *     {@link DefaultAddressbookURL }
     *     
     */
    public DefaultAddressbookURL getDefaultAddressbookURL() {
        return defaultAddressbookURL;
    }

    /**
     * Sets the value of the defaultAddressbookURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link DefaultAddressbookURL }
     *     
     */
    public void setDefaultAddressbookURL(DefaultAddressbookURL value) {
        this.defaultAddressbookURL = value;
    }

    /**
     * Gets the value of the maxResourceSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxResourceSize() {
        return maxResourceSize;
    }

    /**
     * Sets the value of the maxResourceSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxResourceSize(String value) {
        this.maxResourceSize = value;
    }

    /**
     * Gets the value of the supportedAddressData property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedAddressData }
     *     
     */
    public SupportedAddressData getSupportedAddressData() {
        return supportedAddressData;
    }

    /**
     * Sets the value of the supportedAddressData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedAddressData }
     *     
     */
    public void setSupportedAddressData(SupportedAddressData value) {
        this.supportedAddressData = value;
    }

}
