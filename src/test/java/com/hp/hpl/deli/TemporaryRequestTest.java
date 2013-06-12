package com.hp.hpl.deli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TemporaryRequestTest {
	
	private static final String EXAMPLE_STRING_ONE = " some   mixed     white spaces";
	private static final String EXAMPLE_STRING_TWO = "some mixed white spaces";
	
	static DeliConfiguration workspace;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		workspace = new DeliConfiguration(Constants.CONFIG_FILE);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test @Ignore
	public void testTemporaryRequest() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testGetRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalculateProfileDiffDigest() throws Exception {
		assertEquals(ProcessedRequest.calculateProfileDiffDigest(EXAMPLE_STRING_ONE, true),
				ProcessedRequest.calculateProfileDiffDigest(EXAMPLE_STRING_TWO, true));
		assertTrue(ProcessedRequest.calculateProfileDiffDigest(EXAMPLE_STRING_ONE, false) != ProcessedRequest
				.calculateProfileDiffDigest(EXAMPLE_STRING_TWO, false));
	}

	@Test
	public void testRemoveWhitespaces() {
		assertEquals(EXAMPLE_STRING_TWO, ProcessedRequest.removeWhitespaces(EXAMPLE_STRING_ONE));
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

}
