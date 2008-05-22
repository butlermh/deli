package com.hp.hpl.deli;

import junit.framework.TestCase;

public class DeliUtilTest extends TestCase {

	public DeliUtilTest(String name) {
		super(name);
	}
	
	private String s1 = " some   mixed     white spaces";
	private String s2 = "some mixed white spaces";

	public void testCalculateProfileDiffDigest() throws Exception {
		assertEquals(Utils.calculateProfileDiffDigest(s1, true), Utils.calculateProfileDiffDigest(s2, true));
		assertTrue(Utils.calculateProfileDiffDigest(s1, false) != Utils.calculateProfileDiffDigest(s2, false)); 
	}

	public void testRemoveWhitespaces() {
		assertEquals(Utils.removeWhitespaces(s1), s2); 
	}
}
