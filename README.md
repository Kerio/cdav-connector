# Simple CalDAV/CardDAV Connector for Kerio Connect

A simple Java library for CalDAV and CardDAV protocols for Kerio Connect server.

The application was developed by students of the [ZČU Plzeň](http://www.zcu.cz) (University of West Bohemia in Pilsen) as a seminar project under the Fundamentals of Software Engineering.

## Authors

* Jan Ambrož \<ambi777@seznam.cz\>
* Tomáš Balíček \<balicekt@gmail.com\>
* Tomáš Krásný \<krasnytt@gmail.com\>
* Jindřich Pouba \<pouba@students.zcu.cz\>
* Marek Šimůnek

## General information

cdav-connector library is a Java library made for Java developers, who need their software to communicate with CardDAV and/or CalDAV servers. Software will be mainly used as a tool for client applications. This tool should enable easy data exchange with server using described protocols. 

Network communication follow this specifications:

* CardDAV protocol specifications - RFC 6352 <http://tools.ietf.org/html/rfc6352>
* CalDAV protocol specifications - RFC 4791 <http://tools.ietf.org/html/rfc4791>
* Calendar event format specifications - RFC 5545 <http://tools.ietf.org/html/rfc5545>
* vCard format specification - RFC 2426 <http://tools.ietf.org/html/rfc2426>

This library is targeted to cooperate with Kerio Connect server and was tested with (and only with) this server. Proper functionality while using the library on other server applications is not tested nor guaranteed. 

Library is developed as free software under Apache License 2.0. See section License.

## Third party libraries

* Apache HTTP Client 4.5.2: <http://hc.apache.org/httpclient-3.x/>
* Card Me 0.4.0: <http://sourceforge.net/projects/cardme/>
* CalDAV4j 0.7: <https://code.google.com/p/caldav4j/>
* iCal4j 1.0.4: <http://sourceforge.net/projects/ical4j/>
    
## HTTPS Secure Layer

cdav-connector library is using secure communication over HTTPS - Hypertext Transfer Protocol Secure. It means, transferred data are bidirectional encrypted by SSL/TLS protocols. Before the transfer itself each side sends to other side its public key. After this there must be a way to decide if we can trust this key. There are usually two different scenarios for verification.

1. There exist something like transferred trust. Certification authority (CA) provides electronic signs for public keys. If we receive such a public key with electronic sign from trustworthy CA, we can believe it is itself trustworthy too.
So this is first scenario. We receive foreign public key and it is electronic signed with trustworthy CA. How does our system recognize that right this CA is trustworthy? We have already somewhere this information stored in our system. It is truststore - place for keys identifiers.

2. Now we can suppose we have received unsigned public key. There are more possible ways.
    * We can override something called Trustmanager and we can synthetically force our library to trust everything.
    * We can try handshaking with server and if it fails, we can download certificate with its public key and add to our truststore. We suppose we know what are we doing and connection to specific server is safe.

## Using HTTPS in cdav-connector

There two possible ways how to realize https connection over cdav-connector.

1. Targeted server does not have signed certificate or you have not any details about it.
You can force the library to download certificate from the targeted server. Copy of the Java integrated truststore is made with default location same as the library and certificate is added with default name `jssacacerts`. Then connection object considers targeted server as trusted.

2. You have added certificate in Java truststore on your own or it is already there.
Adding certificate can be done over `keytool` in command line like this:

`C:\*JAVA_JRE*\bin\keytool.exe -import -keystore "C:\*JAVA_JRE*\lib\security\cacerts" -file "C:\certnew.cer"`

**Comments**

* `"C:\certnew.cer"` - please replace this with filename of you downloaded certificate
* JAVA_JRE represents path to your Java JRE for example: `"Program Files\Java\jre7"`
* Please use quotes in command line for paths. Otherwise space in path can cause problems.

Library just use Java integrated truststore and realize the connection. 

## License

Copyright 2013 Jan Ambrož, Tomáš Balíček, Tomáš Krásný, Jindřich Pouba, Marek Šimůnek

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Usage and examples

* Creating a server connection
* Creating a new contact
* Creating a new event
* Upload of the card to the server
* Getting list of Calendars from the server
* Upload of the event to the server
* Getting cards from the server
* Update to delete of the card on the server
* Update to delete of the event on the server

## Creating a server connection

For connection to the server, user must provide the application with name or address of the server, user name and password. Connection itself realizes an instance of the `HTTPSConnection` class. Example of basic constructor usage, with server `mail.company.tld`, user name `admin` and password `123456`:
	
	HTTPSConnection connection = new HTTPSConnection("mail.company.tld", "admin", "123456");

This will create a server connection. For secure communication is used HTTPS. If the server SSL certificate is not already installed in the client TrustStore or is not signed by an certification authority, it is created and saved. To disable this function (i.e. to not create an connection, if the certificate is not trusted), user can call the more advanced constructor:
	
	HTTPSConnection connection = new HTTPSConnection("mail.company.tld", "admin", "123456",  433, false);

where the last attribute means **do not create the connection, if the certificate is not trusted already**. The attribute `433` specifies port for the connection. Default for HTTPS and for our library is 443, but with this advanced constructors, user can specify his own.
Default value for downloading certificates is `true`, so calling 
	
	HTTPSConnection connection = new HTTPSConnection("mail.company.tld", "admin", "123456", 433, true);

has the same meaning as:
	
	HTTPSConnection connection = new HTTPSConnection("mail.company.tld", "admin", "123456");

### Terminating server connection

For termination of connection user must call the function `shutdown()` on top of instance of that connection, that will get terminated. For example, to terminate the connection used in previous example:
	
	connection.shutdown();

It's highly recommended to terminate every created connection.

## Creating a new contact

In the application card object is represented as instance of `VCard` interface. There are two ways how to create a new vCard object – creating it from scratch or parsing a file. 

### Creating vCard from scratch

Simple way to create a new `vCard` is making an empty new instance of `VCardImpl` class and than setting attributes with the desired information.

**Example of creating a new vCard with a name John Smith:**

	VCard card = new VCardImpl();
	card.setFormattedName(new FormattedNameType("John Smith"));
	card.setName(new NameType("Smith", "John"));

Attribute `FormattedName` is required for a valid vCard, but doesn't have to be shown anywhere (for example Kerio Connect is showing only the Name attribute). To this newly created vCard we can assign additional information using setters for attributes or adding attributes to collections using add method. Majority of the attributes uses its own types such as `TelephoneType`, `BirthdayType` and so on. These types often have constructors enabling user to convert from traditional java types. 

**Adding telephone number:**

	card.addTelephoneNumber(new TelephoneType("+420 123 456 789", TelephoneParameterType.HOME));

**Adding birthday:**

	java.util.Calendar birthday = java.util.Calendar.getInstance();
	birthday.set(java.util.Calendar.YEAR, 2000);
	birthday.set(java.util.Calendar.MONTH, 1);
	birthday.set(java.util.Calendar.DAY_OF_MONTH, 1);
	card.setBirthday(new BirthdayType(birthday));

**Adding note:**

	card.addNote(new NoteType("Some note"));


**Adding email:**

	card.addEmail(new EmailType("email@company.tld"));

**Complex example of creating vCard object:**

	VCard vcard = new VCardImpl();
	vcard.setFormattedName(new FormattedNameType("John Smith"));
	vcard.setName(new NameType("Smith", "John"));
	vcard.addTelephoneNumber(new TelephoneType("+420 123 456 789", TelephoneParameterType.CELL));
		
	java.util.Calendar birthday = java.util.Calendar.getInstance();
	birthday.set(java.util.Calendar.YEAR, 1950);
	birthday.set(java.util.Calendar.MONTH, 10);
	birthday.set(java.util.Calendar.DAY_OF_MONTH, 5);
	vcard.setBirthday(new BirthdayType(birthday));
		
	vcard.addNote(new NoteType("My friend from high school"));
	vcard.addEmail(new EmailType("example@company.tld"));

### Parsing vCard from file

We create object of class VCardEngine:

	VCardEngine vcardEngine= new VcardEngine();

We set compatibility mode of folding scheme

	vcardEngine.setCompatibilityMode(CompatibilityMode.RFC2426);

method parse in class `VCardEngine` requires as a parameter object File, which we create from our business card *.vcf file, where `fileName` is location and name of the file:

	File vcardFile = new File(fileName);

and finally we create object of VCard:

	VCard parsed = vcardEngine.parse(vcardFile);		

If at location specified by fileName is a valid vcf `vCard` file, its returned as `VCard` object. On top of this object user can make the same changes as described in previous vCard examples.

For more information visit CardMe java library documentation at <http://sourceforge.net/projects/cardme/>

## Creating a new event

In the application the event is represented as `VEvent` object. Every event should have starting time, ending time and summary of the activity. 

Starting and ending time should be created as instances of `java.util.Calendar` object. Because of conflicting names between the `ical4j` and `java.util` libraries, all `java.util` classes will be written with full package name.

**Example of creation starting and ending time for a new event "action", starting 1. 5. 2013 and ending at 2. 5. 2013:**

	java.util.Calendar start = java.util.Calendar.getInstance();
	start.set(java.util.Calendar.YEAR, 2013);
	start.set(java.util.Calendar.HOUR_OF_DAY, 15);
	start.set(java.util.Calendar.MONTH, java.util.Calendar.MAY);
	start.set(java.util.Calendar.DAY_OF_MONTH, 1);
		
	java.util.Calendar end = java.util.Calendar.getInstance();
	end.set(java.util.Calendar.YEAR, 2013);
	end.set(java.util.Calendar.HOUR_OF_DAY, 15);
	end.set(java.util.Calendar.MONTH, java.util.Calendar.MAY);
	end.set(java.util.Calendar.DAY_OF_MONTH, 2);

All the attributes user haven't specified (in this case minutes and seconds) are taken from the exact date and the time of creation the `java.util.Calendar` object.

**Now creation of VEvent object:**

	VEvent event = new VEvent(new Date(start.getTime()), new Date(end.getTime()), "action");

`VEvent` can be constructed from two `Date` objects and a `String` summary. Date objects are constructed from `java.util.Calendar getTime()` method. Date objects represent only dates, not time. If user would want to specify date and time of event start and end, it's required to use `DateTime` class.

**Example of event starting 10.1.2013 14:15 and ending 12.1.2013 10:45:**

	java.util.Calendar start = java.util.Calendar.getInstance();
	start.set(java.util.Calendar.YEAR, 2013);
	start.set(java.util.Calendar.HOUR_OF_DAY, 14);
	start.set(java.util.Calendar.MINUTE, 15);
	start.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
	start.set(java.util.Calendar.DAY_OF_MONTH, 10);
		
	java.util.Calendar end = java.util.Calendar.getInstance();
	end.set(java.util.Calendar.YEAR, 2013);
	end.set(java.util.Calendar.HOUR_OF_DAY, 10);
	end.set(java.util.Calendar.MINUTE, 45);
	end.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
	end.set(java.util.Calendar.DAY_OF_MONTH, 12);
		
	VEvent event = new VEvent(new DateTime(start.getTime()), new DateTime(end.getTime()), "action");


**Note:** While making and setting a new `java.util.Calendar` object there can be some problems with timezones or daylight saving time. In some cases (usually with times around midnight) the date can be wrongly interpreted. It's recommended to always set all attributes (hours, minutes and seconds) even when creating only date-based events.

### Adding additional information to events

Additional properties can be added to the event by creating their instances as objects listed in `net.fortuna.ical4j.model.property` package and by using the method `getPoperties().add()`. Example of adding location **Workplace** to the event:

	Location loc = new Location("Workplace");
	event.getProperties().add(loc);

## Uploading a card to the server

For upload a card object to the server is there method `addVCard` on top of `HTTPSConnection` object. First parameter is either `VCard` or a `File`. In both cases the supplied object gets converted into http request and sent to the server. If server responses with OK code, method returns true, otherwise false.

**Example:**

	connection.addVCard(vcard);
	connection.addVCard(new File("path/vcard.ext"));

**More complex example of uploading vCard of "John Smith" on "mail.company.tld" server under user name "admin" and password "123456":**

	HTTPSConnection con = new HTTPSConnection("mail.company.tld", "admin", "123456");

	VCard vcard = new VCardImpl();
	vcard.setFormattedName(new FormattedNameType("John Smith"));
	vcard.setName(new NameType("Smith", "John"));
	
	con.addVCard(vcard);

	con.shutdown();


## Getting list of Calendars from the server

As opposed to `vCards`, event may be saved under different Calendars. So for saving a newly created event, we need to know these Calendars. For getting the list of them, there is method `getCalendars()` of the connection. It returns an `ArrayList` of `ServerCalendar` objects. `ServerCalendar` object represents a Calendar on the server. Calendar is represented by its name, color and order.

**Most important methods for ServerCalendar:**

	c.getDisplayName();
	c.getColor();
	c.getDescription();
	c.getOrder();

Everyone of this methods return String with desired information, with the exception of Order, which returns integer value.

**Example of getting all Calendars from the server and printing information about DisplayName and Color to the output:**

	List<ServerCalendar> calendars = con.getCalendars();
		
	System.out.println(calendars.size());
	for(ServerCalendar c: calendars) {
		System.out.println(c.getDisplayName() + " " + c.getColor());
	}


### Uploading an event to the server

Upload of the event to the server is very similar to uploading a card. Calling a `addVEvent` method of `HTTPSConnection` object with a parameter either a `VEvent` object or a `File`. 

**Example:**

	connection.addVEvent(vevent);
	connection.addVEvent(new File("path/event"));

Opposed to the `vCard`, events can be uploaded on different Calendars. Calling this method without Calendar specification (i.e. example shown top) adds the event to the default Calendar (with name **Calendar**). Upload to different Calendar is done by adding a second parameter to the method `addVEvent`. This second parameter specifies targeted Calendar as `ServerCalendar` object. 

**Example:**

	List<ServerCalendar> calendars = con.getCalendars();
	
	// selecting some ServerCalendar, based on Name, Color etc.
	int selectedIndex = /* some index */;

	connection.addVEvent(vevent, calendars.get(selectedIndex));
	
**Complex example of creating and adding the same event to all ServerCalendars:**

	HTTPSConnection con = new HTTPSConnection("mail.company.tld", "admin", "123456");

	java.util.Calendar start = java.util.Calendar.getInstance();
	start.set(java.util.Calendar.YEAR, 2013);
	start.set(java.util.Calendar.HOUR_OF_DAY, 14);
	start.set(java.util.Calendar.MINUTE, 15);
	start.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
	start.set(java.util.Calendar.DAY_OF_MONTH, 10);
		
	java.util.Calendar end = java.util.Calendar.getInstance();
	end.set(java.util.Calendar.YEAR, 2013);
	end.set(java.util.Calendar.HOUR_OF_DAY, 10);
	end.set(java.util.Calendar.MINUTE, 45);
	end.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
	end.set(java.util.Calendar.DAY_OF_MONTH, 12);
		
	VEvent event = new VEvent(new Date(start.getTime()), new Date(end.getTime()), "Meeting");

	List<ServerCalendar> calendars = con.getCalendars();

	for(ServerCalendar c: calendars) {
		con.addVEvent(event, c);
	}

	con.shutdown();

## Getting cards from the server

Cards downloaded from the server are saved as `ServerVCard` objects. In addition to normal `VCard` objects they have attributes as etag and path. Most important method for updating cards is method `getVcard()` which returns a standard `vCard` object. These attributes are not important for the users but needed for updating or deleting cards on server. You can get all the cards as `ArrayList` by calling method `getVCards`.

**Example for getting all cards on server:**

	List<ServerVCard> list = con.getVCards();

## Update or delete of the card on the server

To update some card on server is designed method `updateVCard`. As a parameter it takes `ServerVcard` object. 

**Example of update the first vcard to the name John Smith:**

	List<ServerVCard> list = con.getVCards();
		
	list.get(0).getVcard().setName(new NameType("Smith", "John"));
	con.updateVCard(list.get(0));

**Complex example of adding a phone number to the John Smith card on the server:**

	HTTPSConnection con = new HTTPSConnection("mail.company.tld", "admin", "123456");	

	List<ServerVCard> list = con.getVCards();
		
	ServerVCard card;
	for(ServerVCard c: cardList) {
		if(c.getVcard().getName().getFamilyName().equals("Smith") &&
				c.getVcard().getName().getGivenName().equals("John")) {
			card = c;
			break;
		}
	}
	
	card.addTelephoneNumber(new TelephoneType("+420 123 456 789", TelephoneParameterType.HOME));

	con.updateVCard(card);

	con.shutdown();

To delete the card use method `deleteVCard` instead of `updateVCard` with the same parameter.

**Example of deletion one card:**

	List<ServerVCard> list = con.getVCards();
		
	// selection of the right ServerVCard
	int someIndex = /* some inedex */;

	con.deleteVCard(list.get(someIndex));

**Example of deletion of all cards on the server:**

	List<ServerVCard> cardList = con.getVCards();
		
	for(ServerVCard c: cardList) {
		con.deleteVCard(c);
	}

## Update or delete of the event on the server

To update some card on server is designed method `updateVEvent`. As a parameter it takes `ServerVEvent` object. You don't need to specify the `ServerCalendar`, method simply uses the one that `ServerVEvent` was downloaded from.

**Example of update the first vEvent to the date the 1st of December 2013:**

	List<ServerVEvent> list = con.getVEvents();

	java.util.Calendar start = java.util.Calendar.getInstance();
	start.set(java.util.Calendar.YEAR, 2013);
	start.set(java.util.Calendar.HOUR_OF_DAY, 15);
	start.set(java.util.Calendar.MONTH, java.util.Calendar.MAY);
	start.set(java.util.Calendar.DAY_OF_MONTH, 10);
		
	list.get(0).getVevent().getStartDate().setDate(new DateTime(start.getTime()));
		
	con.updateVEvent(list.get(0));

To delete the card use method `deleteVEvent` instead of `updateVEvent` with the same parameter.

**Example of deletion one card:**

	List<ServerVEvent> list = con.getVEvents();
		
	// selection of the right ServerVEvent
	int someIndex = /* some index */;

	con.deleteVEvent(list.get(someIndex));

**Example of deletion of all cards on selected Calendar on the server:**

	List<ServerCalendar> calendars = con.getCalendars();
		
	int calIndex = /* someCalendar index */;
		
	List<ServerVEvent> list = con.getVEvents(calendars.get(calIndex));

	for (ServerVEvent ev : list) {
		con.deleteVEvent(ev);
	}


## Licenses of third parties

### Apache HttpClient

Copyright 2013 Jindřich Pouba

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

### Cardme

    Copyright (C) 1989, 1991 Free Software Foundation, Inc.  
    51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
    
    Everyone is permitted to copy and distribute verbatim copies
    of this license document, but changing it is not allowed.

### iCal4j

    Copyright (c) 2012, Ben Fortuna
    All rights reserved.
    
    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:
    
    	o Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
    
    	o Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
    
    	o Neither the name of Ben Fortuna nor the names of any other contributors
    may be used to endorse or promote products derived from this software
    without specific prior written permission.
    
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
    CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.