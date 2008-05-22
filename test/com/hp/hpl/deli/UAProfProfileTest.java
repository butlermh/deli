package com.hp.hpl.deli;

abstract public class UAProfProfileTest extends CCPPProfileTest {
	public UAProfProfileTest(String name) {
		super(name);
	}

	public void testModel() {
		testAttribute("Model", "HardwarePlatform", Constants.LOCKED, Constants.SIMPLE, Constants.LITERAL);
	}

	public void testTextInputCapable() {
		testAttribute("TextInputCapable", "HardwarePlatform", Constants.LOCKED, Constants.SIMPLE, Constants.BOOLEAN);
	}

	public void testAcceptDownloadableSoftware() {
		testAttribute("AcceptDownloadableSoftware", "SoftwarePlatform", Constants.LOCKED, Constants.SIMPLE, Constants.BOOLEAN);
	}

	public void testSupportedBearers() {
		testAttribute("SupportedBearers", "NetworkCharacteristics", Constants.LOCKED, Constants.BAG, Constants.LITERAL);
	}

	public void testFramesCapable() {
		testAttribute("FramesCapable", "BrowserUA", Constants.OVERRIDE, Constants.SIMPLE, Constants.BOOLEAN);
	}

	public void testTablesCapable() {
		testAttribute("TablesCapable", "BrowserUA", Constants.LOCKED, Constants.SIMPLE, Constants.BOOLEAN);
	}

	public void testWapVersion() {
		testAttribute("WapVersion", "WapCharacteristics", Constants.LOCKED, null, Constants.LITERAL);
	}

	public void testWmlVersion() {
		testAttribute("WmlVersion", "WapCharacteristics", Constants.APPEND, Constants.BAG, Constants.LITERAL);
	}

	public void testPushMsgSize() {
		// Type should be number but this attribute is not defined in the schemas
		testAttribute("Push-MsgSize", "PushCharacteristics", Constants.OVERRIDE, Constants.SIMPLE, Constants.NUMBER);
	}

	public void testBitsPerPixel() {
		testAttribute("BitsPerPixel", "HardwarePlatform", Constants.OVERRIDE, Constants.SIMPLE, Constants.NUMBER);
	}

	public void testColorCapable() {
		testAttribute("ColorCapable", "HardwarePlatform", Constants.OVERRIDE, Constants.SIMPLE, Constants.BOOLEAN);
	}

	public void testCcppAcceptCharset() {
		testAttribute("CcppAccept-Charset", "SoftwarePlatform", Constants.APPEND, Constants.BAG, Constants.LITERAL);
	}

	public void testCcppAcceptEncoding() {
		testAttribute("CcppAccept-Encoding", "SoftwarePlatform", Constants.APPEND, Constants.BAG, Constants.LITERAL);
	}

	public void testPushAccept() {
		testAttribute("Push-Accept", "PushCharacteristics", Constants.OVERRIDE, Constants.BAG, Constants.LITERAL);
	}

	public void testBrowserName() {
		testAttribute("BrowserName", "BrowserUA", Constants.LOCKED, Constants.SIMPLE, Constants.LITERAL);
	}

	public void testBluetoothProfile() {
		testAttribute("BluetoothProfile", "HardwarePlatform", Constants.LOCKED, Constants.BAG, Constants.LITERAL);
	}

	public void testSupportedBluetoothVersion() {
		testAttribute("SupportedBluetoothVersion", "NetworkCharacteristics", Constants.LOCKED, Constants.SIMPLE, Constants.LITERAL);
	}

	public void testSecuritySupport() {
		testAttribute("SecuritySupport", "NetworkCharacteristics", Constants.LOCKED, null, Constants.LITERAL);
	}

	public void testCcppAccept() {
		testAttribute("CcppAccept", null, Constants.APPEND, Constants.BAG, Constants.LITERAL);
	}

	public void testScreenSize() {
		testAttribute("ScreenSize", "HardwarePlatform", Constants.LOCKED, Constants.SIMPLE, Constants.DIMENSION);
	}
}