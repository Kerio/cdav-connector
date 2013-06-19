package zswi.protocols.caldav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

public class VEvent_XML_Parser {


	public List<ServerVEvent> parseMultiVEvent(String veventXML) throws IOException, ParserException {
		
		List<ServerVEvent> parsedVevents = new ArrayList<ServerVEvent>();  
		//String[] lines = veventXML.split("\\r?\\n");
		String[] lines = veventXML.split("\r");
		StringBuilder currentVEvent = new StringBuilder();
		String etag = null;
		String path = null;
		
		for (String line : lines) {
			// parse from xml etag code
			if (line.contains("getetag")) {		
				etag = parseEtag(line);
				 //System.out.println("**etag**: "+ etag);
			}
			if (line.contains("href")) {		
				path = parsePath(line);
				//System.out.println("**path**: "+ path);
			}
			if (line.endsWith("BEGIN:VEVENT")) {
				// empty
				currentVEvent.setLength(0);
				line = "BEGIN:VCALENDAR\nBEGIN:VEVENT";
			}

			// expand multilinne string
			currentVEvent.append(line).append("\n");

			if (line.startsWith("END:VEVENT")) {
				
				currentVEvent.append("END:VCALENDAR");
				//System.out.println("**vevent**: "+currentVEvent.toString());
				CalendarBuilder builder = new CalendarBuilder();
				Calendar cal = builder.build(new StringReader(currentVEvent.toString()));
				VEvent vevent = (VEvent)cal.getComponents().get(0);
				parsedVevents.add(new ServerVEvent(vevent, etag, path));
				etag= null;
			}
		}
		return parsedVevents;
	}
	
	public String parsePath(String line){
		String pathLine[] = line.split("</a:href>");// path code is before </href> therefore pathLine[0]
		return  pathLine[0].split("<a:href>")[1]; // path code is behind <href> therefore [1]
		
		
	}
	
	public String parseEtag(String line){
		String etagLine[] = line.split("getetag");
		String etag = null;
		if(etagLine.length==3){
			int middle =1; 
			String etagWithGreaterThanSign[] = etagLine[middle].split(">");
			String etagWithoutGreaterThanSign[] = etagWithGreaterThanSign[middle].split("</");
			etag = etagWithoutGreaterThanSign[0];
			
		}else{
			System.out.println("error: more etags on one line");
		}
	
		return etag;
	}
	
	/**
	 * File to String
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws ParserException 
	 */
	public List<ServerVEvent> parseMultiVEvent(File file) throws IOException, ParserException {
		String line = null;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		while((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
	
		return parseMultiVEvent(sb.toString());
	}
	



}
