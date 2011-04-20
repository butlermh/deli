package com.hp.hpl.deli.functional;

import org.junit.Test;

import com.hp.hpl.deli.Constants;

abstract public class UAProfProfileTest extends CCPPProfileTest {

	@Test
	public void testModel() throws Exception {
		testAttribute("Model", "HardwarePlatform", Constants.LOCKED, Constants.SIMPLE,
				Constants.LITERAL);
	}

	@Test
	public void testTextInputCapable() throws Exception {
		testAttribute("TextInputCapable", "HardwarePlatform", Constants.LOCKED,
				Constants.SIMPLE, Constants.BOOLEAN);
	}

	@Test
	public void testAcceptDownloadableSoftware() throws Exception {
		testAttribute("AcceptDownloadableSoftware", "SoftwarePlatform", Constants.LOCKED,
				Constants.SIMPLE, Constants.BOOLEAN);
	}

	@Test
	public void testSupportedBearers() throws Exception {
		testAttribute("SupportedBearers", "NetworkCharacteristics", Constants.LOCKED,
				Constants.BAG, Constants.LITERAL);
	}

	@Test
	public void testFramesCapable() throws Exception {
		testAttribute("FramesCapable", "BrowserUA", Constants.OVERRIDE, Constants.SIMPLE,
				Constants.BOOLEAN);
	}

	@Test
	public void testTablesCapable() throws Exception {
		testAttribute("TablesCapable", "BrowserUA", Constants.LOCKED, Constants.SIMPLE,
				Constants.BOOLEAN);
	}

	@Test
	public void testWapVersion() throws Exception {
		testAttribute("WapVersion", "WapCharacteristics", Constants.LOCKED, null,
				Constants.LITERAL);
	}

	@Test
	public void testWmlVersion() throws Exception {
		testAttribute("WmlVersion", "WapCharacteristics", Constants.APPEND,
				Constants.BAG, Constants.LITERAL);
	}

	@Test
	public void testPushMsgSize() throws Exception {
		// Type should be number but this attribute is not defined in the
		// schemas
		testAttribute("Push-MsgSize", "PushCharacteristics", Constants.OVERRIDE,
				Constants.SIMPLE, Constants.NUMBER);
	}

	@Test
	public void testBitsPerPixel()  throws Exception {
		testAttribute("BitsPerPixel", "HardwarePlatform", Constants.OVERRIDE,
				Constants.SIMPLE, Constants.NUMBER);
	}

	@Test
	public void testColorCapable()  throws Exception {
		testAttribute("ColorCapable", "HardwarePlatform", Constants.OVERRIDE,
				Constants.SIMPLE, Constants.BOOLEAN);
	}

	@Test
	public void testCcppAcceptCharset()  throws Exception {
		testAttribute("CcppAccept-Charset", "SoftwarePlatform", Constants.APPEND,
				Constants.BAG, Constants.LITERAL);
	}

	@Test
	public void testCcppAcceptEncoding()  throws Exception {
		testAttribute("CcppAccept-Encoding", "SoftwarePlatform", Constants.APPEND,
				Constants.BAG, Constants.LITERAL);
	}

	@Test
	public void testPushAccept()  throws Exception {
		testAttribute("Push-Accept", "PushCharacteristics", Constants.OVERRIDE,
				Constants.BAG, Constants.LITERAL);
	}

	@Test
	public void testBrowserName()  throws Exception {
		testAttribute("BrowserName", "BrowserUA", Constants.LOCKED, Constants.SIMPLE,
				Constants.LITERAL);
	}

	@Test
	public void testBluetoothProfile()  throws Exception {
		testAttribute("BluetoothProfile", "HardwarePlatform", Constants.LOCKED,
				Constants.BAG, Constants.LITERAL);
	}

	@Test
	public void testSupportedBluetoothVersion()  throws Exception {
		testAttribute("SupportedBluetoothVersion", "NetworkCharacteristics",
				Constants.LOCKED, Constants.SIMPLE, Constants.LITERAL);
	}

	@Test
	public void testSecuritySupport()  throws Exception  {
		testAttribute("SecuritySupport", "NetworkCharacteristics", Constants.LOCKED,
				null, Constants.LITERAL);
	}

	@Test
	public void testCcppAccept()  throws Exception {
		testAttribute("CcppAccept", null, Constants.APPEND, Constants.BAG,
				Constants.LITERAL);
	}

	@Test
	public void testScreenSize()  throws Exception {
		testAttribute("ScreenSize", "HardwarePlatform", Constants.LOCKED,
				Constants.SIMPLE, Constants.DIMENSION);
	}
}