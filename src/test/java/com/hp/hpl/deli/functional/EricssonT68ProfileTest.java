package com.hp.hpl.deli.functional;

import org.junit.*;

import static org.junit.Assert.*;

public class EricssonT68ProfileTest extends UAProfProfileTest {
	// This test checks that you can load a UAProf reference profile from
	// the local filesystem using DELI.
	// Note it does not check *ALL* attributes in the profile, just a subset
	@Before
	public void setUp() {
		setUp("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
				"profiles/testprofiles/Ericsson_T68R1.rdf");
	}

	// Hardware Platform
	@Test
	public void testModel() throws Exception {
		super.testModel();
		assertTrue(profile.getAttribute("Model").contains("T68R1"));
	}

	@Test
	public void testTextInputCapable() throws Exception {
		super.testTextInputCapable();
		assertTrue(profile.getAttribute("TextInputCapable").contains("Yes"));
	}

	// Software platform
	@Test
	public void testCcppAcceptCharset() {
		// super.testCcppAcceptCharset();
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("US-ASCII"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-8859-1"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("UTF-8"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-10646-UCS-2"));
	}

	@Test
	public void testAcceptDownloadableSoftware() throws Exception {
		super.testAcceptDownloadableSoftware();
		assertTrue(profile.getAttribute("AcceptDownloadableSoftware").contains("No"));
	}

	// Network characteristics
	@Test
	public void testSupportedBearers() throws Exception {
		super.testSupportedBearers();

		// testAttribute("SupportedBearers", "NetworkCharacteristics", "Locked",
		// "Bag", "Literal", wapURI,
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
		assertTrue(profile.getAttribute("WapVersion").contains("1.2.1/June 2000"));
	}

	@Test
	public void testWmlVersion() throws Exception {
		super.testWmlVersion();
		assertTrue(profile.getAttribute("WmlVersion").contains("1.1"));
		assertTrue(profile.getAttribute("WmlVersion").contains("1.2.1/June 2000"));
	}

	// Push characteristics
	@Test
	public void testWapPushMsgSize() throws Exception {
		super.testPushMsgSize();
		assertTrue(profile.getAttribute("Push-MsgSize").contains("3000"));
	}
}
