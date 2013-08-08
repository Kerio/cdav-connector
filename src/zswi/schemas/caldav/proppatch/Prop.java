
package zswi.schemas.caldav.proppatch;

import java.math.BigInteger;
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
 *       &lt;sequence>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-description"/>
 *         &lt;element ref="{http://apple.com/ns/ical/}calendar-color"/>
 *         &lt;element ref="{http://apple.com/ns/ical/}calendar-order"/>
 *         &lt;element ref="{DAV}displayname"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}schedule-calendar-transp"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-timezone"/>
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
    "calendarDescription",
    "calendarColor",
    "calendarOrder",
    "displayname",
    "scheduleCalendarTransp",
    "calendarTimezone"
})
@XmlRootElement(name = "prop")
public class Prop {

    @XmlElement(name = "calendar-description", namespace = "urn:ietf:params:xml:ns:caldav", required = true)
    protected String calendarDescription;
    @XmlElement(name = "calendar-color", namespace = "http://apple.com/ns/ical/", required = true)
    protected String calendarColor;
    @XmlElement(name = "calendar-order", namespace = "http://apple.com/ns/ical/", required = true)
    protected BigInteger calendarOrder;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String displayname;
    @XmlElement(name = "schedule-calendar-transp", namespace = "urn:ietf:params:xml:ns:caldav", required = true)
    protected ScheduleCalendarTransp scheduleCalendarTransp;
    @XmlElement(name = "calendar-timezone", namespace = "urn:ietf:params:xml:ns:caldav", required = true)
    protected String calendarTimezone;

    /**
     * Gets the value of the calendarDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalendarDescription() {
        return calendarDescription;
    }

    /**
     * Sets the value of the calendarDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalendarDescription(String value) {
        this.calendarDescription = value;
    }

    /**
     * Gets the value of the calendarColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalendarColor() {
        return calendarColor;
    }

    /**
     * Sets the value of the calendarColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalendarColor(String value) {
        this.calendarColor = value;
    }

    /**
     * Gets the value of the calendarOrder property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCalendarOrder() {
        return calendarOrder;
    }

    /**
     * Sets the value of the calendarOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCalendarOrder(BigInteger value) {
        this.calendarOrder = value;
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
     * Gets the value of the scheduleCalendarTransp property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleCalendarTransp }
     *     
     */
    public ScheduleCalendarTransp getScheduleCalendarTransp() {
        return scheduleCalendarTransp;
    }

    /**
     * Sets the value of the scheduleCalendarTransp property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleCalendarTransp }
     *     
     */
    public void setScheduleCalendarTransp(ScheduleCalendarTransp value) {
        this.scheduleCalendarTransp = value;
    }

    /**
     * Gets the value of the calendarTimezone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalendarTimezone() {
        return calendarTimezone;
    }

    /**
     * Sets the value of the calendarTimezone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalendarTimezone(String value) {
        this.calendarTimezone = value;
    }

}
