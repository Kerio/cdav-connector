package zswi.protocols.caldav;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * CalendarGetter class represents parser for server responses, 
 * which can select only calendar collections from the entire collection set.
 * 
 * @author Jindřich Pouba
 *
 */
public class CalendarsGetter {
	
	/**
	 * Method getCalendars parses the XML response, which should contain all collections returned by PROPFIND request (see propfind_calendars.txt).
	 * Than is selects only the collections, which appear to be a Calendar (have their calendar-order, calendar-name and calendar-color set not to null).
	 * These collections are returned as ServerCalendar objects in a single List. 
	 * 
	 * @param XMLResponse String reponse to a PROPFIND request
	 * @return list of ServerCalendars
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<ServerCalendar> getCalendars(String XMLResponse) throws ParserConfigurationException, SAXException, IOException {
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(false);
		SAXParser sp = spf.newSAXParser(); 
		XMLReader parser = sp.getXMLReader();
		
		XMLCalendarContentHandler handler = new XMLCalendarContentHandler();
		parser.setContentHandler(handler);
		
		StringReader reader = new StringReader(XMLResponse);
		InputSource source = new InputSource(reader);
		
		parser.parse(source);
		
		return handler.getCalendars();
	}
	
}
