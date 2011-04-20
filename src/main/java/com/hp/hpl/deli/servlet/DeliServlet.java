package com.hp.hpl.deli.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeliServlet extends HttpServlet {
	private static final long serialVersionUID = 5584077528313752693L;

	private static final String TEXT = "RDF";

	private static final String URI = "URI";

	// The email address for bug reports
	private static final String MAIL_TO = "markhenrybutler@gmail.com";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/*
	 * Servlet's doGet info method - supported for testing
	 * 
	 * @param req the request
	 * 
	 * @param res the response
	 * 
	 * @throws ServletException, IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String sRDF = req.getParameter(TEXT);
		String sURI = req.getParameter(URI);

		sRDF = (sRDF == null) ? "" : sRDF;
		sURI = (sURI == null) ? "" : sURI;

		process(req, res, sRDF, sURI);
	}

	/*
	 * Servlet's doPost method
	 * 
	 * @param req the request
	 * 
	 * @param res the response
	 * 
	 * @throws ServletException, IOException,
	 * java.io.UnsupportedEncodingException
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		String sRDF = req.getParameter(TEXT);
		String sURI = req.getParameter(URI);

		sRDF = (sRDF == null) ? "" : sRDF;
		sURI = (sURI == null) ? "" : sURI;

		try {
			process(req, res, sRDF, sURI);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

	/*
	 * Print the document's header info
	 * 
	 * @param out the servlet's output Writer
	 */
	private void printDocumentHeader(PrintWriter out) {
		try {

			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
					+ "<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'>"
					+ "<head>"
					+ "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
					+ "<title>W3C RDF Validation Results</title>"
					+ "<link rel='stylesheet' href='http://validator.w3.org/base.css' type='text/css' />"
					+ "<style type='text/css'> #menu li { color: white;}"
					+ "  td {"
					+ "    background:#EEEEEE;"
					+ "    font-family:'courier new',courier,serif;"
					+ "    border-width: 1px;"
					+ "    border-color: black;"
					+ "  }"
					+ "</style>\n"
					+ "</head><body><div id='banner'><h1 id='title'>"
					+ "<a href='http://www.w3.org/'><img height='48' alt='W3C' id='logo' src='http://www.w3.org/Icons/WWW/w3c_home_nb' /></a>"
					+ "<a href='http://www.w3.org/RDF/' title='RDF (Resource Description Framework)'><img src='http://www.w3.org/RDF/icons/rdf_powered_button.48' alt='RDF' /></a>"
					+ "Validation Service</h1> </div>"
					+ "<ul class='navbar' id='menu'>"
					+ "<li><span class='hideme'><a href='#skip' accesskey='2' title='Skip past navigation to main part of page'>Skip Navigation</a> |</span>"
					+ "<strong><a href='/RDF/Validator/'>Home</a></strong></li>"
					+ "<li><a href='documentation' accesskey='3' title='Documentation for this Service'>Documentation</a></li>"
					+ "<li><a href='documentation#feedback' accesskey='4' title='How to provide feedback on this service'>Feedback</a></li>"
					+ "</ul><div id='main'>" + "<div id='jumpbar'>Jump To:" + "<ul>"
					+ "<li><a href='#source'>Source</a></li>" + "<li><a href='#triples'>Triples</a></li>"
					+ "<li><a href='#messages'>Messages</a></li>" + "<li><a href='#graph'>Graph</a></li>"
					+ "<li><a href='#feedback'>Feedback</a></li>"
					+ "<li><a href='http://www.w3.org/RDF/Validator/'>Back to Validator Input</a></li>"
					+ "</ul></div><!-- jumpbar -->");

		} catch (Exception e) {
			System.err.println("Exception (printDocumentHeader): " + e.getMessage());
		}
	}

	/*
	 * Print the document's footer info
	 * 
	 * @param out the servlet's output Writer
	 * 
	 * @param rdf the RDF code
	 */
	private void printDocumentFooter(PrintWriter out, String rdf) {
		try {

			String s;

			s = "<hr title='Problem reporting' />"
					+ "<h3><a name='feedback' id='feedback'>Feedback</a></h3>"
					+ "<p>If you suspect the parser is in error, please enter an explanation below and then press the <b>Submit problem report</b> button, to mail the report (and listing) to <i>"
					+ MAIL_TO + "</i></p>" + "<form enctype='text/plain' method='post' action='mailto:" + MAIL_TO
					+ "'>" + "<textarea cols='60' rows='4' name='report'></textarea>";
			out.println(s);

			out.println("<input type='hidden' name='RDF' value=\"&lt;?xml version=&quot;1.0&quot;&gt;");

			// The listing is being passed as a parameter so the '<'
			// and '"' characters must be replaced with &lt; and &quot,
			// respectively
			if (rdf != null) {
				String s1;
				s1 = rdf.replaceAll("&", "&amp;");
				s1 = s1.replaceAll("<", "&lt;");
				s1 = s1.replaceAll(">", "&gt;");
				s1 = s1.replaceAll("\"", "&quot;");
				out.println(s1);
			}
			out.println("\" />");

			out.println("<input type='submit' value='Submit problem report' />" + "</form>");

			out.println("</div><!-- main --></body></html>");

		} catch (Exception e) {
			System.err.println("Exception (printDocumentFooter): " + e.getMessage());
		}
	}

	private void process(HttpServletRequest req, HttpServletResponse res, String sRDF, String sURI)
			throws ServletException, IOException {

		res.setContentType("text/html;charset=utf-8");
		PrintWriter out = res.getWriter();
		printDocumentHeader(out);
		printDocumentFooter(out, sRDF);
	}

}
