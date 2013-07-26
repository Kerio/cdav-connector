
package zswi.schemas.carddav.allprop;

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
 *         &lt;element ref="{DAV}supported-privilege"/>
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
    "supportedPrivilege"
})
@XmlRootElement(name = "supported-privilege-set")
public class SupportedPrivilegeSet {

    @XmlElement(name = "supported-privilege", required = true)
    protected SupportedPrivilege supportedPrivilege;

    /**
     * Gets the value of the supportedPrivilege property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedPrivilege }
     *     
     */
    public SupportedPrivilege getSupportedPrivilege() {
        return supportedPrivilege;
    }

    /**
     * Sets the value of the supportedPrivilege property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedPrivilege }
     *     
     */
    public void setSupportedPrivilege(SupportedPrivilege value) {
        this.supportedPrivilege = value;
    }

}
