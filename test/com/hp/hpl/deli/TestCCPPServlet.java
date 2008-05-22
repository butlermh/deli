package com.hp.hpl.deli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.deli.Profile;
import com.hp.hpl.deli.ProfileAttribute;
import com.hp.hpl.deli.Workspace;

public class TestCCPPServlet extends HttpServlet {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	Workspace workspace;

	private ServletContext servletContext;

	private boolean HTML = true;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			servletContext = config.getServletContext();
			Workspace.getInstance().configure(servletContext, Constants.CONFIG_FILE);
		} catch (Exception e) {
			try {
				Workspace.getInstance().configure((ServletContext) null, Constants.CONFIG_FILE);
			} catch (Exception f) {
				System.out.println(f.toString());
			}
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (HTML) {
			// HTML
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			Profile myprofile = new Profile(req);
			printHTML(myprofile, out);
		} else {
			// WML
			res.setContentType("text/vnd.wap.wml");
			PrintWriter out = res.getWriter();
			Profile myprofile = new Profile(req);
			printWML(myprofile, out);
			res = UAProfCreateHttpResponse.addWarningHeader(res, UAProfCreateHttpResponse.CONTENT_GENERATION_APPLIED);
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
			ProfileAttribute p = (ProfileAttribute) theProfile.get(i);
			out.println("<TR><TD>"
					+ Workspace.getInstance().vocabulary.getAttributeProperty(p.getName(), "ccppComponent") + "</TD>");
			out.println("<TD>" + p.getUri() + "" + p.getAttribute() + "</TD>");
			out.println("<TD>" + p.getResolution() + "</TD>");
			out.println("<TD>" + p.getCollectionType() + "</TD>");
			out.println("<TD>" + p.getType() + "</TD>");
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
		outstr
				.write("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">\n");
		outstr.write("<wml>\n");
		outstr.write("<card id=\"init\" newcontext=\"true\">\n");

		for (int i = 0; i < theProfile.size(); i++) {
			ProfileAttribute p = (ProfileAttribute) theProfile.get(i);
			outstr.write("<p>" + p.getComponent() + "<br/>" + "\n");
			outstr.write(p.getAttribute() + "<br/>" + "\n");
			outstr.write(p.get() + "</p>" + "\n");
		}

		outstr.write("</card>\n");
		outstr.write("</wml>\n");

		String str = outstr.getBuffer().toString();
		out.print(str);
	}
}
