
package zswi.schemas.carddav.allprop;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{DAV}privilege"/>
 *         &lt;element ref="{DAV}supported-privilege"/>
 *         &lt;element ref="{DAV}description"/>
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
    "privilegeOrSupportedPrivilegeOrDescription"
})
@XmlRootElement(name = "supported-privilege")
public class SupportedPrivilege {

    @XmlElements({
        @XmlElement(name = "privilege", type = Privilege.class),
        @XmlElement(name = "supported-privilege", type = SupportedPrivilege.class),
        @XmlElement(name = "description", type = Description.class)
    })
    protected List<Object> privilegeOrSupportedPrivilegeOrDescription;

    /**
     * Gets the value of the privilegeOrSupportedPrivilegeOrDescription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the privilegeOrSupportedPrivilegeOrDescription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrivilegeOrSupportedPrivilegeOrDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Privilege }
     * {@link SupportedPrivilege }
     * {@link Description }
     * 
     * 
     */
    public List<Object> getPrivilegeOrSupportedPrivilegeOrDescription() {
        if (privilegeOrSupportedPrivilegeOrDescription == null) {
            privilegeOrSupportedPrivilegeOrDescription = new ArrayList<Object>();
        }
        return this.privilegeOrSupportedPrivilegeOrDescription;
    }

}
