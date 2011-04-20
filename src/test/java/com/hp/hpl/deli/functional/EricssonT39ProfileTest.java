package com.hp.hpl.deli.functional;

import org.junit.*;

import static org.junit.Assert.assertTrue;

public class EricssonT39ProfileTest extends UAProfProfileTest {

	@Before
	public void setup() {
		setUp("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
				"profiles/testprofiles/Ericsson_T39.rdf");
	}

	// Hardware Platform
	@Test
	public void testBitsPerPixel() throws Exception {
		super.testBitsPerPixel();
		assertTrue(profile.getAttribute("BitsPerPixel").contains("2"));
	}

	@Test
	public void testColorCapable() throws Exception {
		super.testColorCapable();
		assertTrue(profile.getAttribute("ColorCapable").contains("No"));
	}

	// Software platform
	@Test
	public void testCcppAcceptCharset() {
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("US-ASCII"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-8859-1"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("UTF-8"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-10646-UCS-2"));
	}

	@Test
	public void testCcppAcceptEncoding() {
		assertTrue(profile.getAttribute("CcppAccept-Encoding").contains("base64"));
	}

	// Network characteristics
	@Test
	public void testSupportedBearers() throws Exception {
		super.testSupportedBearers();
		assertTrue(profile.getAttribute("SupportedBearers").contains("TwoWaySMS"));
		assertTrue(profile.getAttribute("SupportedBearers").contains("CSD"));
		assertTrue(profile.getAttribute("SupportedBearers").contains("GPRS"));
	}

	// BrowserUA
	@Test
	public void testFramesCapable() throws Exception {
		super.testFramesCapable();
		assertTrue(profile.getAttribute("FramesCapable").contains("No"));
	}

	@Test
	public void testTablesCapable() throws Exception {
		super.testTablesCapable();
		assertTrue(profile.getAttribute("TablesCapable").contains("Yes"));
	}

	// Wap Characteristics
	@Test
	public void testWapVersion() throws Exception {
		super.testWapVersion();
		assertTrue(profile.getAttribute("WapVersion").contains("1.2.1"));
	}

	@Test
	public void testWmlVersion() throws Exception {
		super.testWmlVersion();
		assertTrue(profile.getAttribute("WmlVersion").contains("1.1"));
		assertTrue(profile.getAttribute("WmlVersion").contains("1.2"));
		assertTrue(profile.getAttribute("WmlVersion").contains("1.3"));
	}

	// Push characteristics
	@Test
	public void testWapPushMsgSize() throws Exception {
		super.testPushMsgSize();
		assertTrue(profile.getAttribute("Push-MsgSize").contains("3000"));
	}
}
