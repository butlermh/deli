package com.hp.hpl.deli;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class processes HTTP requests to produce a list of profile references
 * and profile-diffs. It will process CC/PP requests using HTTP-ex, UAProf
 * requests using WSP, UAProf using W-HTTP, and the Nokia emulators that use a
 * variation on W-HTTP.
 */
class ProcessHttpRequest {
	/** The list of profile references. */
	private Vector<String> referenceVector;

	/** The ordered list of profile-diffs. */
	private Vector<String> diffVector;

	private static Log log = LogFactory.getLog(ProcessHttpRequest.class);

	private String httpexNumericalNamespace = null;

	private int maxNumberProfileDiffs = 0;

	private HashMap<Integer, String> profileDiffMap = new HashMap<Integer, String>();

	private HashMap<Integer, String> profileDiffDigestMap = new HashMap<Integer, String>();

	private void getProfileRefs(HttpServletRequest request) {
		Enumeration<String> profileEnum = request.getHeaders("x-wap-profile");

		if (profileEnum == null || !profileEnum.hasMoreElements()) {
			profileEnum = request.getHeaders("profile");
		}

		// Fallback for HTTP-ex devices
		if (profileEnum == null || !profileEnum.hasMoreElements()) {
			// try opt headers...
			httpexNumericalNamespace = getNumericalNamespace(request.getHeader("opt"));

			// try man headers if opt found nothing...
			if (httpexNumericalNamespace == null) {
				httpexNumericalNamespace = getNumericalNamespace(request.getHeader("man"));
			}

			if (httpexNumericalNamespace != null) {
				// found http-ex namespace headers
				log.info("Found HTTP-ex namespace: " + httpexNumericalNamespace);
				profileEnum = request.getHeaders(httpexNumericalNamespace + "-profile");
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
						int profileDiffNumber = Integer.parseInt(token.substring(0, token.indexOf('-')));

						if (profileDiffNumber > maxNumberProfileDiffs) {
							maxNumberProfileDiffs = profileDiffNumber;
						}

						String profileDiffDigest = token.substring(token.indexOf('-') + 1, token.length());
						log.info("Found profile-diff-digest: " + profileDiffDigest);
						profileDiffDigestMap.put(new Integer(profileDiffNumber), profileDiffDigest);
					}
				}
			}
		}
	}

	private void getProfileDiffs(HttpServletRequest request) {
		Enumeration<String> profileDiffEnum = request.getHeaders("x-wap-profile-diff");

		// Fallback for Nokia devices
		if (profileDiffEnum == null || !profileDiffEnum.hasMoreElements()) {
			profileDiffEnum = request.getHeaders("profile-diff");
		}

		// Fallback for HTTP-ex devices
		if (profileDiffEnum == null || !profileDiffEnum.hasMoreElements()) {
			profileDiffEnum = request.getHeaders(httpexNumericalNamespace + "-profile-diff");
		}

		if (profileDiffEnum == null || !profileDiffEnum.hasMoreElements()) {
			// Fallback for HTTP-ex devices that use a hyphen rather
			// than a colon to separate profile-diff number from request header
			boolean foundProfileDiff = true;
			int profileDiffNumber = 1;

			while (foundProfileDiff) {
				String profileDiffHeader = httpexNumericalNamespace + "-profile-diff-" + profileDiffNumber;
				profileDiffEnum = request.getHeaders(profileDiffHeader);

				if (profileDiffEnum == null || !profileDiffEnum.hasMoreElements()) {
					foundProfileDiff = false;
				} else {
					while (profileDiffEnum.hasMoreElements()) {
						String value = (String) profileDiffEnum.nextElement();
						String profileDiff = value.trim();
						log.info("Found profile diff: " + profileDiff);

						if (profileDiff.charAt(0) == '\"') {
							profileDiffMap.put(new Integer(profileDiffNumber), profileDiff.substring(1, profileDiff
									.length() - 1));
						} else {
							profileDiffMap.put(new Integer(profileDiffNumber), profileDiff);
						}
					}

					profileDiffNumber++;
				}
			}
		}

		if (profileDiffEnum != null) {
			while (profileDiffEnum.hasMoreElements()) {
				String value = (String) profileDiffEnum.nextElement();
				int profileDiffNumber = Integer.parseInt(value.substring(0, value.indexOf(';')));
				String profileDiff = value.substring(value.indexOf(';') + 1, value.length());
				profileDiff.trim();

				if (profileDiff.charAt(0) == '\"') {
					profileDiffMap.put(new Integer(profileDiffNumber), profileDiff.substring(1,
							profileDiff.length() - 1));
				} else {
					profileDiffMap.put(new Integer(profileDiffNumber), profileDiff);
				}
			}
		}
	}

	/**
	 * This method processes the HTTP request to retrieve the profile
	 * references, profile-diff-digests and profile-diffs. It is UAProf
	 * specific.
	 * 
	 * @param request The HTTP request.
	 */
	ProcessHttpRequest(HttpServletRequest request) {

		referenceVector = new Vector<String>();
		diffVector = new Vector<String>();

		log.info("ProcessHttpRequest: Starting to process HTTP request");

		try {
			// first we check if we should prefer localOverRemoteProfiles
			if (Workspace.getInstance().preferLocalOverRemoteProfiles && supportLocalProfiles(request)) {
				log.info("Using local profiles");
				supportLocalProfiles(request);
			} else {
				if (Workspace.getInstance().debugRequestHeaders) {
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

					if (Workspace.getInstance().profileDiffDigestVerification
							&& (profileDiffDigestMap.size() == profileDiffMap.size())) {
						String profileDiffDigest = (String) profileDiffDigestMap.get(r);
						String calculatedProfileDiffDigest = Utils.calculateProfileDiffDigest(profileDiff, Workspace
								.getInstance().normaliseWhitespaceInProfileDiff);

						if (!profileDiffDigest.equals(calculatedProfileDiffDigest)) {
							// The profile-diff-digest does not match the
							// profile-diff, ignore it.
							log.info("ProcessHttpRequest|ValidateProfileDiff: ");
							log.info("ProfileDiffDigest and ProfileDiff do not match");
							log.info("	ProfileDiffDigest:   " + profileDiffDigest);
							log.info("	CalculatedDiffDigest:" + calculatedProfileDiffDigest);
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

				if (Workspace.getInstance().useLocalProfilesIfNoCCPP
						|| (referenceVector.size() + diffVector.size()) == 0) {
					supportLocalProfiles(request);
				}
			}
		} catch (Exception e) {
			log.error("ProcessHttpRequest: Could not process HTTP request", e);
		}
	}

	private void printRequestHeaders(HttpServletRequest request) {
		Enumeration<String> debugHeaders = request.getHeaderNames();
		if (debugHeaders != null) {
			while (debugHeaders.hasMoreElements()) {
				StringBuffer header = new StringBuffer();
				String currentName = debugHeaders.nextElement();
				header.append(currentName + ": ");

				Enumeration<String> debugHeaderValues = request.getHeaders(currentName);
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
	private boolean supportLocalProfiles(HttpServletRequest request) throws Exception {
		if (Workspace.getInstance().useLocalProfilesIfNoCCPP) {
			String userAgent = request.getHeader("user-agent");

			if (userAgent != null) {
				String profileReference = Workspace.getInstance().localProfiles.getReference(userAgent);

				if (profileReference != null) {
					referenceVector.add(profileReference);
					log.info("Useragent " + userAgent + "matches local profile " + profileReference);

					return true;
				}
				log.info("Unable to find useragent " + userAgent + " in local profile database");
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
				numericalNamespace = httpex.substring(httpex.indexOf("ns=") + 3, httpex.length());
			}
		}

		return numericalNamespace;
	}

	/**
	 * @return the referenceVector
	 */
	public Vector<String> getReferenceVector() {
		return referenceVector;
	}

	/**
	 * @return the diffVector
	 */
	public Vector<String> getDiffVector() {
		return diffVector;
	}
}
