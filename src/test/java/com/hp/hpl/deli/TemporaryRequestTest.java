package com.hp.hpl.deli;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.ProcessedRequest;
import com.hp.hpl.deli.DeliConfiguration;

public class TemporaryRequestTest {
	
	private String s1 = " some   mixed     white spaces";

	private String s2 = "some mixed white spaces";
	
	static DeliConfiguration workspace;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		workspace = new DeliConfiguration(Constants.CONFIG_FILE);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTemporaryRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalculateProfileDiffDigest() throws Exception {
		assertEquals(ProcessedRequest.calculateProfileDiffDigest(s1, true),
				ProcessedRequest.calculateProfileDiffDigest(s2, true));
		assertTrue(ProcessedRequest.calculateProfileDiffDigest(s1, false) != ProcessedRequest
				.calculateProfileDiffDigest(s2, false));
	}

	@Test
	public void testRemoveWhitespaces() {
		assertEquals(ProcessedRequest.removeWhitespaces(s1), s2);
	}
	
	/**
	 * No UAProf data
	 */
	public void testOne() throws Exception {
		String[] param = {};
		MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
		ProcessedRequest phr = new ProcessedRequest(workspace, null, (HttpServletRequest) mockRequest);
		assertEquals(0, phr.getReferenceVector().size());
		assertEquals(0, phr.getDiffVector().size());
	}
	
	static final String PROFILE = "profiles/testprofiles/Ericsson_T39.rdf";
	
	/**
	 * This flag removes carriage-returns in profile diffs overcoming a bug in
	 * Tomcat 3.2.1 and some other servers.
	 */
	static boolean noCarriageReturnsInProfileDiffs = true;
	
	/**
	 * Try a profile ref containing a profile-diff - this should return nothing
	 */
	public void testTwo() throws Exception {
		TempProfileDiff p1 = new TempProfileDiff(PROFILE,
				noCarriageReturnsInProfileDiffs, false);
		String[] param = { "x-wap-profile: \"1-" + p1.profileDiffDigest + "\"\n" };
		MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
		ProcessedRequest phr = new ProcessedRequest(workspace, null, (HttpServletRequest) mockRequest);
		assertEquals(0, phr.getReferenceVector().size());
		assertEquals(0, phr.getDiffVector().size());
	}
	
	/**
	 * Valid profile ref
	 */
	public void testThree() {
		String[] param = { "x-wap-profile: \"" + TestConstants.WEB_PROFILE_URI + "\n" };
		MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
		ProcessedRequest phr = new ProcessedRequest(workspace, null, (HttpServletRequest) mockRequest);
		assertEquals(1, phr.getReferenceVector().size());
		assertEquals(0, phr.getDiffVector().size());
	}
	
//	HttpProfileProcessor processor = null;
//	
//	Workspace workspace = null;
	
//	protected void setUp() throws Exception {
//	super.setUp();
//	workspace = new Workspace(Constants.CONFIG_FILE);
//	SchemaCollection vocabulary = new SchemaCollection(workspace);
//	LocalProfiles localProfiles = new LocalProfiles(workspace);
//	ProfileCache profileCache = new ProfileCache(workspace, vocabulary);
//	processor = new HttpProfileProcessor(workspace, vocabulary, localProfiles, profileCache);
//}
	
	// public void testFour() {
	// TempProfileDiff p1 = new TempProfileDiff(PROFILE,
	// noCarriageReturnsInProfileDiffs, false);
	// String[] param = {"x-wap-profile: \"1-" + p1.profileDiff + "\"\n",
	// "x-wap-profile-diff:1;\"" + p1.profileDiffDigest + "\"\n"};
	// MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
	// ProcessHttpRequest phr = new ProcessHttpRequest((HttpServletRequest)
	// mockRequest);
	// assertEquals(1, phr.getReferenceVector().size());
	// assertEquals(0, phr.getDiffVector().size());
	// }
	//
	// public void testFive() {
	// TempProfileDiff p1 = new TempProfileDiff(args[2],
	// noCarriageReturnsInProfileDiffs, false);
	// request.append("x-wap-profile: \"" + args[1] + "\", \"1-" +
	// p1.profileDiffDigest + "\"\n");
	// request.append("x-wap-profile-diff:1;\"" + p1.profileDiff + "\"\n");
	// }
	//
	// public void testSix() {
	// TempProfileDiff p2 = new TempProfileDiff(args[3],
	// noCarriageReturnsInProfileDiffs, false);
	// request.append("x-wap-profile: \"" + args[1] + "\", \"1-" +
	// p1.profileDiffDigest + "\", ");
	// request.append("\"2-" + p2.profileDiffDigest + "\"\n");
	// }
	//
	// public void testSeven() {
	// request.append("x-wap-profile-diff:3;\"" + p1.profileDiff + "\"\n");
	// request.append("x-wap-profile-diff:8;\"" + p2.profileDiff + "\"\n");
	// }
	//
	// public void testEight() {
	// request.append("x-wap-profile-diff:1;\"" + p1.profileDiff + "\"\n");
	// request.append("x-wap-profile-diff:2;\"" + p2.profileDiff + "\"\n");
	// }

}
