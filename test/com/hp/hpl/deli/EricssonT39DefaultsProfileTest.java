package com.hp.hpl.deli;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EricssonT39DefaultsProfileTest extends EricssonT39ProfileTest {

    public EricssonT39DefaultsProfileTest(String name) {
        super(name);
        create("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
        	"profiles/testprofiles/Ericsson_T39defaults.rdf");
    }
    
    public static Test suite() {
        return new TestSuite(EricssonT39DefaultsProfileTest.class);
    }
}