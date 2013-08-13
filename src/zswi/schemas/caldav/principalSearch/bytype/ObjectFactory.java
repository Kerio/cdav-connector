
package zswi.schemas.caldav.principalSearch.bytype;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.caldav.principalSearch.bytype package. 
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

    private final static QName _Match_QNAME = new QName("DAV:", "match");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.caldav.principalSearch.bytype
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PrincipalPropertySearch }
     * 
     */
    public PrincipalPropertySearch createPrincipalPropertySearch() {
        return new PrincipalPropertySearch();
    }

    /**
     * Create an instance of {@link PropertySearch }
     * 
     */
    public PropertySearch createPropertySearch() {
        return new PropertySearch();
    }

    /**
     * Create an instance of {@link Prop }
     * 
     */
    public Prop createProp() {
        return new Prop();
    }

    /**
     * Create an instance of {@link CalendarUserAddressSet }
     * 
     */
    public CalendarUserAddressSet createCalendarUserAddressSet() {
        return new CalendarUserAddressSet();
    }

    /**
     * Create an instance of {@link CalendarUserType }
     * 
     */
    public CalendarUserType createCalendarUserType() {
        return new CalendarUserType();
    }

    /**
     * Create an instance of {@link RecordType }
     * 
     */
    public RecordType createRecordType() {
        return new RecordType();
    }

    /**
     * Create an instance of {@link FirstName }
     * 
     */
    public FirstName createFirstName() {
        return new FirstName();
    }

    /**
     * Create an instance of {@link LastName }
     * 
     */
    public LastName createLastName() {
        return new LastName();
    }

    /**
     * Create an instance of {@link AutoSchedule }
     * 
     */
    public AutoSchedule createAutoSchedule() {
        return new AutoSchedule();
    }

    /**
     * Create an instance of {@link AutoScheduleMode }
     * 
     */
    public AutoScheduleMode createAutoScheduleMode() {
        return new AutoScheduleMode();
    }

    /**
     * Create an instance of {@link Resourcetype }
     * 
     */
    public Resourcetype createResourcetype() {
        return new Resourcetype();
    }

    /**
     * Create an instance of {@link Displayname }
     * 
     */
    public Displayname createDisplayname() {
        return new Displayname();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "match")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createMatch(String value) {
        return new JAXBElement<String>(_Match_QNAME, String.class, null, value);
    }

}
