/*
Copyright 2013 Jindrich Pouba

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package zswi.protocols.communication.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import zswi.protocols.caldav.CalendarsGetter;
import zswi.protocols.caldav.ServerCalendar;
import zswi.protocols.caldav.ServerVEvent;
import zswi.protocols.caldav.VEvent_XML_Parser;
import zswi.protocols.carddav.ServerVCard;
import zswi.protocols.carddav.VCard_XML_Parser;
import zswi.protocols.communication.core.requests.DeleteRequest;
import zswi.protocols.communication.core.requests.PropfindRequest;
import zswi.protocols.communication.core.requests.PutRequest;
import zswi.protocols.communication.core.requests.ReportRequest;
import zswi.protocols.communication.core.requests.UpdateRequest;


/**
 * Object of this class represents the actual connection to a server and provides
 * data management. By data we mean contacts and calendars with events. 
 * 
 * 
 * @author Jan Ambrož
 *
 */
public class HTTPSConnection {
	/** https protocol type */
	private static final String SCHEME_HTTPS = "https";
	/** http header vcard description */
	private static final String TYPE_VCARD = "text/vcard";
	/** http header calendar description */
	private static final String TYPE_VEVENT = "text/calendar";
	/** http header xml description */
	private static final String TYPE_XML = "text/xml; charset=\"utf-8\"";
	/** response code for OK Created */
	private static final int CODE_OK_CREATED = 201;
	/** response code for OK */
	private static final int CODE_OK = 200;
	/** response code for NO Content */
	private static final int CODE_NO_CONTENT = 204;
	/** default ssl port number */
	private static final int DEF_SSL_PORT = 443;
	/** default keystore password */
	private static final String DEF_KS_PASS = "changeit";
	//change directory 
	public static String defKeystoreLocation = System.getProperty("user.home") + File.separator + "jssecacerts";

	/** username */
	private String username;
	/** password */
	private String password;
	/** servername */
	private String servername;
	/** server port */
	private int port;
	/** server certificate will be installed */ 
	private boolean installCert;
	/** keystore password */
	private String keystorePass;
	/** domain */
	private String domain;

	/** default contacts path */
	private String defaultContactsPath;
	/** default calendar path */
	private String defaultCalendarPath;

	/** http client */
	private DefaultHttpClient client;

	// Constructors

	/**
		Constructor of https connection.
		@param servername server name
		@param installCert add server ssl certificate to truststore  ('true' mean yes)
	 */
	public HTTPSConnection(String servername, boolean installCert) throws InstallCertException, InitKeystoreException {
		this(servername, null, null);
	}

	/**
		Constructor of https connection.
		@param servername server name
		@param username user name
		@param password user password
	 */
	public HTTPSConnection(String servername, String username, String password) throws InstallCertException, InitKeystoreException {
		this(servername, username, password, DEF_SSL_PORT, true);
	}

	public HTTPSConnection(String servername, String domain, String username, String password) throws InstallCertException, InitKeystoreException {
	  this(servername, domain, username, password, DEF_SSL_PORT, true);
	}

	/**
		Constructor of https connection.
		@param servername server name
		@param username user name
		@param password user password
		@param installCert add server ssl certificate to truststore  ('true' mean yes)
	 */
	public HTTPSConnection(String servername, String username, String password, int port, boolean installCert) throws InstallCertException, InitKeystoreException{
		this( servername, null, username,  password,  port,  installCert, DEF_KS_PASS);
	}
	
	public HTTPSConnection(String servername, String domain, String username, String password, int port, boolean installCert) throws InstallCertException, InitKeystoreException{
	  this( servername, domain, username,  password,  port,  installCert, DEF_KS_PASS);
	}
	
	/**
		Constructor of https connection.
		@param servername server name
		@param username user name
		@param password user password
		@param installCert add server ssl certificate to truststore  ('true' mean yes)
		@param keystorePass keystore password
	 */
	public HTTPSConnection(String servername, String domain, String username, String password, int port, boolean installCert, String keystorePass) throws InstallCertException, InitKeystoreException {
		this.servername = servername;
		this.username = username;
		this.password = password;
		this.installCert = installCert;
		this.port = port;
		this.keystorePass = keystorePass;
		if (domain == null) {
		  this.domain = servername;
		} else {
		  this.domain = domain;
		}

		this.defaultContactsPath = "/contacts/" + domain + "/" + username + "/addressbook/";
		this.defaultCalendarPath = "/full-calendars/" + domain + "/" + username + "/Calendar/";

		this.client = new DefaultHttpClient();
		this.init();
	}

