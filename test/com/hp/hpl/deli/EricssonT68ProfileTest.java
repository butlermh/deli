package com.hp.hpl.deli;

import junit.framework.Test;
import junit.framework.TestSuite;


public class EricssonT68ProfileTest extends UAProfProfileTest {
    // This test checks that you can load a UAProf reference profile from
    // the local filesystem using DELI.
    // Note it does not check *ALL* attributes in the profile, just a subset
    public EricssonT68ProfileTest(String name) {
        super(name);
        create("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
        		"profiles/testprofiles/Ericsson_T68R1.rdf");
    }

    public static Test suite() {
        return new TestSuite(EricssonT68ProfileTest.class);
    }

    // Hardware Platform
    public void testModel() {
        super.testModel();
        assertTrue(profile.getAttribute("Model").contains("T68R1"));
    }

    public void testTextInputCapable() {
        super.testTextInputCapable();
        assertTrue(profile.getAttribute("TextInputCapable").contains("Yes"));
    }

    // Software platform
    public void testCcppAcceptCharset() {
        //super.testCcppAcceptCharset();
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("US-ASCII"));
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-8859-1"));
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("UTF-8"));
        assertTrue(profile.getAttribute("CcppAccept-Charset").contains("ISO-10646-UCS-2"));
    }

    public void testAcceptDownloadableSoftware() {
        super.testAcceptDownloadableSoftware();
        assertTrue(profile.getAttribute("AcceptDownloadableSoftware").contains("No"));
    }

    // Network characteristics
    public void testSupportedBearers() {
        super.testSupportedBearers();

        //testAttribute("SupportedBearers", "NetworkCharacteristics", "Locked", "Bag", "Literal", wapURI, 
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
        assertTrue(profile.getAttribute("WapVersion").contains("1.2.1/June 2000"));
    }

    public void testWmlVersion() {
        super.testWmlVersion();
        assertTrue(profile.getAttribute("WmlVersion").contains("1.1"));
        assertTrue(profile.getAttribute("WmlVersion").contains("1.2.1/June 2000"));
    }

    // Push characteristics
    public void testWapPushMsgSize() {
        super.testPushMsgSize();
        assertTrue(profile.getAttribute("Push-MsgSize").contains("3000"));
    }
}
