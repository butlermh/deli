package com.hp.hpl.deli;

import javax.servlet.http.HttpServletResponse;

/**
 *  This class adds the x-wap-profile-warning header.
 */
public class UAProfCreateHttpResponse {
	/**
	 * This value MUST be included if the content has
	 * not been tailored, and is sent in a representation which is the
	 * only representaiton available in the server.
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
	 * This class contains no data members and only contains
	 * static methods so a constructor is unnecessary.
	 */
	private UAProfCreateHttpResponse() {
	}

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
