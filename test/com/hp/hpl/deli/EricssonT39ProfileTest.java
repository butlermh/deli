package com.hp.hpl.deli;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EricssonT39ProfileTest extends UAProfProfileTest {
    public EricssonT39ProfileTest(String name) {
        super(name);
        create("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
        	"profiles/testprofiles/Ericsson_T39.rdf");
    }

    public static Test suite() {
        return new TestSuite(EricssonT39ProfileTest.class);
    }

    // Hardware Platform
    public void testBitsPerPixel() {
        super.testBitsPerPixel();
        assertTrue(profile.getAttribute("BitsPerPixel").contains("2"));
    }

    public void testColorCapable() {
        super.testColorCapable();
        assertTrue(profile.getAttribute("ColorCapable").contains("No"));
    }

    // Software platform
    public void testCcppAcceptCharset() {
        // super.testCcppAcceptCharset();
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("US-ASCII"));
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-8859-1"));
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("UTF-8"));
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-10646-UCS-2"));
    }

    public void testCcppAcceptEncoding() {
        // super.testCcppAcceptEncoding();
        assertTrue(profile.getAttribute("CcppAccept-Encoding").contains("base64"));
    }

    // Network characteristics
    public void testSupportedBearers() {
        super.testSupportedBearers();
        assertTrue(profile.getAttribute("SupportedBearers").contains("TwoWaySMS"));
        assertTrue(profile.getAttribute("SupportedBearers").contains("CSD"));
        assertTrue(profile.getAttribute("SupportedBearers").contains("GPRS"));
    }

    // BrowserUA
    public void testFramesCapable() {
        super.testFramesCapable();
        assertTrue(profile.getAttribute("FramesCapable").contains("No"));
    }

    public void testTablesCapable() {
        super.testTablesCapable();
        assertTrue(profile.getAttribute("TablesCapable").contains("Yes"));
    }

    // Wap Characteristics
    public void testWapVersion() {
        super.testWapVersion();
        assertTrue(profile.getAttribute("WapVersion").contains("1.2.1"));
    }

    public void testWmlVersion() {
        super.testWmlVersion();
        assertTrue(profile.getAttribute("WmlVersion").contains("1.1"));
        assertTrue(profile.getAttribute("WmlVersion").contains("1.2"));
        assertTrue(profile.getAttribute("WmlVersion").contains("1.3"));
    }

    // Push characteristics
    public void testWapPushMsgSize() {
        super.testPushMsgSize();
        assertTrue(profile.getAttribute("Push-MsgSize").contains("3000"));
    }
}
