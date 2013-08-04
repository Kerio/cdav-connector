package zswi.protocols.communication.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import zswi.objects.dav.collections.AbstractDavCollection;
import zswi.objects.dav.collections.CalendarCollection;
import zswi.objects.dav.enums.DavFeature;
import zswi.protocols.communication.core.DavStore.NotSupportedComponent;
import zswi.protocols.communication.core.DavStore.UidConflict;

public class Utilities {

  public static URI initUri(String path, String scheme, String serverName, int port, String username, String password) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder();
    String decodedPath = path;
    try {
      decodedPath = java.net.URLDecoder.decode(path, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      System.out.println(e);
    }
    uriBuilder.setScheme(scheme).setHost(serverName).setPath(decodedPath).setPort(port);

    if (username != null) {
      uriBuilder.setUserInfo(username, password);
    }

    return uriBuilder.build();
  }

  public static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  } 
  
  public static String checkComponentsValidity(CalendarCollection collection, Calendar calendar) throws NotSupportedComponent, UidConflict, ValidationException {
    String uid = null;

    calendar.validate();
    
    for (Object component: calendar.getComponents()) {
      if (!(collection.getSupportedCalendarComponentSet().contains(((Component)component).getName()))) {
        throw new NotSupportedComponent("The calendar object contains components not acceptable for this collection, only " + collection.getSupportedCalendarComponentSet() + " are accepted");
      }
      Property uidForComponent = ((Component)component).getProperty(Property.UID);
      if (uid == null) {
        uid = uidForComponent.getValue();
      } else {
        if (!(uid.equals(uidForComponent.getValue()))) {
          throw new UidConflict("The UID of every component of this calendar must be the same. The culprit is " + uidForComponent.getValue() + ".");
        }
      }
    }
    
    return uid;
  }
  
  /**
   * TODO this method should be called each a collection is called, and the features/allowed methods should be stored inside the collection data structure
   * 
   * Get the list of DAV/CalDAV/CardDAV features that this server supports. This is done by looking at 
   * the "DAV" header of the response (done by a OPTIONS request).
   * 
   * @param path URL path to the calendar home set or a calendar collection.
   */
  public static void fetchFeatures(HttpClient httpClient, URI uri, AbstractDavCollection collection) {
    ArrayList<DavFeature> supportedFeatures = new ArrayList<DavFeature>();
    ArrayList<String> allowedMethods = new ArrayList<String>();
    
    try {
      HttpOptions headersMethod = new HttpOptions(uri);

      HttpResponse response = httpClient.execute(headersMethod);
      Header[] davHeaders = response.getHeaders("DAV");
      Header[] allowHeaders = response.getHeaders("Allow");

      EntityUtils.consume(response.getEntity());
      
      for (int davIndex = 0; davIndex < davHeaders.length; davIndex++) {
        Header header = davHeaders[davIndex];
        String[] featuresAsString = header.getValue().split(",");
        for (int featureIndex = 0; featureIndex < featuresAsString.length; featureIndex++) {
          DavFeature feature = DavFeature.getByFeatureName(featuresAsString[featureIndex].trim());
          supportedFeatures.add(feature);
        }
      }
      
      collection.setSupportedFeatures(supportedFeatures);
      
      for (int index = 0; index < allowHeaders.length; index++) {
        Header header = allowHeaders[index];
        String[] methodsAsString = header.getValue().split(",");
        for (int methodIndex = 0; methodIndex < methodsAsString.length; methodIndex++) {
          String feature = methodsAsString[methodIndex].trim();
          allowedMethods.add(feature);
        }
      }
      
      collection.setAllowedMethods(allowedMethods);
    }
    catch (ClientProtocolException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
