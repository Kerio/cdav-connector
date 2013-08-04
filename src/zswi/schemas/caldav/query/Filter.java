
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
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}comp-filter"/>
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
    "compFilter"
})
@XmlRootElement(name = "filter")
public class Filter {

    @XmlElement(name = "comp-filter", required = true)
    protected CompFilter compFilter;

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

}