	// Methods for adding VEvents

	/**
		This method provides adding VEvent to default calendar.
		@param file	file containing VEvent
		@return true if adding was successful, otherwise return false
	 */
	public boolean addVEvent(File file) throws ClientProtocolException, IOException, URISyntaxException {
		return this.addVEvent(file, null);
	}

	/**
		This method provides adding VEvent to calendar passed in the second argument.
		@param file	file containing VEvent
		@param calendar destination for adding VEvent 
		@return true if adding was successful, otherwise return false
	 */
	public boolean addVEvent(File file, ServerCalendar calendar) throws ClientProtocolException, IOException, URISyntaxException {
		FileEntity fe = new FileEntity(file);
		fe.setContentType(TYPE_VEVENT);
		String path;
		if (calendar == null) {
			path = defaultCalendarPath;
		} else {
			path = calendar.getPath();
		}
		return this.addVEvent(fe, path);
	}

	/**
		This method provides adding VEvent to default calendar.
		@param event	VEvent object
		@return true if adding was successful, otherwise return false
	 */
	public boolean addVEvent(VEvent event) throws ClientProtocolException, IOException, URISyntaxException {
		return this.addVEvent(event, null);
	}

	/**
		This method provides adding VEvent to calendar passed in the second argument.
		@param event	VEvent object
		@param calendar destination for adding VEvent 
		@return true if adding was successful, otherwise return false
	 */
	public boolean addVEvent(VEvent event, ServerCalendar calendar) throws ClientProtocolException, IOException, URISyntaxException {

		// vEvent must be enclosed in Calendar, otherwise is not added
		Calendar calendarForEvent = new Calendar();
		calendarForEvent.getComponents().add(event);

		StringEntity se = new StringEntity(calendarForEvent.toString());
		se.setContentType(TYPE_VEVENT);
		String path;
		if (calendar == null) {
			path = defaultCalendarPath;
		} else {
			path = calendar.getPath();
		}
		return this.addVEvent(se, path);
	}

	/**
		This method provides adding VEvent to default calendar.
		@param entity http entity containg VEvent
		@param path destination path for adding VEvent
		@return true if adding was successful, otherwise return false
	 */
	private boolean addVEvent(HttpEntity entity, String path) throws URISyntaxException, ClientProtocolException, IOException {
		PutRequest putReq = new PutRequest(initUri(path));
		putReq.setEntity(entity);
		HttpResponse resp = client.execute(putReq);
		EntityUtils.consume(resp.getEntity());
		if (resp.getStatusLine().getStatusCode() == CODE_OK_CREATED) {
			return true;
		} else {
			return false;
		}
	}

	// Methods for adding VCards

	/**
		This method provides adding VCard to contacts.
		@param file file containing VCard
		@return true if adding was successful, otherwise return false
	 */
	public boolean addVCard(File file) throws URISyntaxException, IOException {
		FileEntity fe = new FileEntity(file);
		fe.setContentType(TYPE_VCARD);
		return this.addVCard(fe, defaultContactsPath);
	}

	/**
		This method provides adding VCard to contacts.
		@param card VCard object
		@return true if adding was successful, otherwise return false
	 */
	public boolean addVCard(VCard card) throws ClientProtocolException, IOException, URISyntaxException {
		VCardWriter wr = new VCardWriter();
		wr.setVCard(card);

		StringEntity se = new StringEntity(wr.buildVCardString());
		se.setContentType(TYPE_VCARD);
		return this.addVCard(se, defaultContactsPath);
	}

	/**
		This method provides adding VCard to contacts.
		@param entity entity with VCard object
		@param path path destination for adding VCard
		@return true if adding was successful, otherwise return false
	 */
	private boolean addVCard(HttpEntity entity, String path) throws URISyntaxException, ClientProtocolException, IOException {
		PutRequest putReq = new PutRequest(initUri(path));
		putReq.setEntity(entity);
		HttpResponse resp = client.execute(putReq);
		EntityUtils.consume(resp.getEntity());
		if (resp.getStatusLine().getStatusCode() == CODE_OK_CREATED) {
			return true;
		} else {
			return false;
		}
	}

