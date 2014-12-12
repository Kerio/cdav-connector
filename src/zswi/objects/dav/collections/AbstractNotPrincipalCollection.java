package zswi.objects.dav.collections;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import zswi.protocols.communication.core.HTTPConnectionManager;
import zswi.protocols.communication.core.DavStore.DavStoreException;
import zswi.protocols.communication.core.requests.DeleteRequest;
import zswi.protocols.communication.core.requests.UpdateRequest;

public abstract class AbstractNotPrincipalCollection extends AbstractDavCollection {

  /** http://tools.ietf.org/html/rfc4918#section-14.17 */
  PrincipalCollection owner;
  
  /** http://tools.ietf.org/html/rfc4331#section-3 */
  BigInteger quotaAvailableBytes;
  
  /** http://tools.ietf.org/html/rfc4331#section-4 */
  BigInteger quotaUsedBytes;
  
  /** http://tools.ietf.org/html/rfc6578#section-4 */
  java.net.URI syncToken;
  
  /** http://svn.calendarserver.org/repository/calendarserver/CalendarServer/trunk/doc/Extensions/caldav-ctag.txt */ 
  String getctag;
  
  public PrincipalCollection getOwner() {
    return owner;
  }
  
  protected void setOwner(PrincipalCollection owner) {
    this.owner = owner;
  }
  
  public BigInteger getQuotaAvailableBytes() {
    return quotaAvailableBytes;
  }
  
  protected void setQuotaAvailableBytes(BigInteger bigInteger) {
    this.quotaAvailableBytes = bigInteger;
  }
  
  public BigInteger getQuotaUsedBytes() {
    return quotaUsedBytes;
  }
  
  protected void setQuotaUsedBytes(BigInteger bigInteger) {
    this.quotaUsedBytes = bigInteger;
  }
  
  public java.net.URI getSyncToken() {
    return syncToken;
  }
  
  protected void setSyncToken(java.net.URI syncToken) {
    this.syncToken = syncToken;
  }
  
  public String getGetctag() {
    return getctag;
  }
  
  protected void setGetctag(String getctag) {
    this.getctag = getctag;
  }
  
  protected String updateObject(HTTPConnectionManager connectionManager, HttpEntity entity, String etag, String path) throws DavStoreException {
    UpdateRequest updateReq;
    try {
      updateReq = new UpdateRequest(connectionManager.initUri(path), etag);
      updateReq.setEntity(entity);
      HttpResponse resp = connectionManager.getHttpClient().execute(updateReq);

      EntityUtils.consume(resp.getEntity());

      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
        Header[] headers = resp.getHeaders("Etag");
        if (headers.length == 1) {
          return headers[0].getValue();
        }
      }
    }
    catch (URISyntaxException e) {
      throw new DavStoreException(e);
    }
    catch (ClientProtocolException e) {
      throw new DavStoreException(e);
    }
    catch (IOException e) {
      throw new DavStoreException(e);
    }
    return null;
  }

  protected boolean delete(HTTPConnectionManager connectionManager, String path, String etag) throws URISyntaxException, ClientProtocolException, IOException{
    DeleteRequest del = new DeleteRequest(connectionManager.initUri(path), etag);
    HttpResponse resp = connectionManager.getHttpClient().execute(del);
    EntityUtils.consume(resp.getEntity());
    if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) return true;
    else return false;
  }

  
}
