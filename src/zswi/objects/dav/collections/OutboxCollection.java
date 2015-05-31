package zswi.objects.dav.collections;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import zswi.protocols.communication.core.requests.PropfindRequest;

public class OutboxCollection extends AbstractNotPrincipalCollection {

  public OutboxCollection(DefaultHttpClient _httpClient, PrincipalCollection principals, URI uriForRequest) throws JAXBException, ClientProtocolException, IOException, URISyntaxException {
    PropfindRequest req = new PropfindRequest(uriForRequest, 1);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("rfc6638-request.xml");            
//    InputStream is = ClassLoader.getSystemResourceAsStream("rfc6638-request.xml");

    StringEntity se = new StringEntity(convertStreamToString(is));

    se.setContentType("text/xml");
    req.setEntity(se);

    HttpResponse resp = _httpClient.execute(req);
    
    JAXBContext jc = JAXBContext.newInstance("zswi.schemas.caldav.rfc6638.response");
    Unmarshaller appPropUnmarshaller = jc.createUnmarshaller();
    zswi.schemas.caldav.rfc6638.response.Multistatus unmarshalAllProp = (zswi.schemas.caldav.rfc6638.response.Multistatus)appPropUnmarshaller.unmarshal(resp.getEntity().getContent());
    
    zswi.schemas.caldav.rfc6638.response.Response response = unmarshalAllProp.getResponse();
    
    for (zswi.schemas.caldav.rfc6638.response.Propstat propstat: response.getPropstat()) {
      if ("HTTP/1.1 200 OK".equals(propstat.getStatus())) {
        setOwner(principals);
        setDisplayName(propstat.getProp().getDisplayname());
        setGetctag(propstat.getProp().getGetctag());
        
        setGetctag(propstat.getProp().getGetctag());
        setUri(response.getHref());
        principals.setOutboxCollection(this);
      }
    }
    EntityUtils.consume(resp.getEntity());
  }
  
}
