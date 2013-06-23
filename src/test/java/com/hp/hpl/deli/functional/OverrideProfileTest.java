package com.hp.hpl.deli.functional;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class OverrideProfileTest extends CCPPProfileTest {
	@Before
	public void setUp() {
        setUp("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
        		"profiles/testprofiles/testProfile06.rdf");
    }

	@Test
    public void testAll() {
        assertTrue(profile.getAttribute("BluetoothProfile").contains("StandardA"));
        assertTrue(profile.getAttribute("InputCharSet").contains("StandardA"));
        assertTrue(profile.getAttribute("InputCharSet").contains("StandardB"));
        assertTrue(profile.getAttribute("Push-Accept").contains("StandardA"));
        assertTrue(profile.getAttribute("Push-Accept").contains("StandardB"));
        assertTrue(profile.getAttribute("SoundOutputCapable").contains("Standard"));
        assertTrue(profile.getAttribute("BitsPerPixel").contains("Standard"));
    }
}