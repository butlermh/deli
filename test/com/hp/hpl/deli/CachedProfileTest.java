package com.hp.hpl.deli;

import java.util.Vector;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

public class CachedProfileTest extends TestCase {

	public CachedProfileTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Workspace.getInstance().configure((ServletContext) null, Constants.CONFIG_FILE);
	}

	public void test() {
		CachedProfile cp = new CachedProfile();
		cp.set(TestConstants.WEB_PROFILE_URI);
		Profile profile = cp.get();
		Vector<String> result = new Vector<String>();
		result.add("1");
		assertEquals(profile.getAttribute("BitsPerPixel").getValue(), result);
	}
}
