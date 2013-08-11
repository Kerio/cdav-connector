
package zswi.schemas.caldav.rfc6638.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}schedule-tag"/>
 *         &lt;sequence>
 *           &lt;element ref="{DAV}displayname"/>
 *           &lt;element ref="{http://calendarserver.org/ns/}schedule-default-tasks-URL"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}schedule-default-calendar-URL"/>
 *           &lt;element ref="{http://calendarserver.org/ns/}getctag"/>
 *           &lt;element ref="{DAV}resourcetype"/>
 *           &lt;element ref="{DAV}getetag"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "scheduleTag",
    "displayname",
    "scheduleDefaultTasksURL",
    "scheduleDefaultCalendarURL",
    "getctag",
    "resourcetype",
    "getetag"
})
@XmlRootElement(name = "prop")
public class Prop {

    @XmlElement(name = "schedule-tag", namespace = "urn:ietf:params:xml:ns:caldav")
    protected ScheduleTag scheduleTag;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String displayname;
    @XmlElement(name = "schedule-default-tasks-URL", namespace = "http://calendarserver.org/ns/")
    protected Href scheduleDefaultTasksURL;
    @XmlElement(name = "schedule-default-calendar-URL", namespace = "urn:ietf:params:xml:ns:caldav")
    protected Href scheduleDefaultCalendarURL;
    @XmlElement(namespace = "http://calendarserver.org/ns/")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String getctag;
    protected Resourcetype resourcetype;
    protected String getetag;

    /**
     * Gets the value of the scheduleTag property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleTag }
     *     
     */
    public ScheduleTag getScheduleTag() {
        return scheduleTag;
    }

    /**
     * Sets the value of the scheduleTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleTag }
     *     
     */
    public void setScheduleTag(ScheduleTag value) {
        this.scheduleTag = value;
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
     * Gets the value of the scheduleDefaultTasksURL property.
     * 
     * @return
     *     possible object is
     *     {@link Href }
     *     
     */
    public Href getScheduleDefaultTasksURL() {
        return scheduleDefaultTasksURL;
    }

    /**
     * Sets the value of the scheduleDefaultTasksURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link Href }
     *     
     */
    public void setScheduleDefaultTasksURL(Href value) {
        this.scheduleDefaultTasksURL = value;
    }

    /**
     * Gets the value of the scheduleDefaultCalendarURL property.
     * 
     * @return
     *     possible object is
     *     {@link Href }
     *     
     */
    public Href getScheduleDefaultCalendarURL() {
        return scheduleDefaultCalendarURL;
    }

    /**
     * Sets the value of the scheduleDefaultCalendarURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link Href }
     *     
     */
    public void setScheduleDefaultCalendarURL(Href value) {
        this.scheduleDefaultCalendarURL = value;
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

}
