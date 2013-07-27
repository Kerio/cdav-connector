package zswi.objects.dav.collections;

import java.util.ArrayList;

import org.apache.http.impl.client.DefaultHttpClient;

public class AddressBookCollection extends AbstractNotPrincipalCollection {

  /** http://tools.ietf.org/html/rfc6352#section-6.2.2 */
  java.util.List<String> supportedAddressData;
  
  DefaultHttpClient httpClient;

  public AddressBookCollection(DefaultHttpClient _httpClient) {
    httpClient = _httpClient;
  }
  
  public java.util.List<String> getSupportedAddressData() {
    if (supportedAddressData == null)
      supportedAddressData = new ArrayList<String>();
    return supportedAddressData;
  }
    
}
