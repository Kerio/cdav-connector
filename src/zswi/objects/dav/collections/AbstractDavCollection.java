package zswi.objects.dav.collections;

import zswi.objects.dav.enums.ResourceType;


public abstract class AbstractDavCollection {

  /*
  java.util.List acl;
  java.util.List aclRestrictions;
  java.util.List currentUserPrivilegeSet;
  java.util.List inheritedAclSet;
  java.util.List supportedPrivilegeSet;
  java.util.List supportedReportSet;
  java.util.List supportedlock;
  */
  /** http://tools.ietf.org/html/rfc4918#section-15.1 Maybe be protected, so we don't add a setter */
  java.util.Date creationDate;
  
  /** http://tools.ietf.org/html/rfc5397#section-3 Propected property, no setter */
  PrincipalCollection currentUserPrincipal;
  
  /** http://tools.ietf.org/html/rfc4918#section-15.2 Might be protected, but let's have a setter */
  String displayName;
  
  /** http://tools.ietf.org/html/rfc4918#section-15.4 Computed value, hence protected */
  int getContentLength;
  
  /** http://tools.ietf.org/html/rfc4918#section-15.5 Potentially protected */
  String getContentType;
  
  /** http://tools.ietf.org/html/rfc4918#section-15.6 Must be protected */
  String getetag;
  
  /** http://tools.ietf.org/html/rfc4918#section-15.7 Should be protected */
  java.util.Date getlastmodified;
  
  /** http://tools.ietf.org/html/rfc3744#section-5.8 Protected value */
  java.net.URI principalCollectionSet;
  
  /** http://tools.ietf.org/html/rfc4918#section-15.9 Should be protected */
  java.util.List<ResourceType> resourcetype;
  
  /** namespace: http://twistedmatrix.com/xml_namespace/dav/ */
  String resourceClass;
  
  /** Always useful to have the URI of a DAV collection */
  String uri;
  
  public java.util.Date getCreationDate() {
    return creationDate;
  }

  public PrincipalCollection getCurrentUserPrincipal() {
    return currentUserPrincipal;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public int getGetContentLength() {
    return getContentLength;
  }

  public String getGetContentType() {
    return getContentType;
  }

  public String getGetetag() {
    return getetag;
  }

  public java.util.Date getGetlastmodified() {
    return getlastmodified;
  }

  public java.net.URI getPrincipalCollectionSet() {
    return principalCollectionSet;
  }

  public java.util.List<ResourceType> getResourcetype() {
    return resourcetype;
  }

  public String getResourceClass() {
    return resourceClass;
  }
  
  public String getUri() {
    return uri;
  }
  
  protected void setUri(String uri) {
    this.uri = uri;
  }

}
