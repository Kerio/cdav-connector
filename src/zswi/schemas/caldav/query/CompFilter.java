
package zswi.schemas.caldav.query;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}comp-filter"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}is-not-defined"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}time-range"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}prop-filter" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "compFilter",
    "isNotDefined",
    "timeRange",
    "propFilter"
})
@XmlRootElement(name = "comp-filter")
public class CompFilter {

    @XmlElement(name = "comp-filter")
    protected CompFilter compFilter;
    @XmlElement(name = "is-not-defined")
    protected IsNotDefined isNotDefined;
    @XmlElement(name = "time-range")
    protected TimeRange timeRange;
    @XmlElement(name = "prop-filter")
    protected List<PropFilter> propFilter;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    /**
     * Gets the value of the compFilter property.
     * 
     * @return
     *     possible object is
     *     {@link CompFilter }
     *     
     */
    public CompFilter getCompFilter() {
        return compFilter;
    }

    /**
     * Sets the value of the compFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompFilter }
     *     
     */
    public void setCompFilter(CompFilter value) {
        this.compFilter = value;
    }

    /**
     * Gets the value of the isNotDefined property.
     * 
     * @return
     *     possible object is
     *     {@link IsNotDefined }
     *     
     */
    public IsNotDefined getIsNotDefined() {
        return isNotDefined;
    }

    /**
     * Sets the value of the isNotDefined property.
     * 
     * @param value
     *     allowed object is
     *     {@link IsNotDefined }
     *     
     */
    public void setIsNotDefined(IsNotDefined value) {
        this.isNotDefined = value;
    }

    /**
     * Gets the value of the timeRange property.
     * 
     * @return
     *     possible object is
     *     {@link TimeRange }
     *     
     */
    public TimeRange getTimeRange() {
        return timeRange;
    }

    /**
     * Sets the value of the timeRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeRange }
     *     
     */
    public void setTimeRange(TimeRange value) {
        this.timeRange = value;
    }

    /**
     * Gets the value of the propFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropFilter }
     * 
     * 
     */
    public List<PropFilter> getPropFilter() {
        if (propFilter == null) {
            propFilter = new ArrayList<PropFilter>();
        }
        return this.propFilter;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
