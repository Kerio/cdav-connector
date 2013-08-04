
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
 *         &lt;element ref="{DAV}allprop"/>
 *         &lt;element ref="{DAV}propname"/>
 *         &lt;element ref="{DAV}prop"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}filter"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}timezone"/>
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
    "allprop",
    "propname",
    "prop",
    "filter",
    "timezone"
})
@XmlRootElement(name = "calendar-query")
public class CalendarQuery {

    @XmlElement(namespace = "DAV:", required = true)
    protected Allprop allprop;
    @XmlElement(namespace = "DAV:", required = true)
    protected Propname propname;
    @XmlElement(namespace = "DAV:", required = true)
    protected Prop prop;
    @XmlElement(required = true)
    protected Filter filter;
    @XmlElement(required = true)
    protected String timezone;

    /**
     * Gets the value of the allprop property.
     * 
     * @return
     *     possible object is
     *     {@link Allprop }
     *     
     */
    public Allprop getAllprop() {
        return allprop;
    }

    /**
     * Sets the value of the allprop property.
     * 
     * @param value
     *     allowed object is
     *     {@link Allprop }
     *     
     */
    public void setAllprop(Allprop value) {
        this.allprop = value;
    }

    /**
     * Gets the value of the propname property.
     * 
     * @return
     *     possible object is
     *     {@link Propname }
     *     
     */
    public Propname getPropname() {
        return propname;
    }

    /**
     * Sets the value of the propname property.
     * 
     * @param value
     *     allowed object is
     *     {@link Propname }
     *     
     */
    public void setPropname(Propname value) {
        this.propname = value;
    }

    /**
     * Gets the value of the prop property.
     * 
     * @return
     *     possible object is
     *     {@link Prop }
     *     
     */
    public Prop getProp() {
        return prop;
    }

    /**
     * Sets the value of the prop property.
     * 
     * @param value
     *     allowed object is
     *     {@link Prop }
     *     
     */
    public void setProp(Prop value) {
        this.prop = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link Filter }
     *     
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Filter }
     *     
     */
    public void setFilter(Filter value) {
        this.filter = value;
    }

    /**
     * Gets the value of the timezone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Sets the value of the timezone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimezone(String value) {
        this.timezone = value;
    }

}
