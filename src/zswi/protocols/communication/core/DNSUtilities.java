package zswi.protocols.communication.core;

import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class DNSUtilities {

  public static HashMap<String, String> doTXTLookup(String nameToLookup) throws NamingException {
    HashMap<String, String> values = new HashMap<String, String>();

    Attributes txtAttrs =  doDNSLookup(nameToLookup, "TXT");
    if ((txtAttrs != null) && (txtAttrs.size() > 0)) {
      String txtValue = (String)txtAttrs.get("txt").get();
      // path=/
      String[] value = txtValue.split("=");
      if (value.length == 2) 
        if ("path".equals(value[0]))
          values.put("path", value[1]);
    } else {
      throw new NamingException("No TXT record found");
    }
    
    return values;
  }
  
  public static HashMap<String, String> doSRVLookup(String nameToLookup) throws NamingException {
    Attributes attrs = doDNSLookup(nameToLookup, "SRV");
    String srvValue = (String)attrs.get("srv").get();
    // 10 1 8008 server.domain.com.
    String[] dnsEntry = srvValue.split("\\s+");
    HashMap<String, String> values = new HashMap<String, String>();
    values.put("priority", dnsEntry[0]);
    values.put("weight", dnsEntry[1]);
    values.put("port", dnsEntry[2]);
    String hostName = dnsEntry[3];
    if (hostName.endsWith(".")) {
      hostName = hostName.substring(0, hostName.length() - 1);
    }
    values.put("host", hostName);
    return values;
  }
  
  public static Attributes doDNSLookup(String nameToLookup, String entryType) throws NamingException {
    Hashtable env = new Hashtable();
    env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
    env.put("java.naming.provider.url", "dns:");
    env.put("com.sun.jndi.dns.timeout.initial", "1000");
    env.put("com.sun.jndi.dns.timeout.retries","2");
    DirContext ctx;
    ctx = new InitialDirContext(env);
    Attributes attrs = ctx.getAttributes(nameToLookup, new String[] { entryType });
    return attrs;
  }
  
}
