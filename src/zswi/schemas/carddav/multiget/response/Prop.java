
package zswi.schemas.carddav.multiget.response;

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
 *         &lt;element ref="{DAV}getetag" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:carddav}address-data" minOccurs="0"/>
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
    "addressData"
})
@XmlRootElement(name = "prop")
public class Prop {

    protected String getetag;
    @XmlElement(name = "address-data", namespace = "urn:ietf:params:xml:ns:carddav")
    protected String addressData;

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
     * Gets the value of the addressData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressData() {
        return addressData;
    }

    /**
     * Sets the value of the addressData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressData(String value) {
        this.addressData = value;
    }

}
