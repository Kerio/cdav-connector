package zswi.protocols.carddav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.vcard.VCard;

public class VCard_XML_Parser {

	VCardEngine vcardEngine = null;

	public VCard_XML_Parser() {
		this.vcardEngine =  new VCardEngine();
	}

	public List<ServerVCard> parseMultiVCard(String vcardXML) throws IOException {
		List<ServerVCard> parsedVcards = new ArrayList<ServerVCard>();
		String[] lines = vcardXML.split("\\r?\\n");
		StringBuilder currentVCard = new StringBuilder();
		String etag = null;
		String path = null;
		
		for (String line : lines) {
			// parse from xml etag code
			if (line.contains("getetag")) {		
				etag = parseEtag(line);
				// System.out.println("etag "+ etag);
			}
			if (line.contains("href")) {		
				path = parsePath(line);
			
			}
			if (line.endsWith("BEGIN:VCARD")) {
				// empty
				currentVCard.setLength(0);
				line = "BEGIN:VCARD";
			}

			// expand multilinne string
			currentVCard.append(line).append("\n");

			if (line.startsWith("END:VCARD")) {

				// System.out.println(currentVCard.toString());
				VCard vcard = vcardEngine.parse(currentVCard.toString());
				parsedVcards.add(new ServerVCard(vcard, etag, path));
				etag= null;
			}
		}

		return parsedVcards;
	}
	
	public String parsePath(String line){
		String pathLine[] = line.split("</href>");// path code is before </href> therefore pathLine[0]
		return  pathLine[0].split("<href>")[1]; // path code is behind <href> therefore [1]
		
		
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
	 */
	public List<ServerVCard> parseMultiVCard(File file) throws IOException {
		String line = null;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		while((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
	
		return parseMultiVCard(sb.toString());
	}


}
