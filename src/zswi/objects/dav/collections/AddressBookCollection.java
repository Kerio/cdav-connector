package zswi.objects.dav.collections;

import zswi.objects.dav.enums.SupportedData;

public class AddressBookCollection extends AbstractNotPrincipalCollection {

  /** http://tools.ietf.org/html/rfc6352#section-6.2.2 */
  java.util.List<SupportedData> supportedAddressData;

  java.util.List<SupportedData> getSupportedAddressData() {
    return supportedAddressData;
  }
  
}
