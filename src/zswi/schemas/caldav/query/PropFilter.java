
package zswi.schemas.caldav.query;

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
 *       &lt;sequence>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}is-not-defined" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}text-match" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}param-filter" minOccurs="0"/>
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
    "isNotDefined",
    "textMatch",
    "paramFilter"
})
@XmlRootElement(name = "prop-filter")
public class PropFilter {

    @XmlElement(name = "is-not-defined")
    protected IsNotDefined isNotDefined;
    @XmlElement(name = "text-match")
    protected TextMatch textMatch;
    @XmlElement(name = "param-filter")
    protected ParamFilter paramFilter;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

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
     * Gets the value of the textMatch property.
     * 
     * @return
     *     possible object is
     *     {@link TextMatch }
     *     
     */
    public TextMatch getTextMatch() {
        return textMatch;
    }

    /**
     * Sets the value of the textMatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextMatch }
     *     
     */
    public void setTextMatch(TextMatch value) {
        this.textMatch = value;
    }

    /**
     * Gets the value of the paramFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ParamFilter }
     *     
     */
    public ParamFilter getParamFilter() {
        return paramFilter;
    }

    /**
     * Sets the value of the paramFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParamFilter }
     *     
     */
    public void setParamFilter(ParamFilter value) {
        this.paramFilter = value;
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
