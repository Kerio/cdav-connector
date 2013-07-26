
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
 *       &lt;choice>
 *         &lt;element ref="{DAV}write"/>
 *         &lt;element ref="{DAV}all"/>
 *         &lt;element ref="{DAV}bind"/>
 *         &lt;element ref="{DAV}read"/>
 *         &lt;element ref="{DAV}read-acl"/>
 *         &lt;element ref="{DAV}read-current-user-privilege-set"/>
 *         &lt;element ref="{DAV}unbind"/>
 *         &lt;element ref="{DAV}unlock"/>
 *         &lt;element ref="{DAV}write-acl"/>
 *         &lt;element ref="{DAV}write-content"/>
 *         &lt;element ref="{DAV}write-properties"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}read-free-busy"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "write",
    "all",
    "bind",
    "read",
    "readAcl",
    "readCurrentUserPrivilegeSet",
    "unbind",
    "unlock",
    "writeAcl",
    "writeContent",
    "writeProperties",
    "readFreeBusy"
})
@XmlRootElement(name = "privilege")
public class Privilege {

    protected Write write;
    protected All all;
    protected Bind bind;
    protected Read read;
    @XmlElement(name = "read-acl")
    protected ReadAcl readAcl;
    @XmlElement(name = "read-current-user-privilege-set")
    protected ReadCurrentUserPrivilegeSet readCurrentUserPrivilegeSet;
    protected Unbind unbind;
    protected Unlock unlock;
    @XmlElement(name = "write-acl")
    protected WriteAcl writeAcl;
    @XmlElement(name = "write-content")
    protected WriteContent writeContent;
    @XmlElement(name = "write-properties")
    protected WriteProperties writeProperties;
    @XmlElement(name = "read-free-busy", namespace = "urn:ietf:params:xml:ns:caldav")
    protected ReadFreeBusy readFreeBusy;

    /**
     * Gets the value of the write property.
     * 
     * @return
     *     possible object is
     *     {@link Write }
     *     
     */
    public Write getWrite() {
        return write;
    }

    /**
     * Sets the value of the write property.
     * 
     * @param value
     *     allowed object is
     *     {@link Write }
     *     
     */
    public void setWrite(Write value) {
        this.write = value;
    }

    /**
     * Gets the value of the all property.
     * 
     * @return
     *     possible object is
     *     {@link All }
     *     
     */
    public All getAll() {
        return all;
    }

    /**
     * Sets the value of the all property.
     * 
     * @param value
     *     allowed object is
     *     {@link All }
     *     
     */
    public void setAll(All value) {
        this.all = value;
    }

    /**
     * Gets the value of the bind property.
     * 
     * @return
     *     possible object is
     *     {@link Bind }
     *     
     */
    public Bind getBind() {
        return bind;
    }

    /**
     * Sets the value of the bind property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bind }
     *     
     */
    public void setBind(Bind value) {
        this.bind = value;
    }

    /**
     * Gets the value of the read property.
     * 
     * @return
     *     possible object is
     *     {@link Read }
     *     
     */
    public Read getRead() {
        return read;
    }

    /**
     * Sets the value of the read property.
     * 
     * @param value
     *     allowed object is
     *     {@link Read }
     *     
     */
    public void setRead(Read value) {
        this.read = value;
    }

    /**
     * Gets the value of the readAcl property.
     * 
     * @return
     *     possible object is
     *     {@link ReadAcl }
     *     
     */
    public ReadAcl getReadAcl() {
        return readAcl;
    }

    /**
     * Sets the value of the readAcl property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadAcl }
     *     
     */
    public void setReadAcl(ReadAcl value) {
        this.readAcl = value;
    }

    /**
     * Gets the value of the readCurrentUserPrivilegeSet property.
     * 
     * @return
     *     possible object is
     *     {@link ReadCurrentUserPrivilegeSet }
     *     
     */
    public ReadCurrentUserPrivilegeSet getReadCurrentUserPrivilegeSet() {
        return readCurrentUserPrivilegeSet;
    }

    /**
     * Sets the value of the readCurrentUserPrivilegeSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadCurrentUserPrivilegeSet }
     *     
     */
    public void setReadCurrentUserPrivilegeSet(ReadCurrentUserPrivilegeSet value) {
        this.readCurrentUserPrivilegeSet = value;
    }

    /**
     * Gets the value of the unbind property.
     * 
     * @return
     *     possible object is
     *     {@link Unbind }
     *     
     */
    public Unbind getUnbind() {
        return unbind;
    }

    /**
     * Sets the value of the unbind property.
     * 
     * @param value
     *     allowed object is
     *     {@link Unbind }
     *     
     */
    public void setUnbind(Unbind value) {
        this.unbind = value;
    }

    /**
     * Gets the value of the unlock property.
     * 
     * @return
     *     possible object is
     *     {@link Unlock }
     *     
     */
    public Unlock getUnlock() {
        return unlock;
    }

    /**
     * Sets the value of the unlock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Unlock }
     *     
     */
    public void setUnlock(Unlock value) {
        this.unlock = value;
    }

    /**
     * Gets the value of the writeAcl property.
     * 
     * @return
     *     possible object is
     *     {@link WriteAcl }
     *     
     */
    public WriteAcl getWriteAcl() {
        return writeAcl;
    }

    /**
     * Sets the value of the writeAcl property.
     * 
     * @param value
     *     allowed object is
     *     {@link WriteAcl }
     *     
     */
    public void setWriteAcl(WriteAcl value) {
        this.writeAcl = value;
    }

    /**
     * Gets the value of the writeContent property.
     * 
     * @return
     *     possible object is
     *     {@link WriteContent }
     *     
     */
    public WriteContent getWriteContent() {
        return writeContent;
    }

    /**
     * Sets the value of the writeContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link WriteContent }
     *     
     */
    public void setWriteContent(WriteContent value) {
        this.writeContent = value;
    }

    /**
     * Gets the value of the writeProperties property.
     * 
     * @return
     *     possible object is
     *     {@link WriteProperties }
     *     
     */
    public WriteProperties getWriteProperties() {
        return writeProperties;
    }

    /**
     * Sets the value of the writeProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link WriteProperties }
     *     
     */
    public void setWriteProperties(WriteProperties value) {
        this.writeProperties = value;
    }

    /**
     * Gets the value of the readFreeBusy property.
     * 
     * @return
     *     possible object is
     *     {@link ReadFreeBusy }
     *     
     */
    public ReadFreeBusy getReadFreeBusy() {
        return readFreeBusy;
    }

    /**
     * Sets the value of the readFreeBusy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadFreeBusy }
     *     
     */
    public void setReadFreeBusy(ReadFreeBusy value) {
        this.readFreeBusy = value;
    }

}
