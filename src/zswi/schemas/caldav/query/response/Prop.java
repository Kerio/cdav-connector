
package zswi.schemas.caldav.query.response;

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
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}calendar-data" minOccurs="0"/>
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
@XmlRootElement(name = "prop")
public class Prop {

    @XmlElement(required = true)
    protected String getetag;
    @XmlElement(name = "calendar-data", namespace = "urn:ietf:params:xml:ns:caldav")
    protected String calendarData;

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
     * Gets the value of the calendarData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalendarData() {
        return calendarData;
    }

    /**
     * Sets the value of the calendarData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalendarData(String value) {
        this.calendarData = value;
    }

}
