
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
 *         &lt;element ref="{DAV}collection"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:carddav}addressbook" minOccurs="0"/>
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
    "collection",
    "addressbook"
})
@XmlRootElement(name = "resourcetype")
public class Resourcetype {

    @XmlElement(required = true)
    protected Collection collection;
    @XmlElement(namespace = "urn:ietf:params:xml:ns:carddav")
    protected Addressbook addressbook;

    /**
     * Gets the value of the collection property.
     * 
     * @return
     *     possible object is
     *     {@link Collection }
     *     
     */
    public Collection getCollection() {
        return collection;
    }

    /**
     * Sets the value of the collection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Collection }
     *     
     */
    public void setCollection(Collection value) {
        this.collection = value;
    }

    /**
     * Gets the value of the addressbook property.
     * 
     * @return
     *     possible object is
     *     {@link Addressbook }
     *     
     */
    public Addressbook getAddressbook() {
        return addressbook;
    }

    /**
     * Sets the value of the addressbook property.
     * 
     * @param value
     *     allowed object is
     *     {@link Addressbook }
     *     
     */
    public void setAddressbook(Addressbook value) {
        this.addressbook = value;
    }

}
