/**
 * 
 */
package com.hp.hpl.deli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class ProcessHttpRequestTest extends TestCase {

	/** Logging support. */
	private static Log log = LogFactory.getLog(ProcessHttpRequestTest.class);
	
	/**
	 * This flag removes carriage-returns in profile diffs overcoming a bug in
	 * Tomcat 3.2.1 and some other servers.
	 */
	static boolean noCarriageReturnsInProfileDiffs = true;
	
	static final String PROFILE = "profiles/testprofiles/Ericsson_T39.rdf"; 

	/**
	 * @param name
	 */
	public ProcessHttpRequestTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * No UAProf data
	 */
	public void testOne() throws Exception {
		String[] param = {};
		MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
		ProcessHttpRequest phr = new ProcessHttpRequest((HttpServletRequest) mockRequest);
		assertEquals(0, phr.getReferenceVector().size());
		assertEquals(0, phr.getDiffVector().size());
	}

	/**
	 * Try a profile ref containing a profile-diff - this should return nothing
	 */
	public void testTwo() throws Exception {
		TempProfileDiff p1 = new TempProfileDiff(PROFILE, noCarriageReturnsInProfileDiffs, false);
		String[] param = { "x-wap-profile: \"1-" + p1.profileDiffDigest + "\"\n" };
		MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
		ProcessHttpRequest phr = new ProcessHttpRequest((HttpServletRequest) mockRequest);
		assertEquals(0, phr.getReferenceVector().size());
		assertEquals(0, phr.getDiffVector().size());
	}

	/**
	 * Valid profile ref
	 */
	public void testThree() {
		String[] param = { "x-wap-profile: \"" + TestConstants.WEB_PROFILE_URI + "\n" };
		MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
		ProcessHttpRequest phr = new ProcessHttpRequest((HttpServletRequest) mockRequest);
		assertEquals(1, phr.getReferenceVector().size());
		assertEquals(0, phr.getDiffVector().size());
	}

//	public void testFour() {
//		TempProfileDiff p1 = new TempProfileDiff(PROFILE, noCarriageReturnsInProfileDiffs, false);
//		String[] param = {"x-wap-profile: \"1-" + p1.profileDiff + "\"\n",
//				"x-wap-profile-diff:1;\"" + p1.profileDiffDigest + "\"\n"};
//		MockHttpServletRequest mockRequest = new MockHttpServletRequest(param);
//		ProcessHttpRequest phr = new ProcessHttpRequest((HttpServletRequest) mockRequest);
//		assertEquals(1, phr.getReferenceVector().size());
//		assertEquals(0, phr.getDiffVector().size());
//	}
	//	
	// public void testFive() {
	// TempProfileDiff p1 = new TempProfileDiff(args[2],
	//                noCarriageReturnsInProfileDiffs, false);
	//        request.append("x-wap-profile: \"" + args[1] + "\", \"1-" +
	//            p1.profileDiffDigest + "\"\n");
	//        request.append("x-wap-profile-diff:1;\"" + p1.profileDiff + "\"\n");
	//	}
	//	
	//	public void testSix() {
	//        TempProfileDiff p2 = new TempProfileDiff(args[3],
	//                noCarriageReturnsInProfileDiffs, false);
	//        request.append("x-wap-profile: \"" + args[1] + "\", \"1-" +
	//            p1.profileDiffDigest + "\", ");
	//        request.append("\"2-" + p2.profileDiffDigest + "\"\n");
	//	}
	//	
	//	public void testSeven() {
	//        request.append("x-wap-profile-diff:3;\"" + p1.profileDiff + "\"\n");
	//        request.append("x-wap-profile-diff:8;\"" + p2.profileDiff + "\"\n");
	//	}
	//	
	//	public void testEight() {
	//        request.append("x-wap-profile-diff:1;\"" + p1.profileDiff + "\"\n");
	//        request.append("x-wap-profile-diff:2;\"" + p2.profileDiff + "\"\n");
	//	}

	class TempProfileDiff {
		public String profileDiff;

		public String profileDiffDigest;

		TempProfileDiff(String filename, boolean noCarriageReturnsInProfileDiffs, boolean broken) {
			try {
				profileDiff = readFile(filename);

				if (broken) {
					profileDiffDigest = Utils.calculateProfileDiffDigest(profileDiff.toString()
							+ "another random string to corrupt the digest value!", true);
				} else {
					profileDiffDigest = Utils.calculateProfileDiffDigest(profileDiff.toString(), true);
				}

				if (noCarriageReturnsInProfileDiffs) {
					profileDiff = Utils.removeWhitespaces(profileDiff.toString());
				}
			} catch (Exception e) {
				log.error(e);
			}
		}

		public String readFile(String filename) throws IOException {
			StringBuffer file = new StringBuffer();
			BufferedReader fileIn = new BufferedReader(new FileReader(filename));
			String str;

			while ((str = fileIn.readLine()) != null) {
				file.append(str);
			}

			return file.toString();
		}
	}

	public class MockHttpServletRequest implements HttpServletRequest {

		private Vector<String> headers = null;

		private Map<String, Vector<String>> headerMap = null;

		public MockHttpServletRequest(String[] values) {
			headerMap = new HashMap<String, Vector<String>>();
			headers = new Vector<String>();
			for (String header : values) {
				headers.add(header);
				String headerName = header.substring(0, header.indexOf(":"));
				String value = header.substring(header.indexOf(":") + 1, header.length());
				System.out.println("header: "+headerName +", value: " + value);
				Vector<String> v = headerMap.containsKey(headerName) ? headerMap.get(headerName) : new Vector<String>();
				v.add(value);
				headerMap.put(headerName, v);
			}
		}

		public Enumeration<String> getHeaders() {
			return headers.elements();
		}

		public String getAuthType() {
			return null;
		}

		public String getContextPath() {
			return null;
		}

		public Cookie[] getCookies() {
			return null;
		}

		public long getDateHeader(String arg0) {
			return 0;
		}

		public String getHeader(String arg0) {
			return headerMap.containsKey(arg0) ? headerMap.get(arg0).firstElement() : null;
		}

		public Enumeration getHeaderNames() {
			Vector<String> v = new Vector<String>();
			for (String header : headers) {
				v.add(header.substring(0, header.indexOf(":")));
			}
			return v.elements();
		}

		public Enumeration getHeaders(String arg0) {
			return headerMap.containsKey(arg0) ? headerMap.get(arg0).elements() : null;
		}

		public int getIntHeader(String arg0) {
			return 0;
		}

		public String getMethod() {
			return null;
		}

		public String getPathInfo() {
			return null;
		}

		public String getPathTranslated() {
			return null;
		}

		public String getQueryString() {
			return null;
		}

		public String getRemoteUser() {
			return null;
		}

		public String getRequestURI() {
			return null;
		}

		public StringBuffer getRequestURL() {
			return null;
		}

		public String getRequestedSessionId() {
			return null;
		}

		public String getServletPath() {

			return null;
		}

		public HttpSession getSession() {
			return null;
		}

		public HttpSession getSession(boolean arg0) {
			return null;
		}

		public Principal getUserPrincipal() {
			return null;
		}

		public boolean isRequestedSessionIdFromCookie() {
			return false;
		}

		public boolean isRequestedSessionIdFromURL() {
			return false;
		}

		public boolean isRequestedSessionIdFromUrl() {
			return false;
		}

		public boolean isRequestedSessionIdValid() {
			return false;
		}

		public boolean isUserInRole(String arg0) {
			return false;
		}

		public Object getAttribute(String arg0) {
			return null;
		}

		public Enumeration getAttributeNames() {
			return null;
		}

		public String getCharacterEncoding() {
			return null;
		}

		public int getContentLength() {
			return 0;
		}

		public String getContentType() {
			return null;
		}

		public ServletInputStream getInputStream() throws IOException {
			return null;
		}

		public String getLocalAddr() {
			return null;
		}

		public String getLocalName() {
			return null;
		}

		public int getLocalPort() {
			return 0;
		}

		public Locale getLocale() {
			return null;
		}

		public Enumeration getLocales() {
			return null;
		}

		public String getParameter(String arg0) {
			return null;
		}

		public Map getParameterMap() {
			return null;
		}

		public Enumeration getParameterNames() {
			return null;
		}

		public String[] getParameterValues(String arg0) {
			return null;
		}

		public String getProtocol() {
			return null;
		}

		public BufferedReader getReader() throws IOException {
			return null;
		}

		public String getRealPath(String arg0) {
			return null;
		}

		public String getRemoteAddr() {
			return null;
		}

		public String getRemoteHost() {
			return null;
		}

		public int getRemotePort() {
			return 0;
		}

		public RequestDispatcher getRequestDispatcher(String arg0) {
			return null;
		}

		public String getScheme() {
			return null;
		}

		public String getServerName() {
			return null;
		}

		public int getServerPort() {
			return 0;
		}

		public boolean isSecure() {
			return false;
		}

		public void removeAttribute(String arg0) {
		}

		public void setAttribute(String arg0, Object arg1) {
		}

		public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		}
	}

}
