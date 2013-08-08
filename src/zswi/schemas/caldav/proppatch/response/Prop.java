
package zswi.schemas.caldav.proppatch.response;

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
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-description"/>
 *           &lt;element ref="{http://apple.com/ns/ical/}calendar-color"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}schedule-calendar-transp"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-timezone"/>
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
    "calendarDescription",
    "calendarColor",
    "scheduleCalendarTransp",
    "calendarTimezone"
})
@XmlRootElement(name = "prop")
public class Prop {

    @XmlElement(name = "calendar-description", namespace = "urn:ietf:params:xml:ns:caldav")
    protected CalendarDescription calendarDescription;
    @XmlElement(name = "calendar-color", namespace = "http://apple.com/ns/ical/")
    protected CalendarColor calendarColor;
    @XmlElement(name = "schedule-calendar-transp", namespace = "urn:ietf:params:xml:ns:caldav")
    protected ScheduleCalendarTransp scheduleCalendarTransp;
    @XmlElement(name = "calendar-timezone", namespace = "urn:ietf:params:xml:ns:caldav")
    protected CalendarTimezone calendarTimezone;

    /**
     * Gets the value of the calendarDescription property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarDescription }
     *     
     */
    public CalendarDescription getCalendarDescription() {
        return calendarDescription;
    }

    /**
     * Sets the value of the calendarDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarDescription }
     *     
     */
    public void setCalendarDescription(CalendarDescription value) {
        this.calendarDescription = value;
    }

    /**
     * Gets the value of the calendarColor property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarColor }
     *     
     */
    public CalendarColor getCalendarColor() {
        return calendarColor;
    }

    /**
     * Sets the value of the calendarColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarColor }
     *     
     */
    public void setCalendarColor(CalendarColor value) {
        this.calendarColor = value;
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
     *     {@link CalendarTimezone }
     *     
     */
    public CalendarTimezone getCalendarTimezone() {
        return calendarTimezone;
    }

    /**
     * Sets the value of the calendarTimezone property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarTimezone }
     *     
     */
    public void setCalendarTimezone(CalendarTimezone value) {
        this.calendarTimezone = value;
    }

}
