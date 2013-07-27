
package zswi.schemas.dav.propfind.etag;

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
 *         &lt;element ref="{DAV}href"/>
 *         &lt;element ref="{DAV}propstat"/>
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
    "href",
    "propstat"
})
@XmlRootElement(name = "response")
public class Response {

    @XmlElement(required = true)
    protected String href;
    @XmlElement(required = true)
    protected Propstat propstat;

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the propstat property.
     * 
     * @return
     *     possible object is
     *     {@link Propstat }
     *     
     */
    public Propstat getPropstat() {
        return propstat;
    }

    /**
     * Sets the value of the propstat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Propstat }
     *     
     */
    public void setPropstat(Propstat value) {
        this.propstat = value;
    }

}
