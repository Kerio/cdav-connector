
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
 *         &lt;element ref="{DAV}principal"/>
 *         &lt;element ref="{DAV}grant"/>
 *         &lt;element ref="{DAV}protected"/>
 *         &lt;choice>
 *           &lt;element ref="{DAV}inherited"/>
 *           &lt;element ref="{http://twistedmatrix.com/xml_namespace/dav/}inheritable"/>
 *         &lt;/choice>
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
    "principal",
    "grant",
    "_protected",
    "inherited",
    "inheritable"
})
@XmlRootElement(name = "ace")
public class Ace {

    @XmlElement(required = true)
    protected Principal principal;
    @XmlElement(required = true)
    protected Grant grant;
    @XmlElement(name = "protected", required = true)
    protected Protected _protected;
    protected Inherited inherited;
    @XmlElement(namespace = "http://twistedmatrix.com/xml_namespace/dav/")
    protected Inheritable inheritable;

    /**
     * Gets the value of the principal property.
     * 
     * @return
     *     possible object is
     *     {@link Principal }
     *     
     */
    public Principal getPrincipal() {
        return principal;
    }

    /**
     * Sets the value of the principal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Principal }
     *     
     */
    public void setPrincipal(Principal value) {
        this.principal = value;
    }

    /**
     * Gets the value of the grant property.
     * 
     * @return
     *     possible object is
     *     {@link Grant }
     *     
     */
    public Grant getGrant() {
        return grant;
    }

    /**
     * Sets the value of the grant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Grant }
     *     
     */
    public void setGrant(Grant value) {
        this.grant = value;
    }

    /**
     * Gets the value of the protected property.
     * 
     * @return
     *     possible object is
     *     {@link Protected }
     *     
     */
    public Protected getProtected() {
        return _protected;
    }

    /**
     * Sets the value of the protected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Protected }
     *     
     */
    public void setProtected(Protected value) {
        this._protected = value;
    }

    /**
     * Gets the value of the inherited property.
     * 
     * @return
     *     possible object is
     *     {@link Inherited }
     *     
     */
    public Inherited getInherited() {
        return inherited;
    }

    /**
     * Sets the value of the inherited property.
     * 
     * @param value
     *     allowed object is
     *     {@link Inherited }
     *     
     */
    public void setInherited(Inherited value) {
        this.inherited = value;
    }

    /**
     * Gets the value of the inheritable property.
     * 
     * @return
     *     possible object is
     *     {@link Inheritable }
     *     
     */
    public Inheritable getInheritable() {
        return inheritable;
    }

    /**
     * Sets the value of the inheritable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Inheritable }
     *     
     */
    public void setInheritable(Inheritable value) {
        this.inheritable = value;
    }

}
