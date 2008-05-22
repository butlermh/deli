package com.hp.hpl.deli;

import junit.framework.TestCase;

public class LocalProfilesTest extends TestCase {

	public LocalProfilesTest(String name) {
		super(name);
	}

	public void testLocalProfiles() {
		LocalProfiles lp = new LocalProfiles(Constants.ALL_KNOWN_UAPROF_PROFILES);
		String s = lp.getReference("MOT-SAP4 /11.03 UP.Browser/4.1.23c");
		assertEquals(s, "http://www.w3development.de/rdf/uaprof_repository/Motorola_V66.rdf");
	}

}
