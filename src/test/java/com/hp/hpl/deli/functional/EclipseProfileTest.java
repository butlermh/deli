package com.hp.hpl.deli.functional;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class EclipseProfileTest extends UAProfProfileTest {
	@Before
	public void setUp() {
        setUp("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#", "profiles/testprofiles/Trium_Eclipse.rdf");
	}

	// Hardware Platform
	@Test
	public void testModel()  throws Exception {
		super.testModel();
		assertTrue(profile.getAttribute("Model").contains("Eclipse"));
	}

	@Test
	public void testTextInputCapable()  throws Exception {
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
	}

	@Test
	public void testAcceptDownloadableSoftware()  throws Exception {
		super.testAcceptDownloadableSoftware();
		assertTrue(profile.getAttribute("AcceptDownloadableSoftware").contains("No"));
	}

	// Network characteristics
	@Test
	public void testSecuritySupport()  throws Exception {
		super.testSecuritySupport();
		assertTrue(profile.getAttribute("SecuritySupport").contains("WTLS"));
	}

	@Test
	public void testSupportedBearers()  throws Exception {
		super.testSupportedBearers();
		assertTrue(profile.getAttribute("SupportedBearers").contains("OneWaySMS"));
		assertTrue(profile.getAttribute("SupportedBearers").contains("CSD"));
		assertTrue(profile.getAttribute("SupportedBearers").contains("GPRS"));
	}

	// BrowserUA
	@Test
	public void testBrowserName()  throws Exception {
		super.testBrowserName();
		assertTrue(profile.getAttribute("BrowserName").contains("METE"));
	}

	// Wap Characteristics
	@Test
	public void testWapVersion()  throws Exception {
		super.testWapVersion();
		assertTrue(profile.getAttribute("WapVersion").contains("June 2000"));
	}

	@Test
	public void testWmlVersion()  throws Exception {
		super.testWmlVersion();
		assertTrue(profile.getAttribute("WmlVersion").contains("1.2"));
	}

	// Push characteristics
	@Test
	public void testWapPushMsgSize() throws Exception  {
		super.testPushMsgSize();
		assertTrue(profile.getAttribute("Push-MsgSize").contains("1400"));
	}
}
