package zswi.objects.dav.collections;

import java.math.BigInteger;

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
  
}
