package zswi.protocols.carddav;

import static org.junit.Assert.*;

import java.io.File;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.cardme.io.BinaryFoldingScheme;
import net.sourceforge.cardme.io.CompatibilityMode;
import net.sourceforge.cardme.io.FoldingScheme;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardVersion;
import net.sourceforge.cardme.vcard.features.EmailFeature;
import net.sourceforge.cardme.vcard.features.FormattedNameFeature;
import net.sourceforge.cardme.vcard.features.NameFeature;
import net.sourceforge.cardme.vcard.features.TelephoneFeature;

import net.sourceforge.cardme.vcard.types.parameters.EmailParameterType;

import org.junit.Test;

import zswi.protocols.carddav.ServerVCard;
import zswi.protocols.carddav.VCard_XML_Parser;


/**
 * 
 * @author Marek Šimůnek
 *
 */
public class TestVcardXMLParser {

	@Test
	public void testParseOneVCard() throws IOException {

		String vcardString = "BEGIN:VCARD\n" + "VERSION:3.0\n" + "FN;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:Vaclav Veliky\n" + "N;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:Veliky Vaclav\n" + "ADR;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:;;;;;;\n" + "TEL;WORK:+420123456789\n" + "EMAIL;INTERNET:velikyvenca@kerio.cz\n" + "END:VCARD\n";

		VCard_XML_Parser vcardParser = new VCard_XML_Parser();
		List<ServerVCard> vcards = vcardParser.parseMultiVCard(vcardString);
		VCard vcard = vcards.get(0).getVcard();

		VCardWriter vcardWriter = new VCardWriter();
		vcardWriter.setOutputVersion(VCardVersion.V3_0);
		vcardWriter.setFoldingScheme(FoldingScheme.MIME_DIR);
		vcardWriter.setCompatibilityMode(CompatibilityMode.RFC2426);
		vcardWriter.setBinaryfoldingScheme(BinaryFoldingScheme.MIME_DIR);

		vcardWriter.setVCard(vcard);

		String vcardOutString = vcardWriter.buildVCardString();
		assertNotNull(vcardOutString);
		// test Formatted Name
		FormattedNameFeature fn = vcard.getFormattedName();
		assertEquals("Full Name ", "Vaclav Veliky", fn.getFormattedName());
		// test Name
		NameFeature n = vcard.getName();
		assertEquals("Name ", "Veliky Vaclav", n.getFamilyName());
		// test Tel
		Iterator<TelephoneFeature> it = vcard.getTelephoneNumbers();
		TelephoneFeature f = it.next();
		assertEquals("+420123456789", f.getTelephone());
		// test Email
		Iterator<EmailFeature> ite = vcard.getEmails();
		EmailFeature email = ite.next();
		assertEquals("Email ", "velikyvenca@kerio.cz", email.getEmail());
		List<EmailParameterType> types = email.getEmailParameterTypesList();
		assertEquals(1, types.size());
		assertTrue(types.contains(EmailParameterType.INTERNET));

	}

	@Test
	public void testParseXMLMultiVCard() throws IOException {
		File xmlVcard = new File(System.getProperty("user.dir") + File.separator + "test" + File.separator + "testedFiles" + File.separator + "vcard_xml_report.txt");

		VCard_XML_Parser vcardParser = new VCard_XML_Parser();
		List<ServerVCard> vcards = vcardParser.parseMultiVCard(xmlVcard);

		assertEquals("Count of vcards ", 5, vcards.size());

		for (int i = 0; i < vcards.size(); i++) {
			FormattedNameFeature fn = vcards.get(i).getVcard().getFormattedName();

			// first vcard
			if (i == 0) {
				assertEquals("1.Full Name ", "Adam Vesely", fn.getFormattedName());
				assertEquals("1.etag ", "b3a0b06aec23456aa0e0877dba95a1b90000000100000001", vcards.get(i).geteTag());
			} else if (i == 1) {
				assertEquals("2.Full Name ", "George Smith", fn.getFormattedName());
				assertEquals("2.Full Name ", "b3a0b06aec23456aa0e0877dba95a1b90000000700000000", vcards.get(i).geteTag());

			} else if (i == 2) {
				assertEquals("3.Full Name ", "Pepa Zdepa", fn.getFormattedName());
				assertEquals("3.Full Name ", "b3a0b06aec23456aa0e0877dba95a1b90000000800000000", vcards.get(i).geteTag());

			} else if (i == 3) {
				assertEquals("4.Full Name ", "Hektor Čávéz", fn.getFormattedName());
				assertEquals("4.Full Name ", "b3a0b06aec23456aa0e0877dba95a1b90000000900000000", vcards.get(i).geteTag());

			} else if (i == 4) {
				assertEquals("5.Full Name ", "Řehoř Skywalker", fn.getFormattedName());
				assertEquals("5.Full Name ", "b3a0b06aec23456aa0e0877dba95a1b90000000a00000000", vcards.get(i).geteTag());

			}
		}
	}

	@Test
	public void testParseEtag() {
		String xml_report = "<propstat><status>HTTP/1.1 200 OK</status><prop>" + "<getetag xmlns=\"DAV:\">b3a0b06aec23456aa0e0877dba95a1b90000000</getetag>" + "<address-data xmlns=\"urn:ietf:params:xml:ns:carddav\"><![CDATA[BEGIN:VCARD";

		VCard_XML_Parser vcardParser = new VCard_XML_Parser();

		assertEquals("Simple etag", "b3a0b06aec23456aa0e0877dba95a1b90000000", vcardParser.parseEtag(xml_report));

		String xml_report_etagWithNamespace = "<propstat><status>HTTP/1.1 200 OK</status><prop>" + "<getetag xmlns=\"DAV:folder:new\">b3a0b06aec23456aa0e0877dba95a1b90000000</DAV:folder:new:getetag>" + "<address-data xmlns=\"urn:ietf:params:xml:ns:carddav\"><![CDATA[BEGIN:VCARD";

		assertEquals("etag with namespace", "b3a0b06aec23456aa0e0877dba95a1b90000000", vcardParser.parseEtag(xml_report_etagWithNamespace));
		/*
		 * String xml_report_etagWithNamespace2 = "<propstat><status>HTTP/1.1 200 OK</status><prop>" + "<getetag xmlns=\"DAV:folder:new\">b3a0b06aec23456aa0e0877dba95a1b90000000</DAV:folder:new:getetag>" + "<address-data xmlns=\"urn:ietf:params:xml:ns:carddav\"><![CDATA[BEGIN:VCARD";
		 * 
		 * assertEquals("etag with namespace","b3a0b06aec23456aa0e0877dba95a1b90000000", vcardParser.parseEtag(xml_report_etagWithNamespace2));
		 */

	}
}
