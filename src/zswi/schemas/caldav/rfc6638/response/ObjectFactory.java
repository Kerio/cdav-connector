
package zswi.schemas.caldav.rfc6638.response;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.caldav.rfc6638.response package. 
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

    private final static QName _Status_QNAME = new QName("DAV:", "status");
    private final static QName _Getctag_QNAME = new QName("http://calendarserver.org/ns/", "getctag");
    private final static QName _ScheduleDefaultCalendarURL_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "schedule-default-calendar-URL");
    private final static QName _ScheduleDefaultTasksURL_QNAME = new QName("http://calendarserver.org/ns/", "schedule-default-tasks-URL");
    private final static QName _Href_QNAME = new QName("DAV:", "href");
    private final static QName _Displayname_QNAME = new QName("DAV:", "displayname");
    private final static QName _Getetag_QNAME = new QName("DAV:", "getetag");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.caldav.rfc6638.response
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link Href }
     * 
     */
    public Href createHref() {
        return new Href();
    }

    /**
     * Create an instance of {@link Propstat }
     * 
     */
    public Propstat createPropstat() {
        return new Propstat();
    }

    /**
     * Create an instance of {@link Prop }
     * 
     */
    public Prop createProp() {
        return new Prop();
    }

    /**
     * Create an instance of {@link ScheduleTag }
     * 
     */
    public ScheduleTag createScheduleTag() {
        return new ScheduleTag();
    }

    /**
     * Create an instance of {@link Resourcetype }
     * 
     */
    public Resourcetype createResourcetype() {
        return new Resourcetype();
    }

    /**
     * Create an instance of {@link Collection }
     * 
     */
    public Collection createCollection() {
        return new Collection();
    }

    /**
     * Create an instance of {@link ScheduleInbox }
     * 
     */
    public ScheduleInbox createScheduleInbox() {
        return new ScheduleInbox();
    }

    /**
     * Create an instance of {@link Multistatus }
     * 
     */
    public Multistatus createMultistatus() {
        return new Multistatus();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "status")
    public JAXBElement<String> createStatus(String value) {
        return new JAXBElement<String>(_Status_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://calendarserver.org/ns/", name = "getctag")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createGetctag(String value) {
        return new JAXBElement<String>(_Getctag_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Href }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:caldav", name = "schedule-default-calendar-URL")
    public JAXBElement<Href> createScheduleDefaultCalendarURL(Href value) {
        return new JAXBElement<Href>(_ScheduleDefaultCalendarURL_QNAME, Href.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Href }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://calendarserver.org/ns/", name = "schedule-default-tasks-URL")
    public JAXBElement<Href> createScheduleDefaultTasksURL(Href value) {
        return new JAXBElement<Href>(_ScheduleDefaultTasksURL_QNAME, Href.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "href")
    public JAXBElement<String> createHref(String value) {
        return new JAXBElement<String>(_Href_QNAME, String.class, null, value);
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

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "getetag")
    public JAXBElement<String> createGetetag(String value) {
        return new JAXBElement<String>(_Getetag_QNAME, String.class, null, value);
    }

}
