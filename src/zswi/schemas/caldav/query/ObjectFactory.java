
package zswi.schemas.caldav.query;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.caldav.query package. 
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

    private final static QName _Timezone_QNAME = new QName("urn:ietf:params:xml:ns:caldav", "timezone");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.caldav.query
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IsNotDefined }
     * 
     */
    public IsNotDefined createIsNotDefined() {
        return new IsNotDefined();
    }

    /**
     * Create an instance of {@link Cprop }
     * 
     */
    public Cprop createCprop() {
        return new Cprop();
    }

    /**
     * Create an instance of {@link TimeRange }
     * 
     */
    public TimeRange createTimeRange() {
        return new TimeRange();
    }

    /**
     * Create an instance of {@link Comp }
     * 
     */
    public Comp createComp() {
        return new Comp();
    }

    /**
     * Create an instance of {@link TextMatch }
     * 
     */
    public TextMatch createTextMatch() {
        return new TextMatch();
    }

    /**
     * Create an instance of {@link PropFilter }
     * 
     */
    public PropFilter createPropFilter() {
        return new PropFilter();
    }

    /**
     * Create an instance of {@link ParamFilter }
     * 
     */
    public ParamFilter createParamFilter() {
        return new ParamFilter();
    }

    /**
     * Create an instance of {@link LimitRecurrenceSet }
     * 
     */
    public LimitRecurrenceSet createLimitRecurrenceSet() {
        return new LimitRecurrenceSet();
    }

    /**
     * Create an instance of {@link CalendarQuery }
     * 
     */
    public CalendarQuery createCalendarQuery() {
        return new CalendarQuery();
    }

    /**
     * Create an instance of {@link Allprop }
     * 
     */
    public Allprop createAllprop() {
        return new Allprop();
    }

    /**
     * Create an instance of {@link Propname }
     * 
     */
    public Propname createPropname() {
        return new Propname();
    }

    /**
     * Create an instance of {@link Prop }
     * 
     */
    public Prop createProp() {
        return new Prop();
    }

    /**
     * Create an instance of {@link Getetag }
     * 
     */
    public Getetag createGetetag() {
        return new Getetag();
    }

    /**
     * Create an instance of {@link CalendarData }
     * 
     */
    public CalendarData createCalendarData() {
        return new CalendarData();
    }

    /**
     * Create an instance of {@link Expand }
     * 
     */
    public Expand createExpand() {
        return new Expand();
    }

    /**
     * Create an instance of {@link LimitFreebusySet }
     * 
     */
    public LimitFreebusySet createLimitFreebusySet() {
        return new LimitFreebusySet();
    }

    /**
     * Create an instance of {@link Filter }
     * 
     */
    public Filter createFilter() {
        return new Filter();
    }

    /**
     * Create an instance of {@link CompFilter }
     * 
     */
    public CompFilter createCompFilter() {
        return new CompFilter();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:caldav", name = "timezone")
    public JAXBElement<String> createTimezone(String value) {
        return new JAXBElement<String>(_Timezone_QNAME, String.class, null, value);
    }

}
