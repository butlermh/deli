package com.hp.hpl.deli;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.codec.binary.Base64;

/**
 * This class processes HTTP requests to produce a list of profile references
 * and profile-diffs. It will process CC/PP requests using HTTP-ex, UAProf
 * requests using WSP, UAProf using W-HTTP, and the Nokia emulators that use a
 * variation on W-HTTP.
 */
@SuppressWarnings("unchecked")
class ProcessedRequest {
	/**
	 * The list of profile references.
	 */
	private Vector<String> referenceVector = new Vector<String>();

	/**
	 * The ordered list of profile-diffs.
	 */
	private Vector<String> diffVector = new Vector<String>();

	private static Log log = LogFactory.getLog(ProcessedRequest.class);
	private String httpexNumericalNamespace = null;
	private int maxNumberProfileDiffs = 0;
	private HashMap<Integer, String> profileDiffMap = new HashMap<Integer, String>();
	private HashMap<Integer, String> profileDiffDigestMap = new HashMap<Integer, String>();

	/**
	 * Debug request headers?
	 */
	private boolean debugRequestHeaders = false;

	/**
	 * Is whitespace normalisation of the profile-diff prior to calculating the
	 * profile-diff-digest turned on?
	 */
	private boolean normaliseWhitespaceInProfileDiff = false;

	/** Is profile-diff-digest verification turned on? */
	private boolean profileDiffDigestVerification = false;

	/** Should remote profiles be ignored if there is a local one? */
	private boolean preferLocalOverRemoteProfiles = false;

	/**
	 * Use the local profiles database if there is no CC/PP information in the
	 * request?
	 */
	private boolean useLocalProfilesIfNoCCPP = true;

	private LocalProfiles localProfiles;

	/**
	 * This method processes the HTTP request to retrieve the profile
	 * references, profile-diff-digests and profile-diffs. It is UAProf
	 * specific.
	 * 
	 * @param workspace The DELI workspace.
	 * @param localProfiles The locally cached profiles.
	 * @param request The HTTP request.
	 */

	ProcessedRequest(DeliConfiguration workspace, LocalProfiles localProfiles,
			HttpServletRequest request) {
		log.info("ProcessHttpRequest: Starting to process HTTP request");
		this.localProfiles = localProfiles;
		normaliseWhitespaceInProfileDiff = workspace.get(
				DeliSchema.normaliseWhitespaceInProfileDiff,
				normaliseWhitespaceInProfileDiff);
		profileDiffDigestVerification = workspace.get(
				DeliSchema.profileDiffDigestVerification,
				profileDiffDigestVerification);
		preferLocalOverRemoteProfiles = workspace.get(
				DeliSchema.preferLocalOverRemoteProfiles,
				preferLocalOverRemoteProfiles);
		useLocalProfilesIfNoCCPP = workspace.get(
				DeliSchema.useLocalProfilesIfNoCCPP, useLocalProfilesIfNoCCPP);
		debugRequestHeaders = workspace.get(DeliSchema.debugRequestHeaders,
				debugRequestHeaders);

		try {
			// first we check if we should prefer localOverRemoteProfiles
			if (preferLocalOverRemoteProfiles && supportLocalProfiles(request)) {
				log.info("Using local profiles");
				supportLocalProfiles(request);
			} else {
				if (debugRequestHeaders) {
					printRequestHeaders(request);
				}

				getProfileRefs(request);
				getProfileDiffs(request);

				if (profileDiffDigestMap.size() != profileDiffMap.size()) {
					log.info("ProcessHttpRequest: ");
					log.info("The number of profile-diff-digests and profile-diffs do not match");
				}

				for (int i = 0; i < profileDiffMap.size(); i++) {
					Integer r = new Integer(i + 1);
					String profileDiff = (String) profileDiffMap.get(r);

					if (profileDiffDigestVerification
							&& (profileDiffDigestMap.size() == profileDiffMap
									.size())) {
						String profileDiffDigest = (String) profileDiffDigestMap
								.get(r);
						String calculatedProfileDiffDigest = calculateProfileDiffDigest(
								profileDiff, normaliseWhitespaceInProfileDiff);

						if (!profileDiffDigest
								.equals(calculatedProfileDiffDigest)) {
							// The profile-diff-digest does not match the
							// profile-diff, ignore it.
							log.info("ProcessHttpRequest|ValidateProfileDiff: ");
							log.info("ProfileDiffDigest and ProfileDiff do not match");
							log.info("	ProfileDiffDigest:   "
									+ profileDiffDigest);
							log.info("	CalculatedDiffDigest:"
									+ calculatedProfileDiffDigest);
						} else {
							diffVector.add(profileDiff);
						}
					} else {
						diffVector.add(profileDiff);
					}
				}

				if ((referenceVector.size() + diffVector.size()) == 0) {
					log.info("ProcessHttpRequest: No headers found");
				}

				if (useLocalProfilesIfNoCCPP
						|| (referenceVector.size() + diffVector.size()) == 0) {
					supportLocalProfiles(request);
				}
			}
		} catch (Exception e) {
			log.error("ProcessHttpRequest: Could not process HTTP request", e);
		}
	}

