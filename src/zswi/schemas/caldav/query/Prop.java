
package zswi.schemas.caldav.query;

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
 *         &lt;element ref="{DAV}getetag"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-data"/>
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
    "getetag",
    "calendarData"
})
@XmlRootElement(name = "prop", namespace = "DAV:")
public class Prop {

    @XmlElement(namespace = "DAV:", required = true)
    protected Getetag getetag;
    @XmlElement(name = "calendar-data", required = true)
    protected CalendarData calendarData;

    /**
     * Gets the value of the getetag property.
     * 
     * @return
     *     possible object is
     *     {@link Getetag }
     *     
     */
    public Getetag getGetetag() {
        return getetag;
    }

    /**
     * Sets the value of the getetag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Getetag }
     *     
     */
    public void setGetetag(Getetag value) {
        this.getetag = value;
    }

    /**
     * Gets the value of the calendarData property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarData }
     *     
     */
    public CalendarData getCalendarData() {
        return calendarData;
    }

    /**
     * Sets the value of the calendarData property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarData }
     *     
     */
    public void setCalendarData(CalendarData value) {
        this.calendarData = value;
    }

}
