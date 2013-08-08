
package zswi.schemas.caldav.proppatch;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.caldav.proppatch package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CalendarColor_QNAME = new QName("http://apple.com/ns/ical/", "calendar-color");
    private final static QName _CalendarDescription_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "calendar-description");
    private final static QName _CalendarOrder_QNAME = new QName("http://apple.com/ns/ical/", "calendar-order");
    private final static QName _CalendarTimezone_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "calendar-timezone");
    private final static QName _Displayname_QNAME = new QName("DAV:", "displayname");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.caldav.proppatch
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Set }
     * 
     */
    public Set createSet() {
        return new Set();
    }

    /**
     * Create an instance of {@link Prop }
     * 
     */
    public Prop createProp() {
        return new Prop();
    }

    /**
     * Create an instance of {@link ScheduleCalendarTransp }
     * 
     */
    public ScheduleCalendarTransp createScheduleCalendarTransp() {
        return new ScheduleCalendarTransp();
    }

    /**
     * Create an instance of {@link Transparent }
     * 
     */
    public Transparent createTransparent() {
        return new Transparent();
    }

    /**
     * Create an instance of {@link Opaque }
     * 
     */
    public Opaque createOpaque() {
        return new Opaque();
    }

    /**
     * Create an instance of {@link Propertyupdate }
     * 
     */
    public Propertyupdate createPropertyupdate() {
        return new Propertyupdate();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://apple.com/ns/ical/", name = "calendar-color")
    public JAXBElement<String> createCalendarColor(String value) {
        return new JAXBElement<String>(_CalendarColor_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:caldav", name = "calendar-description")
    public JAXBElement<String> createCalendarDescription(String value) {
        return new JAXBElement<String>(_CalendarDescription_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://apple.com/ns/ical/", name = "calendar-order")
    public JAXBElement<BigInteger> createCalendarOrder(BigInteger value) {
        return new JAXBElement<BigInteger>(_CalendarOrder_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:caldav", name = "calendar-timezone")
    public JAXBElement<String> createCalendarTimezone(String value) {
        return new JAXBElement<String>(_CalendarTimezone_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "displayname")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createDisplayname(String value) {
        return new JAXBElement<String>(_Displayname_QNAME, String.class, null, value);
    }

}
