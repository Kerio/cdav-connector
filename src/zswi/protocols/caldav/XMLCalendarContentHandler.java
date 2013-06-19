package zswi.protocols.caldav;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XMLCalendarContentHandler is a simple SAX handler responsible for parsing the responses.
 * Parses the entire XML String and returns List of ServerCalendars. 
 * 
 * @author Jindřich Pouba
 *
 */
public class XMLCalendarContentHandler extends DefaultHandler {

	private List<ServerCalendar> calendars = new ArrayList<ServerCalendar>();

	private StringBuilder sbOK = new StringBuilder();
	private StringBuilder sbPath = new StringBuilder();

	private boolean inMultistatus = false;
	private boolean inResponse = false;
	private boolean inHref = false;
	private boolean inPropstat = false;
	private boolean inStatus = false;
	private boolean inOKPropstat = false;
	private boolean inProp = false;

	private StringBuilder propertiesBuilder = new StringBuilder();
	private String ctag = null;
	private String calendarColor = null;
	private String calendarDescription = null;
	private String displayName = null;
	private int calendarOrder = -1;

	/**
	 * tepmporary variables, will store data until we know that propstat was OK or not If OK, will copy data into pemanent variables If not OK, data will be discarded
	 */
	String tempColor = null;
	String tempDesc = null;
	int tempOrder = -1;

	/**
	 * Returns List of the parsed ServerCalendars.
	 * 
	 * @return
	 */
	public List<ServerCalendar> getCalendars() {
		return calendars;
	}

	/**
	 * Method startElement sets boolean values according to the element read.
	 * 
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.toLowerCase().endsWith("multistatus")) {
			inMultistatus = true;
		}

		else if (qName.toLowerCase().endsWith("response")) {
			inResponse = true;
			calendarColor = null;
			calendarDescription = null;
			calendarOrder = -1;
		}

		else if (qName.toLowerCase().endsWith("propstat")) {
			inOKPropstat = false;
			inPropstat = true;
		}

		else if (qName.toLowerCase().endsWith("status")) {
			inStatus = true;
			sbOK.setLength(0);
		}

		else if (qName.toLowerCase().endsWith("prop")) {
			inProp = true;
		}

		else if (qName.toLowerCase().endsWith("href")) {
			inHref = true;
			sbPath.setLength(0);
		}

		else if (qName.toLowerCase().endsWith("calendar-color")) {
			propertiesBuilder.setLength(0);
		} else if (qName.toLowerCase().endsWith("displayname")) {
			propertiesBuilder.setLength(0);
		} else if (qName.toLowerCase().endsWith("calendar-description")) {
			propertiesBuilder.setLength(0);
		} else if (qName.toLowerCase().endsWith("calendar-order")) {
			propertiesBuilder.setLength(0);
		} else if (qName.toLowerCase().endsWith("getctag")) {
			propertiesBuilder.setLength(0);
		}

	}

	/**
	 * Method endElement set the boolean attributes according to the values read.
	 * At the end of "reponse" element this method checks if all the collection is a calendar - if it is its added to the list.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.toLowerCase().endsWith("multistatus")) {
			inMultistatus = false;
		}

		else if (qName.toLowerCase().endsWith("response")) {

			inResponse = false;

			// Collection is Calendar if it has calendar-color,
			// calendar-order
			// calendar-description
			// and ctag not set on null
			if ((calendarColor != null) && (calendarDescription != null) && (calendarOrder != -1) && (ctag != null)) {
				ServerCalendar c = new ServerCalendar(ctag, displayName, calendarColor, calendarOrder, sbPath.toString(), calendarDescription);
				calendars.add(c);
			}
		}

		else if (qName.toLowerCase().endsWith("propstat")) {

			inPropstat = false;

			if (inOKPropstat) {
				calendarColor = tempColor;
				calendarDescription = tempDesc;
				calendarOrder = tempOrder;
				tempColor = null;
				tempDesc = null;
				tempOrder = -1;
			} else {
				tempColor = null;
				tempDesc = null;
				tempOrder = -1;
			}
		}

		else if (qName.toLowerCase().endsWith("status")) {
			inStatus = false;
			if (sbOK.toString().contains("OK") || sbOK.toString().contains("200")) {
				inOKPropstat = true;
			}
		}

		else if (qName.toLowerCase().endsWith("prop")) {
			inProp = false;
		}

		else if (qName.toLowerCase().endsWith("href")) {
			inHref = false;
		}

		else if (qName.toLowerCase().endsWith("calendar-color")) {
			tempColor = propertiesBuilder.toString();
		} else if (qName.toLowerCase().endsWith("displayname")) {
			displayName = propertiesBuilder.toString();
		} else if (qName.toLowerCase().endsWith("calendar-description")) {
			tempDesc = propertiesBuilder.toString();
		} else if (qName.toLowerCase().endsWith("calendar-order")) {
			try {
				tempOrder = Integer.parseInt(propertiesBuilder.toString());
			} catch (Exception e) {
				tempOrder = -1;
			}
		} else if (qName.toLowerCase().endsWith("getctag")) {
			ctag = propertiesBuilder.toString();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (inMultistatus && inResponse && inHref) {
			sbPath.append(ch, start, length);
		} else if (inMultistatus && inResponse && inPropstat && inStatus) {
			sbOK.append(ch, start, length);
		}

		else if (inMultistatus && inResponse && inPropstat && inProp) {
			propertiesBuilder.append(ch, start, length);
		}
	}

}
