package com.hp.hpl.deli.functional;

import org.junit.Before;

@SuppressWarnings("javadoc")
public class EricssonT39DefaultsProfileTest extends EricssonT39ProfileTest {
	@Before
	public void setUp() {
        setUp("http://www.wapforum.org/profiles/UAPROF/ccppschema-20010430#",
        	"profiles/testprofiles/Ericsson_T39defaults.rdf");
    }
}