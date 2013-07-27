
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
 *         &lt;element ref="{DAV}ace"/>
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
    "ace"
})
@XmlRootElement(name = "acl")
public class Acl {

    @XmlElement(required = true)
    protected Ace ace;

    /**
     * Gets the value of the ace property.
     * 
     * @return
     *     possible object is
     *     {@link Ace }
     *     
     */
    public Ace getAce() {
        return ace;
    }

    /**
     * Sets the value of the ace property.
     * 
     * @param value
     *     allowed object is
     *     {@link Ace }
     *     
     */
    public void setAce(Ace value) {
        this.ace = value;
    }

}
