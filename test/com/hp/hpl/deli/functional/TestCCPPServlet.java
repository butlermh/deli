package com.hp.hpl.deli.functional;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.deli.Attribute;
import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.HttpProfileProcessor;
import com.hp.hpl.deli.Profile;
import com.hp.hpl.deli.ProfileProcessor;
import com.hp.hpl.deli.VocabularyException;

public class TestCCPPServlet extends HttpServlet {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	private boolean HTML = true;

	private static ProfileProcessor configuration;

	private static HttpProfileProcessor profileProcessor;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			configuration = new ProfileProcessor(Constants.CONFIG_FILE);
			profileProcessor = new HttpProfileProcessor(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Profile profile = profileProcessor.processRequest(req);
		if (HTML) {
			// HTML
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			printHTML(profile, out);
		} else {
			// WML
			res.setContentType("text/vnd.wap.wml");
			PrintWriter out = res.getWriter();
			printWML(profile, out);
			res = HttpProfileProcessor.addWarningHeader(res,
					HttpProfileProcessor.CONTENT_GENERATION_APPLIED);
		}
	}

	/*
	 * Print a HTML version of the profile.
	 */
	public static void printHTML(Profile theProfile, PrintWriter out) {
		out.println("<HTML>\n<HEAD><TITLE>Device Profile</TITLE></HEAD>\n");
		out.println("<BODY><H1>Device Profile</H1>");
		out.println("<TABLE>");
		out.println("<TR><TD><B>Component</B></TD>");
		out.println("<TD><B>Attribute</B></TD>");
		out.println("<TD><B>Resolution</B></TD>");
		out.println("<TD><B>CollectionType</B></TD>");
		out.println("<TD><B>Type</B></TD>");
		out.println("<TD><B>Value</B></TD></TR>");
		out.println();

		for (int i = 0; i < theProfile.size(); i++) {
			Attribute p = (Attribute) theProfile.get(i);
			out.println("<TR><TD>" + p.getComponent() + "</TD>");
			out.println("<TD>" + p.getName().getNameSpace() + " "
					+ p.getName().getLocalName() + "</TD>");
			try {
			out.println("<TD>" + p.getResolution() + "</TD>");
			out.println("<TD>" + p.getCollectionType() + "</TD>");
			out.println("<TD>" + p.getType() + "</TD>");
			} catch (VocabularyException ve) {
				ve.printStackTrace();
			}
			out.println("<TD>" + p.get() + "</TD></TR>");
		}

		out.println("</TABLE>");
		out.println("</BODY></HTML>");
	}

	// WML version

	/*
	 * Print a HTML version of the profile.
	 */
	public static void printWML(Profile theProfile, PrintWriter out) {
		StringWriter outstr = new StringWriter();
		outstr.write("<?xml version=\"1.0\"?>\n");
		outstr.write("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">\n");
		outstr.write("<wml>\n");
		outstr.write("<card id=\"init\" newcontext=\"true\">\n");

		for (int i = 0; i < theProfile.size(); i++) {
			Attribute p = (Attribute) theProfile.get(i);
			outstr.write("<p>" + p.getComponent() + "<br/>" + "\n");
			outstr.write(p.getName().getLocalName() + "<br/>" + "\n");
			outstr.write(p.get() + "</p>" + "\n");
		}

		outstr.write("</card>\n");
		outstr.write("</wml>\n");

		String str = outstr.getBuffer().toString();
		out.print(str);
	}
}
