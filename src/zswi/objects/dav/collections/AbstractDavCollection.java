package zswi.objects.dav.collections;

import java.util.ArrayList;

import zswi.objects.dav.enums.DavFeature;
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
  
  protected ArrayList<String> allowedMethods;
  protected ArrayList<DavFeature> supportedFeatures;
  
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
  
  protected String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
  
  public ArrayList<String> allowedMethods() {
    if (allowedMethods == null) {
      allowedMethods = new ArrayList<String>();
    }
    return allowedMethods;
  }

  public void setAllowedMethods(ArrayList<String> allowedMethods2) {
    this.allowedMethods = allowedMethods2;
  }
  
  public ArrayList<DavFeature> supportedFeatures() {
    if (supportedFeatures == null) {
      supportedFeatures = new ArrayList<DavFeature>();
    }
    return supportedFeatures;
  }

  public void setSupportedFeatures(ArrayList<DavFeature> supportedFeatures2) {
    this.supportedFeatures = supportedFeatures2;
  } 
  
}
