package com.hp.hpl.deli;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A processor that can deal with HttpServletRequests and add the appropriate fields to HttpServletResponses.
 */
public class HttpProfileProcessor {
	/** Logging. */
	private static Log log = LogFactory.getLog(HttpProfileProcessor.class);

	private ProfileCache profileCache = null;

	private LocalProfiles localProfiles;

	private ProfileProcessor configuration;

	HttpProfileProcessor(ProfileProcessor configuration,
			LocalProfiles localProfiles, ProfileCache profileCache) {
		this.configuration = configuration;
		this.localProfiles = localProfiles;
		this.profileCache = profileCache;
	}
	
	public HttpProfileProcessor(ProfileProcessor configuration) throws IOException {
		this(configuration, new LocalProfiles(configuration), new ProfileCache(configuration));
	}

	public Profile processRequest(HttpServletRequest request) throws IOException {
		ProcessedRequest tr = new ProcessedRequest(configuration.getWorkspace(), localProfiles, request);
		return processProfile(tr);
	}

	/**
	 * Create a new profile from a processed request.
	 * 
	 * @param request The HTTP request.
	 */
	Profile processProfile(ProcessedRequest request) throws IOException {
		Vector<String> profileReferences = request.getReferenceVector();
		Vector<Attribute> unresolved = new Vector<Attribute>();
		for (String s : profileReferences) {
			log.info("Retrieving profile " + s);
			unresolved.addAll(profileCache.get(this, s).get());
		}
		if (request.getDiffVector().size() > 0) {
			for (String s : request.getDiffVector()) {
				ProcessProfile profile = new ProcessProfile(configuration, false, new StringReader(s)); 
				unresolved.addAll(profile.getProfileAttributes());
			}
		}
		return new Profile(configuration.getVocabulary(), unresolved);
	}
	
	/**
	 * This value MUST be included if the content has
	 * not been tailored, and is sent in a representation which is the
	 * only representation available in the server.
	 */
	public static final int NOT_APPLIED = 200;

	/**
	 * MUST be included if the included content has been selected
	 * from one of the representations available.
	 */
	public static final int CONTENT_SELECTION_APPLIED = 201;

	/**
	 * MUST be included if the content has been tailored or generated
	 * as a result of applying the included profile.
	 */
	public static final int CONTENT_GENERATION_APPLIED = 202;

	/**
	 * MUST be added by an intermediate proxy if it applies any
	 * transformation changing the content-coding based on the CPI
	 * data.
	 */
	public static final int TRANSFORMATION_APPLIED = 203;

	/**
	 * Indicates that the entity sending this warning code does not
	 * support UAProf.
	 */
	public static final int NOT_SUPPORTED = 500;

	/**
	 * This method adds the x-wap-profile-warning header to the response
	 * header.
	 *
	 *@param        res        The HttpServletResponse object.
	 *@param        warning        The warning code.
	 *@return        The modified HttpServletResponse object.
	 */
	public static HttpServletResponse addWarningHeader(HttpServletResponse res, int warning) {
		res.addHeader("x-wap-profile-warning", new Integer(warning).toString());
		return res;
	}
}
