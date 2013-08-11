
package zswi.schemas.caldav.rfc6638.response;

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
 *         &lt;element ref="{urn:ietf:params:xml:ns:caldav}schedule-inbox"/>
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
    "scheduleInbox"
})
@XmlRootElement(name = "resourcetype")
public class Resourcetype {

    @XmlElement(required = true)
    protected Collection collection;
    @XmlElement(name = "schedule-inbox", namespace = "urn:ietf:params:xml:ns:caldav", required = true)
    protected ScheduleInbox scheduleInbox;

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
     * Gets the value of the scheduleInbox property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleInbox }
     *     
     */
    public ScheduleInbox getScheduleInbox() {
        return scheduleInbox;
    }

    /**
     * Sets the value of the scheduleInbox property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleInbox }
     *     
     */
    public void setScheduleInbox(ScheduleInbox value) {
        this.scheduleInbox = value;
    }

}
