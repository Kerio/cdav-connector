
package zswi.schemas.caldav.rfc6638;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.caldav.rfc6638 package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.caldav.rfc6638
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Prop }
     * 
     */
    public Prop createProp() {
        return new Prop();
    }

    /**
     * Create an instance of {@link Displayname }
     * 
     */
    public Displayname createDisplayname() {
        return new Displayname();
    }

    /**
     * Create an instance of {@link ScheduleDefaultTasksURL }
     * 
     */
    public ScheduleDefaultTasksURL createScheduleDefaultTasksURL() {
        return new ScheduleDefaultTasksURL();
    }

    /**
     * Create an instance of {@link ScheduleDefaultCalendarURL }
     * 
     */
    public ScheduleDefaultCalendarURL createScheduleDefaultCalendarURL() {
        return new ScheduleDefaultCalendarURL();
    }

    /**
     * Create an instance of {@link Getctag }
     * 
     */
    public Getctag createGetctag() {
        return new Getctag();
    }

    /**
     * Create an instance of {@link Resourcetype }
     * 
     */
    public Resourcetype createResourcetype() {
        return new Resourcetype();
    }

    /**
     * Create an instance of {@link Getetag }
     * 
     */
    public Getetag createGetetag() {
        return new Getetag();
    }

    /**
     * Create an instance of {@link ScheduleTag }
     * 
     */
    public ScheduleTag createScheduleTag() {
        return new ScheduleTag();
    }

    /**
     * Create an instance of {@link Propfind }
     * 
     */
    public Propfind createPropfind() {
        return new Propfind();
    }

}
