
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
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}expand"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}limit-recurrence-set"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}limit-freebusy-set"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}comp"/>
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
    "expand",
    "limitRecurrenceSet",
    "limitFreebusySet",
    "comp"
})
@XmlRootElement(name = "calendar-data")
public class CalendarData {

    @XmlElement(required = true)
    protected Expand expand;
    @XmlElement(name = "limit-recurrence-set", required = true)
    protected LimitRecurrenceSet limitRecurrenceSet;
    @XmlElement(name = "limit-freebusy-set", required = true)
    protected LimitFreebusySet limitFreebusySet;
    @XmlElement(required = true)
    protected Comp comp;

    /**
     * Gets the value of the expand property.
     * 
     * @return
     *     possible object is
     *     {@link Expand }
     *     
     */
    public Expand getExpand() {
        return expand;
    }

    /**
     * Sets the value of the expand property.
     * 
     * @param value
     *     allowed object is
     *     {@link Expand }
     *     
     */
    public void setExpand(Expand value) {
        this.expand = value;
    }

    /**
     * Gets the value of the limitRecurrenceSet property.
     * 
     * @return
     *     possible object is
     *     {@link LimitRecurrenceSet }
     *     
     */
    public LimitRecurrenceSet getLimitRecurrenceSet() {
        return limitRecurrenceSet;
    }

    /**
     * Sets the value of the limitRecurrenceSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitRecurrenceSet }
     *     
     */
    public void setLimitRecurrenceSet(LimitRecurrenceSet value) {
        this.limitRecurrenceSet = value;
    }

    /**
     * Gets the value of the limitFreebusySet property.
     * 
     * @return
     *     possible object is
     *     {@link LimitFreebusySet }
     *     
     */
    public LimitFreebusySet getLimitFreebusySet() {
        return limitFreebusySet;
    }

    /**
     * Sets the value of the limitFreebusySet property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitFreebusySet }
     *     
     */
    public void setLimitFreebusySet(LimitFreebusySet value) {
        this.limitFreebusySet = value;
    }

    /**
     * Gets the value of the comp property.
     * 
     * @return
     *     possible object is
     *     {@link Comp }
     *     
     */
    public Comp getComp() {
        return comp;
    }

    /**
     * Sets the value of the comp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Comp }
     *     
     */
    public void setComp(Comp value) {
        this.comp = value;
    }

}
