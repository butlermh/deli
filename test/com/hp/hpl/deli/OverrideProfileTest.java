package com.hp.hpl.deli;


import junit.framework.Test;
import junit.framework.TestSuite;


public class OverrideProfileTest extends CCPPProfileTest {
    public OverrideProfileTest(String name) {
        super(name);
        create("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
        		"profiles/testprofiles/testProfile06.rdf");
    }

    public static Test suite() {
        return new TestSuite(OverrideProfileTest.class);
    }

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