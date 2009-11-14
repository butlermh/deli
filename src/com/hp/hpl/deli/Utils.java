package com.hp.hpl.deli;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Encoder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Utils {

	private final static String USER_AGENT = "User-Agent";

	private final static String USER_AGENT_STRING = "Mozilla/5.0 (X11; U; Linux i686; rv:1.7.3) Gecko/20041020 Firefox/0.10.1";


	public static String getPropertyUri(Resource resource, Property property) {
		return (resource.hasProperty(property)) ? ((Resource) resource.getProperty(property).getObject()).getURI() : null;
	}

	public static String getPropertyString(Resource resource, Property property) {
		return (resource.hasProperty(property)) ? resource.getProperty(property).getLiteral().toString() : null;
	}

	/** Logging support. */
	private static Log log = LogFactory.getLog(Utils.class);

	/**
	 * This method calculates a profile-diff-digest. It does this by taking a
	 * profile-diff as a string and calculating the corresponding
	 * profile-diff-digest by normalising any whitespace, applying the MD5
	 * algorithm and then converting this to a string using BASE64 encoding.
	 *
	 * @param profileDiff
	 *            The profile-diff.
	 * @param normaliseWhitespace
	 *            Turn whitespace normalising on or off.
	 * @return The profile-diff-digest.
	 */
	public static String calculateProfileDiffDigest(String profileDiff, boolean normaliseWhitespace) throws Exception {
		if (normaliseWhitespace) {
			profileDiff = removeWhitespaces(profileDiff);
		}
		BASE64Encoder encoder = new BASE64Encoder();
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(profileDiff.getBytes());
		return encoder.encode(md.digest());
	}

	/**
	 * This method removes whitespaces in the profile-diff according to the
	 * following two rules:
	 * <UL>
	 * <LI>Leading and trailing white spaces are eliminated (white space as
	 * defined in RFC 2616 section 2.2)</LI>
	 * <LI>All non-trailing or non-leading linear white space (LWS) contained
	 * in the profile description, including line folding of multiple HTTP
	 * header lines, is replaced with one single space (SP) character. Note:
	 * This implies that property values, represented as XML attributes or XML
	 * element character data, MUST be adhering to white space compression as
	 * mandated in RFC 2616 section 2.2.</LI>
	 * </UL>
	 *
	 * @param diff
	 *            The profile-diff.
	 * @return The normalised profile-diff.
	 */
	public static String removeWhitespaces(String diff) {
		if (diff == null) {
			return null;
		}

		// replace multiple whitespace chars with a single space
		diff = diff.replaceAll("[ \n\r\t][ \n\r\t]", " ");

		while (diff.lastIndexOf("  ") != -1) {
			diff = diff.replaceAll("[ \n\r\t][ \n\r\t]", " ");
		}

		// remove leading whitespace
		diff = diff.replaceAll("^ *", "");

		// remove trailing whitespace
		diff = diff.replaceAll(" *$", "");

		return diff;
	}

	public static Model loadModel(String filename) {
		Model model = ModelFactory.createDefaultModel();
		try {
			 model.read(Workspace.getInstance().getResource(filename), "", "N3");
		} catch (Exception fe) {
			log.error("Could not load " + filename);
		}
		return model;
	}

	/**
	 * Get the contents of a particular URL as a String.
	 *
	 * @param pageUri the URL.
	 * @return the contents as a String.
	 * @throws Exception
	 */
	public static String getURL(String pageUri) throws Exception {
		StringBuffer input = new StringBuffer();
		URL u = new URL(pageUri);
		URLConnection conn = u.openConnection();
		// have to fake the user agent to get results back from google
		conn.setRequestProperty(USER_AGENT, USER_AGENT_STRING);
		conn.connect();

		InputStream s = conn.getInputStream();
		int ch;
		while ((ch = s.read()) != -1) {
			input.append((char) ch);
		}
		s.close();
		return input.toString();
	}
}