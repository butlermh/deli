package com.hp.hpl.deli;

import java.util.Vector;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

public class ProfileCacheTest extends TestCase {

	public ProfileCacheTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Workspace.getInstance().configure((ServletContext) null, Constants.CONFIG_FILE);
	}

	public void testProfileCache() {
		ProfileCache pc = new ProfileCache(true, true, 1, 3);
		Profile profile = pc.get(TestConstants.WEB_PROFILE_URI);
		Vector<String> result = new Vector<String>();
		result.add("1");
		assertEquals(profile.getAttribute("BitsPerPixel").getValue(), result);
	}

}
