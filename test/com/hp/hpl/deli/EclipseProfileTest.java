package com.hp.hpl.deli;

public class EclipseProfileTest extends UAProfProfileTest {
	public EclipseProfileTest(String name) {
		super(name);
		create("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#", "profiles/testprofiles/Trium_Eclipse.rdf");
	}

	// Hardware Platform
	public void testModel() {
		super.testModel();
		assertTrue(profile.getAttribute("Model").contains("Eclipse"));
	}

	public void testTextInputCapable() {
		super.testTextInputCapable();
		assertTrue(profile.getAttribute("TextInputCapable").contains("Yes"));
	}

	// Software platform
	public void testCcppAcceptCharset() {
		// super.testCcppAcceptCharset();
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("US-ASCII"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-8859-1"));
		assertTrue(profile.getAttribute("CcppAccept-Charset").contains("UTF-8"));
	}

	public void testAcceptDownloadableSoftware() {
		super.testAcceptDownloadableSoftware();
		assertTrue(profile.getAttribute("AcceptDownloadableSoftware").contains("No"));
	}

	// Network characteristics
	public void testSecuritySupport() {
		super.testSecuritySupport();
		assertTrue(profile.getAttribute("SecuritySupport").contains("WTLS"));
	}

	public void testSupportedBearers() {
		super.testSupportedBearers();
		assertTrue(profile.getAttribute("SupportedBearers").contains("OneWaySMS"));
		assertTrue(profile.getAttribute("SupportedBearers").contains("CSD"));
		assertTrue(profile.getAttribute("SupportedBearers").contains("GPRS"));
	}

	// BrowserUA
	public void testBrowserName() {
		super.testBrowserName();
		assertTrue(profile.getAttribute("BrowserName").contains("METE"));
	}

	// Wap Characteristics
	public void testWapVersion() {
		super.testWapVersion();
		assertTrue(profile.getAttribute("WapVersion").contains("June 2000"));
	}

	public void testWmlVersion() {
		super.testWmlVersion();
		assertTrue(profile.getAttribute("WmlVersion").contains("1.2"));
	}

	// Push characteristics
	public void testWapPushMsgSize() {
		super.testPushMsgSize();
		assertTrue(profile.getAttribute("Push-MsgSize").contains("1400"));
	}
}
