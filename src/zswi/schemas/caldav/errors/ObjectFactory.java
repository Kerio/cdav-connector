
package zswi.schemas.caldav.errors;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.caldav.errors package. 
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

    private final static QName _Href_QNAME = new QName("DAV:", "href");
    private final static QName _ErrorDescription_QNAME = new QName("http://twistedmatrix.com/xml_namespace/dav/", "error-description");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.caldav.errors
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NeedsPrivilege }
     * 
     */
    public NeedsPrivilege createNeedsPrivilege() {
        return new NeedsPrivilege();
    }

    /**
     * Create an instance of {@link Error }
     * 
     */
    public Error createError() {
        return new Error();
    }

    /**
     * Create an instance of {@link CalendarCollectionLocationOk }
     * 
     */
    public CalendarCollectionLocationOk createCalendarCollectionLocationOk() {
        return new CalendarCollectionLocationOk();
    }

    /**
     * Create an instance of {@link MaxAttendeesPerInstance }
     * 
     */
    public MaxAttendeesPerInstance createMaxAttendeesPerInstance() {
        return new MaxAttendeesPerInstance();
    }

    /**
     * Create an instance of {@link MaxDateTime }
     * 
     */
    public MaxDateTime createMaxDateTime() {
        return new MaxDateTime();
    }

    /**
     * Create an instance of {@link MaxInstances }
     * 
     */
    public MaxInstances createMaxInstances() {
        return new MaxInstances();
    }

    /**
     * Create an instance of {@link MaxResourceSize }
     * 
     */
    public MaxResourceSize createMaxResourceSize() {
        return new MaxResourceSize();
    }

    /**
     * Create an instance of {@link MinDateTime }
     * 
     */
    public MinDateTime createMinDateTime() {
        return new MinDateTime();
    }

    /**
     * Create an instance of {@link SupportedCalendarComponent }
     * 
     */
    public SupportedCalendarComponent createSupportedCalendarComponent() {
        return new SupportedCalendarComponent();
    }

    /**
     * Create an instance of {@link SupportedCalendarData }
     * 
     */
    public SupportedCalendarData createSupportedCalendarData() {
        return new SupportedCalendarData();
    }

    /**
     * Create an instance of {@link ValidCalendarData }
     * 
     */
    public ValidCalendarData createValidCalendarData() {
        return new ValidCalendarData();
    }

    /**
     * Create an instance of {@link ValidCalendarObjectResource }
     * 
     */
    public ValidCalendarObjectResource createValidCalendarObjectResource() {
        return new ValidCalendarObjectResource();
    }

    /**
     * Create an instance of {@link ValidFilter }
     * 
     */
    public ValidFilter createValidFilter() {
        return new ValidFilter();
    }

    /**
     * Create an instance of {@link NumberOfMatchesWithinLimits }
     * 
     */
    public NumberOfMatchesWithinLimits createNumberOfMatchesWithinLimits() {
        return new NumberOfMatchesWithinLimits();
    }

    /**
     * Create an instance of {@link SupportedCollation }
     * 
     */
    public SupportedCollation createSupportedCollation() {
        return new SupportedCollation();
    }

    /**
     * Create an instance of {@link ResourceMustBeNull }
     * 
     */
    public ResourceMustBeNull createResourceMustBeNull() {
        return new ResourceMustBeNull();
    }

    /**
     * Create an instance of {@link InitializeCalendarCollection }
     * 
     */
    public InitializeCalendarCollection createInitializeCalendarCollection() {
        return new InitializeCalendarCollection();
    }

    /**
     * Create an instance of {@link NoUidConflict }
     * 
     */
    public NoUidConflict createNoUidConflict() {
        return new NoUidConflict();
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
    @XmlElementDecl(namespace = "http://twistedmatrix.com/xml_namespace/dav/", name = "error-description")
    public JAXBElement<String> createErrorDescription(String value) {
        return new JAXBElement<String>(_ErrorDescription_QNAME, String.class, null, value);
    }

}
