
package zswi.schemas.caldav.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;choice>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-collection-location-ok"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}max-attendees-per-instance"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}max-date-time"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}max-instances"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}max-resource-size"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}min-date-time"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}supported-calendar-component"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}supported-calendar-data"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}valid-calendar-data"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}valid-calendar-object-resource"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}valid-filter"/>
 *           &lt;element ref="{DAV}number-of-matches-within-limits"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}supported-collation"/>
 *           &lt;element ref="{DAV}resource-must-be-null"/>
 *           &lt;element ref="{DAV}needs-privilege"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}initialize-calendar-collection"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}no-uid-conflict"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://twistedmatrix.com/xml_namespace/dav/}error-description"/>
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
    "calendarCollectionLocationOk",
    "maxAttendeesPerInstance",
    "maxDateTime",
    "maxInstances",
    "maxResourceSize",
    "minDateTime",
    "supportedCalendarComponent",
    "supportedCalendarData",
    "validCalendarData",
    "validCalendarObjectResource",
    "validFilter",
    "numberOfMatchesWithinLimits",
    "supportedCollation",
    "resourceMustBeNull",
    "needsPrivilege",
    "initializeCalendarCollection",
    "noUidConflict",
    "errorDescription"
})
@XmlRootElement(name = "error", namespace = "DAV:")
public class Error {

    @XmlElement(name = "calendar-collection-location-ok")
    protected CalendarCollectionLocationOk calendarCollectionLocationOk;
    @XmlElement(name = "max-attendees-per-instance")
    protected MaxAttendeesPerInstance maxAttendeesPerInstance;
    @XmlElement(name = "max-date-time")
    protected MaxDateTime maxDateTime;
    @XmlElement(name = "max-instances")
    protected MaxInstances maxInstances;
    @XmlElement(name = "max-resource-size")
    protected MaxResourceSize maxResourceSize;
    @XmlElement(name = "min-date-time")
    protected MinDateTime minDateTime;
    @XmlElement(name = "supported-calendar-component")
    protected SupportedCalendarComponent supportedCalendarComponent;
    @XmlElement(name = "supported-calendar-data")
    protected SupportedCalendarData supportedCalendarData;
    @XmlElement(name = "valid-calendar-data")
    protected ValidCalendarData validCalendarData;
    @XmlElement(name = "valid-calendar-object-resource")
    protected ValidCalendarObjectResource validCalendarObjectResource;
    @XmlElement(name = "valid-filter")
    protected ValidFilter validFilter;
    @XmlElement(name = "number-of-matches-within-limits", namespace = "DAV:")
    protected NumberOfMatchesWithinLimits numberOfMatchesWithinLimits;
    @XmlElement(name = "supported-collation")
    protected SupportedCollation supportedCollation;
    @XmlElement(name = "resource-must-be-null", namespace = "DAV:")
    protected ResourceMustBeNull resourceMustBeNull;
    @XmlElement(name = "needs-privilege", namespace = "DAV:")
    protected NeedsPrivilege needsPrivilege;
    @XmlElement(name = "initialize-calendar-collection")
    protected InitializeCalendarCollection initializeCalendarCollection;
    @XmlElement(name = "no-uid-conflict")
    protected NoUidConflict noUidConflict;
    @XmlElement(name = "error-description", namespace = "http://twistedmatrix.com/xml_namespace/dav/", required = true)
    protected String errorDescription;

    /**
     * Gets the value of the calendarCollectionLocationOk property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarCollectionLocationOk }
     *     
     */
    public CalendarCollectionLocationOk getCalendarCollectionLocationOk() {
        return calendarCollectionLocationOk;
    }

    /**
     * Sets the value of the calendarCollectionLocationOk property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarCollectionLocationOk }
     *     
     */
    public void setCalendarCollectionLocationOk(CalendarCollectionLocationOk value) {
        this.calendarCollectionLocationOk = value;
    }

    /**
     * Gets the value of the maxAttendeesPerInstance property.
     * 
     * @return
     *     possible object is
     *     {@link MaxAttendeesPerInstance }
     *     
     */
    public MaxAttendeesPerInstance getMaxAttendeesPerInstance() {
        return maxAttendeesPerInstance;
    }

    /**
     * Sets the value of the maxAttendeesPerInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaxAttendeesPerInstance }
     *     
     */
    public void setMaxAttendeesPerInstance(MaxAttendeesPerInstance value) {
        this.maxAttendeesPerInstance = value;
    }

    /**
     * Gets the value of the maxDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link MaxDateTime }
     *     
     */
    public MaxDateTime getMaxDateTime() {
        return maxDateTime;
    }

    /**
     * Sets the value of the maxDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaxDateTime }
     *     
     */
    public void setMaxDateTime(MaxDateTime value) {
        this.maxDateTime = value;
    }

    /**
     * Gets the value of the maxInstances property.
     * 
     * @return
     *     possible object is
     *     {@link MaxInstances }
     *     
     */
    public MaxInstances getMaxInstances() {
        return maxInstances;
    }

    /**
     * Sets the value of the maxInstances property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaxInstances }
     *     
     */
    public void setMaxInstances(MaxInstances value) {
        this.maxInstances = value;
    }

    /**
     * Gets the value of the maxResourceSize property.
     * 
     * @return
     *     possible object is
     *     {@link MaxResourceSize }
     *     
     */
    public MaxResourceSize getMaxResourceSize() {
        return maxResourceSize;
    }

    /**
     * Sets the value of the maxResourceSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaxResourceSize }
     *     
     */
    public void setMaxResourceSize(MaxResourceSize value) {
        this.maxResourceSize = value;
    }