	private void getProfileRefs(HttpServletRequest request) {
		Enumeration<String> profileEnum = request.getHeaders("x-wap-profile");

		if (profileEnum == null || !profileEnum.hasMoreElements()) {
			profileEnum = request.getHeaders("profile");
		}

		// Fallback for HTTP-ex devices
		if (profileEnum == null || !profileEnum.hasMoreElements()) {
			// try opt headers...
			httpexNumericalNamespace = getNumericalNamespace(request
					.getHeader("opt"));

			// try man headers if opt found nothing...
			if (httpexNumericalNamespace == null) {
				httpexNumericalNamespace = getNumericalNamespace(request
						.getHeader("man"));
			}

			if (httpexNumericalNamespace != null) {
				// found http-ex namespace headers
				log.info("Found HTTP-ex namespace: " + httpexNumericalNamespace);
				profileEnum = request.getHeaders(httpexNumericalNamespace
						+ "-profile");
			}
		}

		if (profileEnum != null) {
			while (profileEnum.hasMoreElements()) {
				String value = (String) profileEnum.nextElement();
				StringTokenizer str = new StringTokenizer(value, "\",");

				while (str.hasMoreTokens()) {
					String token = str.nextToken();

					if (token.startsWith("http")) {
						log.info("Found profile reference: " + token);
						referenceVector.add(token);
					} else if (token.indexOf('-') > 0) {
						int profileDiffNumber = Integer.parseInt(token
								.substring(0, token.indexOf('-')));

						if (profileDiffNumber > maxNumberProfileDiffs) {
							maxNumberProfileDiffs = profileDiffNumber;
						}

						String profileDiffDigest = token.substring(
								token.indexOf('-') + 1, token.length());
						log.info("Found profile-diff-digest: "
								+ profileDiffDigest);
						profileDiffDigestMap.put(
								new Integer(profileDiffNumber),
								profileDiffDigest);
					}
				}
			}
		}
	}

	private void getProfileDiffs(HttpServletRequest request) {
		Enumeration<String> profileDiffEnum = request
				.getHeaders("x-wap-profile-diff");

		// Fallback for Nokia devices
		if (profileDiffEnum == null || !profileDiffEnum.hasMoreElements()) {
			profileDiffEnum = request.getHeaders("profile-diff");
		}

		// Fallback for HTTP-ex devices
		if (profileDiffEnum == null || !profileDiffEnum.hasMoreElements()) {
			profileDiffEnum = request.getHeaders(httpexNumericalNamespace
					+ "-profile-diff");
		}

		if (profileDiffEnum == null || !profileDiffEnum.hasMoreElements()) {
			// Fallback for HTTP-ex devices that use a hyphen rather
			// than a colon to separate profile-diff number from request
			// header
			boolean foundProfileDiff = true;
			int profileDiffNumber = 1;

			while (foundProfileDiff) {
				String profileDiffHeader = httpexNumericalNamespace
						+ "-profile-diff-" + profileDiffNumber;
				profileDiffEnum = request.getHeaders(profileDiffHeader);

				if (profileDiffEnum == null
						|| !profileDiffEnum.hasMoreElements()) {
					foundProfileDiff = false;
				} else {
					while (profileDiffEnum.hasMoreElements()) {
						String value = (String) profileDiffEnum.nextElement();
						String profileDiff = value.trim();
						log.info("Found profile diff: " + profileDiff);

						if (profileDiff.charAt(0) == '\"') {
							profileDiffMap.put(
									new Integer(profileDiffNumber),
									profileDiff.substring(1,
											profileDiff.length() - 1));
						} else {
							profileDiffMap.put(new Integer(profileDiffNumber),
									profileDiff);
						}
					}

					profileDiffNumber++;
				}
			}
		}

		if (profileDiffEnum != null) {
			while (profileDiffEnum.hasMoreElements()) {
				String value = (String) profileDiffEnum.nextElement();
				int profileDiffNumber = Integer.parseInt(value.substring(0,
						value.indexOf(';')));
				String profileDiff = value.substring(value.indexOf(';') + 1,
						value.length());
				profileDiff.trim();

				if (profileDiff.charAt(0) == '\"') {
					profileDiffMap.put(new Integer(profileDiffNumber),
							profileDiff.substring(1, profileDiff.length() - 1));
				} else {
					profileDiffMap.put(new Integer(profileDiffNumber),
							profileDiff);
				}
			}
		}
	}

