package com.hp.hpl.deli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalProfilesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLocalProfilesWorkspace() {
		fail("Not yet implemented");
	}

	@Test
	public void testLocalProfilesString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetReference() {
		fail("Not yet implemented");
	}
	
	public void testOne() throws Exception {
		LocalProfiles lp = new LocalProfiles(Constants.LOCAL_PROFILES_FILE);
		String s = lp.getReference("MOT-SAP4 /11.03 UP.Browser/4.1.23c");
		assertEquals(s, "http://www.w3development.de/rdf/uaprof_repository/Motorola_V66.rdf");
	}
}
