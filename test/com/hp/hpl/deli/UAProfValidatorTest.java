package com.hp.hpl.deli;

import junit.framework.TestCase;

public class UAProfValidatorTest extends TestCase {
	
	static UAProfValidator v = null;

	public UAProfValidatorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		if (v == null) {
			v = new UAProfValidator();
			Workspace.getInstance().configure(null, Constants.VALIDATOR_CONFIG_FILE);
		}
	}

	public void testUAProfValidator() throws Exception {
		assertTrue(v.process(TestConstants.WEB_PROFILE_URI));
	}

	public void testSetTruststamp() {
		v.setTruststamp(false);
	}

	public void testResults() {
		v.results();
	}

	public void testDownload() {
		UAProfValidator.download(TestConstants.WEB_PROFILE_URI);
	}
	
	public void testTwo() {
		assertTrue(v.process("http://wap.sonyericsson.com/UAprof/K790iR101.xml"));
	}
	
	public void testThree() {
		assertTrue(v.process("http://nds.nokia.com/uaprof/N6800r200.xml"));
	}

}
