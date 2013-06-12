package com.hp.hpl.deli.functional;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.Profile;
import com.hp.hpl.deli.ProfileProcessor;

@SuppressWarnings("javadoc")
public class W3CTestHarness {
	public static ProfileProcessor configuration;
	
	@BeforeClass
	public static void oneTimeSetup() throws IOException {
		configuration = new ProfileProcessor(Constants.CONFIG_FILE);
	}

	@Test
	public void testOne() throws Exception {
		System.out
				.println("01A: Checking processor can process valid RDF serialized XML");

		Profile myprofile = configuration.processProfile( "profiles/W3Ctests/test-profile-01a.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwo() throws Exception {
		System.out
				.println("01B: Checking processor can process valid RDF serialized XML");

		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01b.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThree() throws Exception {
		System.out
				.println("01C: Checking processor can process valid RDF serialized XML");

		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01c.rdf");
		System.out.println(myprofile.toString());
	}
	
	@Test
	public void testFour() throws Exception {
		System.out
				.println("01D: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01d.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testFive() throws Exception {
		System.out
				.println("O1E: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01e.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testSix() throws Exception {
		System.out
				.println("O1F: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01f.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testSeven() throws Exception {
		System.out
				.println("O1G: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01g.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testEight() throws Exception {
		System.out
				.println("01H: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01h.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testNine() throws Exception {
		System.out
				.println("01I: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01i.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTen() throws Exception {
		System.out
				.println("01J: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01j.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testEleven() throws Exception {
		System.out
				.println("01K: Checking processor can process valid RDF serialized XML");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-01k.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwelve() throws Exception {
		System.out.println("02A: Checking component name MAY be in a type statement");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-02a.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirteen() throws Exception {
		System.out.println("02B: Checking component name MAY be in a type statement");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-02b.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testFourteen() throws Exception {
		System.out
				.println("03: Checking components MUST be associated with the CC/PP namespace or some subclass thereof");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-03.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testFifteen() throws Exception {
		System.out.println("06A: Checking default references MUST be valid URLs");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-06a.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testSixteen() throws Exception {
		System.out.println("06B: Checking references MUST be valid URLs");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-06b.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testSeventeen() throws Exception {
		System.out
				.println("07: Checking defaults MUST be associted with the CC/PP namespace or some subclass thereof");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-07.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testEighteen() throws Exception {
		System.out
				.println("08: Checking MAY contain both a default value and a directly applied value, directly applied value takes precedence");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-08.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testNineteen() throws Exception {
		System.out
				.println("09: Checking MUST use valid syntax for namesapce declarations");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-09.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwenty() throws Exception {
		System.out
				.println("10: Checking MUST use valid syntax for namespace declarations");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-10.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentyOne() throws Exception {
		System.out
				.println("11A: Checking MUST use valid syntax for namespace declarations");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-11a.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentyTwo() throws Exception {
		System.out
				.println("11B: Checking MUST use valid syntax for namespace declarations");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-11b.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentyThree() throws Exception {
		System.out
				.println("12: Checking MAY contain attributes that are sets of values (Bags)");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-12.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentyFour() throws Exception {
		System.out
				.println("13: Checking MAY contain attributes that are sequences of values (Seq)");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-13.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentyFive() throws Exception {
		System.out.println("15: Checking MAY contain attributes that are Text values");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-15.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentySix() throws Exception {
		System.out
				.println("16: Checking MAY contain attributes that are Integer numbers");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-16.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentySeven() throws Exception {
		System.out
				.println("17: Checking MAY contain attributes that are Rational numbers");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-17.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentyEight() throws Exception {
		System.out
				.println("18: Invalid: components MUST be associated with the CC/PP namespace or some subclass thereof");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-18.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testTwentyNine() throws Exception {
		System.out.println("19: Invalid: profile MUST contain one or more components ");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-19.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirty() throws Exception {
		System.out.println("20: Invalid: profile MUST contain one or more components");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-20.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirtyOne() throws Exception {
		System.out
				.println("21: Invalid: defaults MUST be associated with the CC/PP namespace or some subclass thereof");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-21.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirtyTwo() throws Exception {
		System.out.println("22: Invalid: the component graph must not contain loops");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-22.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirtyThree() throws Exception {
		System.out.println("23: the component graph must not contain loops");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-23.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirtyFour() throws Exception {
		System.out.println("24: Invalid: the component graph must not contain loops");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-24.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirtyFive() throws Exception {
		System.out.println("25: MUST NOT contain both inline and referenced defaults");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-25.rdf");
		System.out.println(myprofile.toString());
	}

	@Test
	public void testThirtySix() throws Exception {
		System.out
				.println("26: Invalid: MUST NOT contain both inline and referenced defaults");
		Profile myprofile = configuration.processProfile("profiles/W3Ctests/test-profile-26.rdf");
		System.out.println(myprofile.toString());
	}
}
