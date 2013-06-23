package com.hp.hpl.deli.functional;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class UAProfSpecProfileTest extends UAProfProfileTest {
    // This test checks that you can load a UAProf reference profile from
    // the local filesystem using DELI.
    // Note it does not check *ALL* attributes in the profile, just a subset
	@Before
    public void setUp() {
        setUp("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
        		"profiles/testprofiles/testProfile01.rdf");
    }

    // Hardware Platform
    @Test
    public void testBluetoothProfile() {
        try {
            super.testBluetoothProfile();
            assertTrue(profile.getAttribute("BluetoothProfile").contains("headset"));
            assertTrue(profile.getAttribute("BluetoothProfile").contains("dialup"));
            assertTrue(profile.getAttribute("BluetoothProfile").contains("lanaccess"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @Test
    public void testScreenSize()  throws Exception {
        super.testScreenSize();
        assertTrue(profile.getAttribute("ScreenSize").contains("121x87"));
    }

    // Software Platform
    @Test
    public void testAcceptDownloadableSoftware()  throws Exception {
        super.testAcceptDownloadableSoftware();
        assertTrue(profile.getAttribute("AcceptDownloadableSoftware").contains("No"));
    }

    @Test
    public void testCcppAccept() {
        // super.testCcppAccept();
        assertTrue(profile.getAttribute("CcppAccept").contains("application/vnd.wap.wmlc"));
        assertTrue(profile.getAttribute("CcppAccept").contains("application/vnd.wap.wbxml"));
        assertTrue(profile.getAttribute("CcppAccept").contains("application/vnd.wap.wmlscriptc"));
        assertTrue(profile.getAttribute("CcppAccept").contains("application/vnd.wap.multipart.mixed"));
        assertTrue(profile.getAttribute("CcppAccept").contains("application/vnd.wap.multipart.form-data"));
        assertTrue(profile.getAttribute("CcppAccept").contains("text/vnd.wap.wml"));
        assertTrue(profile.getAttribute("CcppAccept").contains("text/vnd.wap.wmlscript"));
        assertTrue(profile.getAttribute("CcppAccept").contains("text/x-vCard"));
        assertTrue(profile.getAttribute("CcppAccept").contains("text/x-vCalendar"));
        assertTrue(profile.getAttribute("CcppAccept").contains("text/x-vMel"));
        assertTrue(profile.getAttribute("CcppAccept").contains("text/x-eMelody"));
        assertTrue(profile.getAttribute("CcppAccept").contains("image/vnd.wap.wbmp"));
        assertTrue(profile.getAttribute("CcppAccept").contains("image/gif"));
    }

    // Network characteristics
    @Test
    public void testSecuritySupport()  throws Exception {
        super.testSecuritySupport();
        assertTrue(profile.getAttribute("SecuritySupport").contains("signText"));
        assertTrue(profile.getAttribute("SecuritySupport").contains("WTLS-1"));
        assertTrue(profile.getAttribute("SecuritySupport").contains("WTLS-2"));
        assertTrue(profile.getAttribute("SecuritySupport").contains("WTLS-3"));
    }

    @Test
    public void testSupportedBluetoothVersion() {
        try {
            super.testSupportedBluetoothVersion();
            assertTrue(profile.getAttribute("SupportedBluetoothVersion").contains("1.1"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    // BrowserUA
    @Test
    public void testBrowserName()  throws Exception {
        super.testBrowserName();
        assertTrue(profile.getAttribute("BrowserName").contains("Ericsson"));
    }

    @Test
    public void testFramesCapable() throws Exception  {
        super.testFramesCapable();
        assertTrue(profile.getAttribute("FramesCapable").contains("No"));
    }

    // Wap characteristics
    @Test
    public void testWapVersion()  throws Exception {
        super.testWapVersion();
        assertTrue(profile.getAttribute("WapVersion").contains("2.0"));
    }

    @Test
    public void testWmlVersion()  throws Exception {
        super.testWmlVersion();
        assertTrue(profile.getAttribute("WmlVersion").contains("2.0"));
    }

    // Push characteristics
    @Test
    public void testPushAccept() {
        try {
            super.testPushAccept();
            assertTrue(profile.getAttribute("Push-Accept").contains("text/html"));
            assertTrue(profile.getAttribute("Push-Accept").contains("application/wml+xml"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @Test
    public void testPushMsgSize() {
        try {
            super.testPushMsgSize();
            assertTrue(profile.getAttribute("Push-MsgSize").contains("1400"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}