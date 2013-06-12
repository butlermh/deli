package com.hp.hpl.deli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("javadoc")
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
	}

	
	@Ignore @Test
	public void testCachedProfile() {
		fail("Not yet implemented");
	}

	@Ignore @Test
	public void testSet() {
		fail("Not yet implemented");
	}

	@Ignore @Test
	public void testGet() {
		fail("Not yet implemented");
	}

}
