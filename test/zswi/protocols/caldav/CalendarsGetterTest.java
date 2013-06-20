package zswi.protocols.caldav;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * 
 * @author Jindřich Pouba
 *
 */
public class CalendarsGetterTest {

	private static final String FILE_OK_4 = "calendars_ok_4.xml";
	private static final String FILE_NO_DESC_1 = "tasks_no_desc.xml";
	private static final String FILE_NO_ATTS_1 = "tasks_only_name.xml";
	private static final String FILE_EMPTY_DESC_1 = "tasks_empty_desc.xml";
	private static final String FILE_EMPTY_FILE = "error_empty.xml";
	
	/**
	 * Basic test for basic data. Input is OK XML file with 4 Calendars.
	 * 
	 */
	@Test
	public void testGetCalendars_OK_4() throws IOException, ParserConfigurationException, SAXException {
		File input = new File(System.getProperty("user.dir")+File.separator+"test"+File.separator+"testedFiles"+File.separator+FILE_OK_4);
		
		// transfer file to String
		
		String entireFile = fileToString(input);
		
		// test itself
		List<ServerCalendar> list = CalendarsGetter.getCalendars(entireFile);
		
		assertEquals("Number of ServerCalendars", 4, list.size());
		
		assertEquals("First Calendar Display name", "Calendar", list.get(0).getDisplayName());
		assertEquals("Second Calendar Display name", "Ctvrty", list.get(1).getDisplayName());
		assertEquals("Third Calendar Display name", "Druhy", list.get(2).getDisplayName());
		assertEquals("Fourth Calendar Display name", "Treti", list.get(3).getDisplayName());
		
		assertEquals("First Calendar Color", "#FCA7B1FF", list.get(0).getColor());
		assertEquals("Second Calendar Color", "#F5BB1EFF", list.get(1).getColor());
		assertEquals("Third Calendar Color", "#8CE773FF", list.get(2).getColor());
		assertEquals("Fourth Calendar Color", "#DCA0FFFF", list.get(3).getColor());
		
		assertEquals("First Calendar Order", 1, list.get(0).getOrder());
		assertEquals("Second Calendar Order", 1, list.get(1).getOrder());
		assertEquals("Third Calendar Order", 1, list.get(2).getOrder());
		assertEquals("Fourth Calendar Order", 1, list.get(3).getOrder());
		
	}
	
	/**
	 * One response, without calendar-description
	 * Is not calendar
	 * 
	 */
	@Test
	public void testGetCalendars_NoDesc_1() throws IOException, ParserConfigurationException, SAXException {
		File input = new File(System.getProperty("user.dir")+File.separator+"test"+File.separator+"testedFiles"+File.separator+FILE_NO_DESC_1);
		
		// transfer file to String
		
		String entireFile = fileToString(input);
		
		// test itself
		List<ServerCalendar> list = CalendarsGetter.getCalendars(entireFile);
		
		assertEquals("Number of ServerCalendars", 0, list.size());
		
	}
	
	/**
	 * One response, only with display-name
	 * Is not calendar
	 * 
	 */
	@Test
	public void testGetCalendars_NoAtts_1() throws IOException, ParserConfigurationException, SAXException {
		File input = new File(System.getProperty("user.dir")+File.separator+"test"+File.separator+"testedFiles"+File.separator+FILE_NO_ATTS_1);
		
		// transfer file to String
		
		String entireFile = fileToString(input);
		
		// test itself
		List<ServerCalendar> list = CalendarsGetter.getCalendars(entireFile);
		
		assertEquals("Number of ServerCalendars", 0, list.size());
		
	}
	
	/**
	 * One response, with description enclosed
	 * Is a calendar
	 * 
	 */
	@Test
	public void testGetCalendars_EmptyDesc_1() throws IOException, ParserConfigurationException, SAXException {
		File input = new File(System.getProperty("user.dir")+File.separator+"test"+File.separator+"testedFiles"+File.separator+FILE_EMPTY_DESC_1);
		
		// transfer file to String
		
		String entireFile = fileToString(input);
		
		// test itself
		List<ServerCalendar> list = CalendarsGetter.getCalendars(entireFile);
		
		assertEquals("Number of ServerCalendars", 1, list.size());
		
		assertEquals("Calendar name", "Calendar", list.get(0).getDisplayName());
		assertEquals("Number of ServerCalendars", "#0252D4FF", list.get(0).getColor());
		assertEquals("Number of ServerCalendars", "", list.get(0).getDescription());
		
	}
	
	/**
	 * Empty file, should result in SAX Exception
	 */
	@Test(expected=SAXException.class)
	public void testGetCalendars_E_EmptyFile_0() throws IOException, ParserConfigurationException, SAXException {
		File input = new File(System.getProperty("user.dir")+File.separator+"test"+File.separator+"testedFiles"+File.separator+FILE_EMPTY_FILE);
		
		// transfer file to String
		
		String entireFile = fileToString(input);
		
		// test itself
		CalendarsGetter.getCalendars(entireFile);
		
	}
	
	private String fileToString(File file) throws IOException {
		BufferedReader bfr = new BufferedReader(new FileReader(file));
		String line = bfr.readLine();
		StringBuilder sb = new StringBuilder();
		while(line != null) {
			sb.append(line);
			line = bfr.readLine();
		}
		
		return sb.toString();
	}

}
