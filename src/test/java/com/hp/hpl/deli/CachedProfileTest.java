package com.hp.hpl.deli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CachedProfileTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() throws IOException {
		ProfileProcessor configuration = new ProfileProcessor(Constants.CONFIG_FILE);
		CachedProfile cp = new CachedProfile(configuration);
		cp.set(TestConstants.WEB_PROFILE_URI);
		Profile profile = cp.get(true, 1000);
		Vector<String> result = new Vector<String>();
		result.add("1");
		assertEquals(profile.getAttribute("BitsPerPixel").getValue(), result);
		fail("Need to use mocks for workspace and vocabulary");
	}

	@Test
	public void testCachedProfile() {
		fail("Not yet implemented");
	}

	@Test
	public void testSet() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

}
