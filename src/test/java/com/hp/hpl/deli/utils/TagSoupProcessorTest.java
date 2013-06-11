package com.hp.hpl.deli.utils;

import org.junit.*;

import static org.junit.Assert.assertTrue;

public class TagSoupProcessorTest {

	@Test
	public void testGetTagSoup() {
		assertTrue(TagSoupProcessor.getTagSoup(
				"<prf:Vendor rdf:datatype=\"&prf-dt;Literal\">SAMSUNG</prf:Vendor>",
				":Vendor rdf:datatype=\"&prf-dt;Literal\">", "<").equals("SAMSUNG"));
	}

	@Test
	public void testGetBackwardTagSoupOne() {
		assertTrue(TagSoupProcessor.getBackwardTagSoup(
				"<prf:Vendor rdf:datatype=\"&prf-dt;Literal\">SAMSUNG</prf:Vendor>", ">",
				"</prf:Vendor").equals("SAMSUNG"));
	}
	
	@Test
	public void testGetBackwardTagSoupTwo() {
		assertTrue(TagSoupProcessor.getBackwardTagSoup(
				"<prf:Vendor rdf:datatype=\"http://www.openmobilealliance.org/tech/profiles/UAPROF/xmlschema-20030226#Literal\">Sony Ericsson Mobile Communications</prf:Vendor>", ">",
				"</prf:Vendor").equals("Sony Ericsson Mobile Communications"));
		 
	}
}
