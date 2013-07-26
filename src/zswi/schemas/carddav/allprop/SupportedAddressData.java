
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
 *         &lt;element ref="{urn:ietf:params:xml:ns:carddav}address-data-type" minOccurs="0"/>
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
    "addressDataType"
})
@XmlRootElement(name = "supported-address-data", namespace = "urn:ietf:params:xml:ns:carddav")
public class SupportedAddressData {

    @XmlElement(name = "address-data-type", namespace = "urn:ietf:params:xml:ns:carddav")
    protected AddressDataType addressDataType;

    /**
     * Gets the value of the addressDataType property.
     * 
     * @return
     *     possible object is
     *     {@link AddressDataType }
     *     
     */
    public AddressDataType getAddressDataType() {
        return addressDataType;
    }

    /**
     * Sets the value of the addressDataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressDataType }
     *     
     */
    public void setAddressDataType(AddressDataType value) {
        this.addressDataType = value;
    }

}
