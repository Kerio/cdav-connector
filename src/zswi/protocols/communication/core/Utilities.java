package zswi.protocols.communication.core;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.http.client.utils.URIBuilder;

import zswi.objects.dav.collections.CalendarCollection;
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

}
