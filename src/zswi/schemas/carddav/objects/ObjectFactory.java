
package zswi.schemas.carddav.objects;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.carddav.objects package. 
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

    private final static QName _AddressData_QNAME = new QName("urn:ietf:params:xml:ns:carddav", "address-data");
    private final static QName _Status_QNAME = new QName("DAV:", "status");
    private final static QName _Href_QNAME = new QName("DAV:", "href");
    private final static QName _Getetag_QNAME = new QName("DAV:", "getetag");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.carddav.objects
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
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:carddav", name = "address-data")
    public JAXBElement<String> createAddressData(String value) {
        return new JAXBElement<String>(_AddressData_QNAME, String.class, null, value);
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
    @XmlElementDecl(namespace = "DAV:", name = "href")
    public JAXBElement<String> createHref(String value) {
        return new JAXBElement<String>(_Href_QNAME, String.class, null, value);
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
