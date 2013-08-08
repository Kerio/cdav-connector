
package zswi.schemas.caldav.proppatch;

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
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}transparent"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}opaque"/>
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
    "transparent",
    "opaque"
})
@XmlRootElement(name = "schedule-calendar-transp", namespace = "urn:ietf:params:xml:ns:caldav")
public class ScheduleCalendarTransp {

    @XmlElement(namespace = "urn:ietf:params:xml:ns:caldav")
    protected Transparent transparent;
    @XmlElement(namespace = "urn:ietf:params:xml:ns:caldav")
    protected Opaque opaque;

    /**
     * Gets the value of the transparent property.
     * 
     * @return
     *     possible object is
     *     {@link Transparent }
     *     
     */
    public Transparent getTransparent() {
        return transparent;
    }

    /**
     * Sets the value of the transparent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Transparent }
     *     
     */
    public void setTransparent(Transparent value) {
        this.transparent = value;
    }

    /**
     * Gets the value of the opaque property.
     * 
     * @return
     *     possible object is
     *     {@link Opaque }
     *     
     */
    public Opaque getOpaque() {
        return opaque;
    }

    /**
     * Sets the value of the opaque property.
     * 
     * @param value
     *     allowed object is
     *     {@link Opaque }
     *     
     */
    public void setOpaque(Opaque value) {
        this.opaque = value;
    }

}