	private void printRequestHeaders(HttpServletRequest request) {
		Enumeration<String> debugHeaders = request.getHeaderNames();
		if (debugHeaders != null) {
			while (debugHeaders.hasMoreElements()) {
				StringBuffer header = new StringBuffer();
				String currentName = debugHeaders.nextElement();
				header.append(currentName + ": ");

				Enumeration<String> debugHeaderValues = request
						.getHeaders(currentName);
				while (debugHeaderValues.hasMoreElements()) {
					header.append(debugHeaderValues.nextElement() + " ");
				}
				log.info(header.toString());
			}
		}
	}

	/**
	 * This method provides support for local profiles.
	 * 
	 * @param request The HTTP Request
	 * @return Did it match the request to a local profile?
	 * @throws Exception
	 */
	private boolean supportLocalProfiles(HttpServletRequest request)
			throws Exception {
		if (useLocalProfilesIfNoCCPP) {
			String userAgent = request.getHeader("user-agent");

			if (userAgent != null) {
				String profileReference = localProfiles.getReference(userAgent);

				if (profileReference != null) {
					referenceVector.add(profileReference);
					log.info("Useragent " + userAgent
							+ "matches local profile " + profileReference);

					return true;
				}
				log.info("Unable to find useragent " + userAgent
						+ " in local profile database");
			}

			return false;
		}
		log.info("ProcessHttpRequest: Local profile support disabled");

		return false;
	}

	/**
	 * This function retrieves a numerical namespace from a request header field
	 * 
	 * @return A vector of profile diffs.
	 */
	private String getNumericalNamespace(String httpex) {
		String numericalNamespace = null;

		if (httpex != null) {
			if (httpex.indexOf("ns=") > 0) {
				numericalNamespace = httpex.substring(
						httpex.indexOf("ns=") + 3, httpex.length());
			}
		}

		return numericalNamespace;
	}

	/**
	 * This method calculates a profile-diff-digest. It does this by taking a
	 * profile-diff as a string and calculating the corresponding
	 * profile-diff-digest by normalising any whitespace, applying the MD5
	 * algorithm and then converting this to a string using BASE64 encoding.
	 * 
	 * @param profileDiff The profile-diff.
	 * @param normaliseWhitespace Turn whitespace normalising on or off.
	 * @return The profile-diff-digest.
	 * @throws NoSuchAlgorithmException Thrown if there is a problem making the
	 *             message digest.
	 */
	static String calculateProfileDiffDigest(final String profileDiff,
			boolean normaliseWhitespace) throws NoSuchAlgorithmException {
		String normalizedDiff = normaliseWhitespace ? removeWhitespaces(profileDiff)
				: profileDiff;
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(normalizedDiff.getBytes());
		return new String(Base64.encodeBase64(md.digest()));
	}

	/**
	 * This method removes whitespaces in the profile-diff according to the
	 * following two rules:
	 * <UL>
	 * <LI>Leading and trailing white spaces are eliminated (white space as
	 * defined in RFC 2616 section 2.2)</LI>
	 * <LI>All non-trailing or non-leading linear white space (LWS) contained in
	 * the profile description, including line folding of multiple HTTP header
	 * lines, is replaced with one single space (SP) character. Note: This
	 * implies that property values, represented as XML attributes or XML
	 * element character data, MUST be adhering to white space compression as
	 * mandated in RFC 2616 section 2.2.</LI>
	 * </UL>
	 * 
	 * @param diff The profile-diff.
	 * @return The normalised profile-diff.
	 */
	static String removeWhitespaces(String diff) {
		if (diff == null) {
			return null;
		}

		// replace multiple whitespace chars with a single space
		// and remove leading or trailing whitespaces
		diff.replaceAll("[ \n\r\t]", " ").replaceAll(" *", " ")
				.replaceAll("^ *", "").replaceAll(" *$", "");

		return diff;
	}

	/**
	 * @return The vector of profile references.
	 */
	public Vector<String> getReferenceVector() {
		return referenceVector;
	}

	/**
	 * @return The vector of profile-diffs.
	 */
	public Vector<String> getDiffVector() {
		return diffVector;
	}
}
