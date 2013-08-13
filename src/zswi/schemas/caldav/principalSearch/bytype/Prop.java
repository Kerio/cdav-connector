
package zswi.schemas.caldav.principalSearch.bytype;

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
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-user-address-set" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-user-type" minOccurs="0"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{http://calendarserver.org/ns/}record-type"/>
 *           &lt;element ref="{http://calendarserver.org/ns/}first-name"/>
 *           &lt;element ref="{http://calendarserver.org/ns/}last-name"/>
 *           &lt;element ref="{http://calendarserver.org/ns/}auto-schedule"/>
 *           &lt;element ref="{http://calendarserver.org/ns/}auto-schedule-mode"/>
 *           &lt;element ref="{DAV}resourcetype"/>
 *         &lt;/sequence>
 *         &lt;element ref="{DAV}displayname" minOccurs="0"/>
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
    "calendarUserAddressSet",
    "calendarUserType",
    "recordType",
    "firstName",
    "lastName",
    "autoSchedule",
    "autoScheduleMode",
    "resourcetype",
    "displayname"
})
@XmlRootElement(name = "prop", namespace = "DAV:")
public class Prop {

    @XmlElement(name = "calendar-user-address-set", namespace = "urn:ietf:params:xml:ns:caldav")
    protected CalendarUserAddressSet calendarUserAddressSet;
    @XmlElement(name = "calendar-user-type", namespace = "urn:ietf:params:xml:ns:caldav")
    protected CalendarUserType calendarUserType;
    @XmlElement(name = "record-type")
    protected RecordType recordType;
    @XmlElement(name = "first-name")
    protected FirstName firstName;
    @XmlElement(name = "last-name")
    protected LastName lastName;
    @XmlElement(name = "auto-schedule")
    protected AutoSchedule autoSchedule;
    @XmlElement(name = "auto-schedule-mode")
    protected AutoScheduleMode autoScheduleMode;
    @XmlElement(namespace = "DAV:")
    protected Resourcetype resourcetype;
    @XmlElement(namespace = "DAV:")
    protected Displayname displayname;

    /**
     * Gets the value of the calendarUserAddressSet property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarUserAddressSet }
     *     
     */
    public CalendarUserAddressSet getCalendarUserAddressSet() {
        return calendarUserAddressSet;
    }

    /**
     * Sets the value of the calendarUserAddressSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarUserAddressSet }
     *     
     */
    public void setCalendarUserAddressSet(CalendarUserAddressSet value) {
        this.calendarUserAddressSet = value;
    }

    /**
     * Gets the value of the calendarUserType property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarUserType }
     *     
     */
    public CalendarUserType getCalendarUserType() {
        return calendarUserType;
    }

    /**
     * Sets the value of the calendarUserType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarUserType }
     *     
     */
    public void setCalendarUserType(CalendarUserType value) {
        this.calendarUserType = value;
    }

    /**
     * Gets the value of the recordType property.
     * 
     * @return
     *     possible object is
     *     {@link RecordType }
     *     
     */
    public RecordType getRecordType() {
        return recordType;
    }

    /**
     * Sets the value of the recordType property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecordType }
     *     
     */
    public void setRecordType(RecordType value) {
        this.recordType = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link FirstName }
     *     
     */
    public FirstName getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link FirstName }
     *     
     */
    public void setFirstName(FirstName value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link LastName }
     *     
     */
    public LastName getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link LastName }
     *     
     */
    public void setLastName(LastName value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the autoSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link AutoSchedule }
     *     
     */
    public AutoSchedule getAutoSchedule() {
        return autoSchedule;
    }

    /**
     * Sets the value of the autoSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutoSchedule }
     *     
     */
    public void setAutoSchedule(AutoSchedule value) {
        this.autoSchedule = value;
    }

    /**
     * Gets the value of the autoScheduleMode property.
     * 
     * @return
     *     possible object is
     *     {@link AutoScheduleMode }
     *     
     */
    public AutoScheduleMode getAutoScheduleMode() {
        return autoScheduleMode;
    }

    /**
     * Sets the value of the autoScheduleMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutoScheduleMode }
     *     
     */
    public void setAutoScheduleMode(AutoScheduleMode value) {
        this.autoScheduleMode = value;
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
     * Gets the value of the displayname property.
     * 
     * @return
     *     possible object is
     *     {@link Displayname }
     *     
     */
    public Displayname getDisplayname() {
        return displayname;
    }

    /**
     * Sets the value of the displayname property.
     * 
     * @param value
     *     allowed object is
     *     {@link Displayname }
     *     
     */
    public void setDisplayname(Displayname value) {
        this.displayname = value;
    }

}