    /**
     * Gets the value of the minDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link MinDateTime }
     *     
     */
    public MinDateTime getMinDateTime() {
        return minDateTime;
    }

    /**
     * Sets the value of the minDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link MinDateTime }
     *     
     */
    public void setMinDateTime(MinDateTime value) {
        this.minDateTime = value;
    }

    /**
     * Gets the value of the supportedCalendarComponent property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedCalendarComponent }
     *     
     */
    public SupportedCalendarComponent getSupportedCalendarComponent() {
        return supportedCalendarComponent;
    }

    /**
     * Sets the value of the supportedCalendarComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedCalendarComponent }
     *     
     */
    public void setSupportedCalendarComponent(SupportedCalendarComponent value) {
        this.supportedCalendarComponent = value;
    }

    /**
     * Gets the value of the supportedCalendarData property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedCalendarData }
     *     
     */
    public SupportedCalendarData getSupportedCalendarData() {
        return supportedCalendarData;
    }

    /**
     * Sets the value of the supportedCalendarData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedCalendarData }
     *     
     */
    public void setSupportedCalendarData(SupportedCalendarData value) {
        this.supportedCalendarData = value;
    }

    /**
     * Gets the value of the validCalendarData property.
     * 
     * @return
     *     possible object is
     *     {@link ValidCalendarData }
     *     
     */
    public ValidCalendarData getValidCalendarData() {
        return validCalendarData;
    }

    /**
     * Sets the value of the validCalendarData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidCalendarData }
     *     
     */
    public void setValidCalendarData(ValidCalendarData value) {
        this.validCalendarData = value;
    }

    /**
     * Gets the value of the validCalendarObjectResource property.
     * 
     * @return
     *     possible object is
     *     {@link ValidCalendarObjectResource }
     *     
     */
    public ValidCalendarObjectResource getValidCalendarObjectResource() {
        return validCalendarObjectResource;
    }

    /**
     * Sets the value of the validCalendarObjectResource property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidCalendarObjectResource }
     *     
     */
    public void setValidCalendarObjectResource(ValidCalendarObjectResource value) {
        this.validCalendarObjectResource = value;
    }

    /**
     * Gets the value of the validFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ValidFilter }
     *     
     */
    public ValidFilter getValidFilter() {
        return validFilter;
    }

    /**
     * Sets the value of the validFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidFilter }
     *     
     */
    public void setValidFilter(ValidFilter value) {
        this.validFilter = value;
    }

    /**
     * Gets the value of the numberOfMatchesWithinLimits property.
     * 
     * @return
     *     possible object is
     *     {@link NumberOfMatchesWithinLimits }
     *     
     */
    public NumberOfMatchesWithinLimits getNumberOfMatchesWithinLimits() {
        return numberOfMatchesWithinLimits;
    }

    /**
     * Sets the value of the numberOfMatchesWithinLimits property.
     * 
     * @param value
     *     allowed object is
     *     {@link NumberOfMatchesWithinLimits }
     *     
     */
    public void setNumberOfMatchesWithinLimits(NumberOfMatchesWithinLimits value) {
        this.numberOfMatchesWithinLimits = value;
    }

    /**
     * Gets the value of the supportedCollation property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedCollation }
     *     
     */
    public SupportedCollation getSupportedCollation() {
        return supportedCollation;
    }

    /**
     * Sets the value of the supportedCollation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedCollation }
     *     
     */
    public void setSupportedCollation(SupportedCollation value) {
        this.supportedCollation = value;
    }

    /**
     * Gets the value of the resourceMustBeNull property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceMustBeNull }
     *     
     */
    public ResourceMustBeNull getResourceMustBeNull() {
        return resourceMustBeNull;
    }

    /**
     * Sets the value of the resourceMustBeNull property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceMustBeNull }
     *     
     */
    public void setResourceMustBeNull(ResourceMustBeNull value) {
        this.resourceMustBeNull = value;
    }

    /**
     * Gets the value of the needsPrivilege property.
     * 
     * @return
     *     possible object is
     *     {@link NeedsPrivilege }
     *     
     */
    public NeedsPrivilege getNeedsPrivilege() {
        return needsPrivilege;
    }

    /**
     * Sets the value of the needsPrivilege property.
     * 
     * @param value
     *     allowed object is
     *     {@link NeedsPrivilege }
     *     
     */
    public void setNeedsPrivilege(NeedsPrivilege value) {
        this.needsPrivilege = value;
    }

    /**
     * Gets the value of the initializeCalendarCollection property.
     * 
     * @return
     *     possible object is
     *     {@link InitializeCalendarCollection }
     *     
     */
    public InitializeCalendarCollection getInitializeCalendarCollection() {
        return initializeCalendarCollection;
    }

    /**
     * Sets the value of the initializeCalendarCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link InitializeCalendarCollection }
     *     
     */
    public void setInitializeCalendarCollection(InitializeCalendarCollection value) {
        this.initializeCalendarCollection = value;
    }

    /**
     * Gets the value of the noUidConflict property.
     * 
     * @return
     *     possible object is
     *     {@link NoUidConflict }
     *     
     */
    public NoUidConflict getNoUidConflict() {
        return noUidConflict;
    }

    /**
     * Sets the value of the noUidConflict property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoUidConflict }
     *     
     */
    public void setNoUidConflict(NoUidConflict value) {
        this.noUidConflict = value;
    }

    /**
     * Gets the value of the errorDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Sets the value of the errorDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorDescription(String value) {
        this.errorDescription = value;
    }

}
