
package zswi.schemas.carddav.multiget;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zswi.schemas.carddav.multiget package. 
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

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zswi.schemas.carddav.multiget
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddressbookMultiget }
     * 
     */
    public AddressbookMultiget createAddressbookMultiget() {
        return new AddressbookMultiget();
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
     * Create an instance of {@link AddressData }
     * 
     */
    public AddressData createAddressData() {
        return new AddressData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "DAV:", name = "href")
    public JAXBElement<String> createHref(String value) {
        return new JAXBElement<String>(_Href_QNAME, String.class, null, value);
    }

}
