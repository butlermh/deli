package com.hp.hpl.deli;

import java.io.IOException;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ProfileCacheTest extends TestCase {

	public ProfileCacheTest(String name) {
		super(name);
	}

	@Test
	public void testProfileCache() throws IOException {
		ProfileProcessor configuration = new ProfileProcessor(Constants.CONFIG_FILE);
		ProfileCache pc = new ProfileCache(configuration);
		LocalProfiles localProfiles = new LocalProfiles(configuration);
		HttpProfileProcessor profileProcessor = new HttpProfileProcessor(configuration, localProfiles, pc);
		Profile profile = pc.get(profileProcessor, TestConstants.WEB_PROFILE_URI);
		Vector<String> result = new Vector<String>();
		result.add("1");
		assertEquals(profile.getAttribute("BitsPerPixel").getValue(), result);
	}

}