	// Methods for getting calendars on server

	/**
		This method provides getting list with calendar objects.
		@return list with ServerCalendar objects		
	 */
	public List<ServerCalendar> getCalendars() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
		String response = this.propfind("propfind_calendars.txt", "/full-calendars/"+domain+"/"+username+"/");
		List<ServerCalendar> calendars = CalendarsGetter.getCalendars(response);
		return calendars;
	}

	// Methods for getting cards on server

	/**
		This method provides getting list with VCard objects,
		@return list with ServerVCard objects
	 */
	public List<ServerVCard> getVCards() throws ClientProtocolException, IOException, URISyntaxException {

		String reportResponse = this.report("rep_contacts.txt", defaultContactsPath, 1);
		VCard_XML_Parser p = new VCard_XML_Parser();
		List<ServerVCard> result = p.parseMultiVCard(reportResponse);
		return result;
	}

	// Methods for getting events on server

	/**
		This method provides getting list with VEvent objects from default calendar.
		@return list with ServerVEvent objects
	 * @throws ParserException 
	 */
	public List<ServerVEvent> getVEvents() throws ClientProtocolException, IOException, URISyntaxException, ParserException {
		return this.getVEvents(null);
	}

	/**
		This method provides getting list with VEvent objects from calendar passed in parameter.
		@param calendar source calendar
		@return list with ServerVEvent objects
	 * @throws ParserException 
	 */
	public List<ServerVEvent> getVEvents(ServerCalendar calendar) throws ClientProtocolException, IOException, URISyntaxException, ParserException {

		String path;
		if (calendar == null) {
			path = defaultCalendarPath;
		} else {
			path = calendar.getPath();
		}

		String response = this.report("rep_events.txt", path, 1);

		VEvent_XML_Parser parser = new VEvent_XML_Parser();
		List<ServerVEvent> result = parser.parseMultiVEvent(response);

		return result;
	}

	// Methods for deleting VCards

	/**
		This method provides deletion VCard object from contacts.
		@param card ServerVCard object to delete
		@return true if deletion successful
	 */
	public boolean deleteVCard(ServerVCard card) throws ClientProtocolException, URISyntaxException, IOException {
		return this.delete(card.getPath(), card.geteTag());
	}

	// Methods for deleting VEvents

	/**
		This method provides deletion VEvent object from contacts.
		@param event ServerVEvent object to delete
		@return true if deletion successful
	 */
	public boolean deleteVEvent(ServerVEvent event) throws ClientProtocolException, URISyntaxException, IOException {
		return this.delete(event.getPath(), event.geteTag());
	}

	// Methods for deleting 

	/**
		This method provides deletion VCard with entered etag from contacts.
		@param path destination path for deleting
		@param card ServerVCard object to delete
		@return true if deletion successful
	 */
	private boolean delete(String path, String etag) throws URISyntaxException, ClientProtocolException, IOException{
		DeleteRequest del = new DeleteRequest(initUri(path), etag);
		HttpResponse resp = client.execute(del);
		EntityUtils.consume(resp.getEntity());
		if(resp.getStatusLine().getStatusCode() == CODE_NO_CONTENT) return true;
		else return false;
	}

	// Methods for updating VCards

	/**
		This method provides updating entered VCard,
		@param card target ServerVCard object 
		@return true if update was successful
	 */
	public boolean updateVCard(ServerVCard card) throws URISyntaxException, ClientProtocolException, IOException{
		VCardWriter wr = new VCardWriter();
		wr.setVCard(card.getVcard());

		StringEntity se = new StringEntity(wr.buildVCardString());
		se.setContentType(TYPE_VCARD);
		return this.updateVCard(se, card.geteTag(), card.getPath());
	}

	// Methods for updating VEvents

	/**
		This method provides updating entered VCard,
		@param card target ServerVCard object 
		@return true if update was successful
	 */
	public boolean updateVEvent(ServerVEvent event) throws URISyntaxException, ClientProtocolException, IOException{
		// vEvent must be enclosed in Calendar, otherwise is not added
		Calendar calendarForEvent = new Calendar();
		calendarForEvent.getComponents().add(event.getVevent());

		StringEntity se = new StringEntity(calendarForEvent.toString());
		se.setContentType(TYPE_VEVENT);

		return this.updateVCard(se, event.geteTag(), event.getPath());
	}

	// Methods for updating

	/**
		This method provides updating VCard described by etag and destination path.
		@param entity entity with updated VCard
		@param etag VCard etag
		@param path destination path for update
		@return true if update was successful
	 */
	private boolean updateVCard(HttpEntity entity, String etag, String path) throws URISyntaxException, ClientProtocolException, IOException{
		UpdateRequest updateReq = new UpdateRequest(initUri(path), etag);
		updateReq.setEntity(entity);
		HttpResponse resp = client.execute(updateReq);

		EntityUtils.consume(resp.getEntity());
		if(resp.getStatusLine().getStatusCode() == CODE_OK) return true;
		else return false;
	}


	// Shutdown method

	/**
		This method provides termination of the connection and release all resources.
	 */	
	public void shutdown() {
		client.getConnectionManager().shutdown();
	}

	// Private assisting methods, server communication

	/**
		This method provides sending report request to server.
		@param filename name of a file containing report request
		@param path request target path
		@param depth report depth
		@return server response
	 */
	private String report(String filename, String path, int depth) throws ClientProtocolException, IOException, URISyntaxException {
		ReportRequest req = new ReportRequest(initUri(path), depth);

		InputStream is = ClassLoader.getSystemResourceAsStream(filename);

		StringEntity se = new StringEntity(convertStreamToString(is));
		
		se.setContentType(TYPE_XML);
		req.setEntity(se);

		HttpResponse resp = client.execute(req);

		String response = "";
		response += EntityUtils.toString(resp.getEntity());

		EntityUtils.consume(resp.getEntity());

		return response;
	}

	/**
		This method provides sending propfind request to server.
		@param fileName name of a file containing propfinde request
		@param uriName propfind uri
		@return server response
	 */
	private String propfind(String fileName, String uriName) throws URISyntaxException, ClientProtocolException, IOException {

		PropfindRequest req = new PropfindRequest(initUri(uriName));
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		
		StringEntity se = new StringEntity(convertStreamToString(is));
		
		se.setContentType(TYPE_XML);
		req.setEntity(se);
		
		HttpResponse resp = client.execute(req);

		String response = "";
		response += EntityUtils.toString(resp.getEntity());
		
		EntityUtils.consume(resp.getEntity());

		return response;

	}

	// Private assisting methods, connection initialization

	private String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}	
	
	
	/**
		This method provides initializing connection. 
		If is installCert set to true - downloads server certificate and adds to truststore.
	 */
	private void init() throws InstallCertException, InitKeystoreException {
		try{
			KeyStore trustStore;			
			FileInputStream instream;
			
			if(installCert){
				trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				InstallCert.install(servername, port, keystorePass);
				instream = new FileInputStream(new File(defKeystoreLocation));
				trustStore.load(instream, keystorePass.toCharArray());
				instream.close();
			}else{
				trustStore = loadJavaDefaultTruststore();				
			}
			
			SSLSocketFactory socketFactory = new SSLSocketFactory(null, null, null, trustStore, null, null, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme(SCHEME_HTTPS, port, socketFactory);
			client.getConnectionManager().getSchemeRegistry().register(sch);		
		}
		catch(Exception e){
			throw new InitKeystoreException();
		}	
	}

	/**
	 * Provides Keystore object with integrated Java truststore.
	 * @return Java truststore
	 * @throws InitKeystoreException exception during keystore initialize
	 */
	private KeyStore loadJavaDefaultTruststore() throws InitKeystoreException{
		char SEP = File.separatorChar;
		File dir = new File(System.getProperty("java.home") + SEP
				+ "lib" + SEP + "security");
		File file = new File(dir, "jssecacerts");
		if (file.isFile() == false) {
			file = new File(dir, "cacerts");
		}

		try{
			InputStream in = new FileInputStream(file);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, DEF_KS_PASS.toCharArray());
			in.close();
			return ks;
		}
		catch(Exception e){
			throw new InitKeystoreException();			
		}
	}

	/**
		This method provides building final URI containing passed path in parameter and other properties of the connection.
		@param path uri path
		@return final URI
	 */
	private URI initUri(String path) throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setScheme(SCHEME_HTTPS).setHost(servername).setPath(path);

		if (username != null) {
			uriBuilder.setUserInfo(username, password);
		}

		return uriBuilder.build();
	}

}