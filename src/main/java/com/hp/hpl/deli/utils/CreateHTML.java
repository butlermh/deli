package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Generate a HTML page from the list of all known UAProf profiles.
 */
class CreateHTML {

	/** Map of manufacturers onto URIs */
	private HashMap<String, TreeSet<String>> manufacturers = new HashMap<String, TreeSet<String>>();

	/** The RDF model containing the data on all profiles. */
	private final Model profiles;
	private StringBuffer result = new StringBuffer();
	private HashMap<Resource, Resource> results;

	/**
	 * Constructor.
	 */
	CreateHTML(Model profiles, HashMap<Resource, Resource> results) throws IOException {
		this.profiles = profiles;
		this.results = results;
		String datenewformat = new SimpleDateFormat("dd MMMMM yyyy").format(new Date());
		result.append("<html>\n<head>\n<title>List of UAProfile profiles "
				+ datenewformat
				+ "</title>\n<style TYPE=\"text/css\"><!--tr.odd {\nbackground-color: #fbe7ef;\n}\n"
				+ "--></style>");
		result.append("</head>\n");
		result.append("<body>\n<h1>List of UAProfile profiles " + datenewformat
				+ "</h1>\n");

		getManufacturers();
		printManufacturers();

		result.append("</body>\n</html>");
		UrlUtils.savePage(Constants.PROFILES_OUTPUT_FILE, result);
	}

	/**
	 * Extract all the manufacturer names from the profile data.
	 */
	private void getManufacturers() {
		Resource resource = null;
		ResIterator profilesIter = profiles
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			resource = profilesIter.nextResource();
			Resource m = (Resource) resource.getProperty(DeliSchema.manufacturedBy)
					.getObject();

			if (m.getURI() != null) {
				String manufacturer = m.getURI();
				TreeSet<String> uris = null;
				if (manufacturers.containsKey(manufacturer)) {
					uris = manufacturers.get(manufacturer);
				} else {
					uris = new TreeSet<String>();
				}
				uris.add(m.getURI());
				manufacturers.put(manufacturer, uris);
			}
		}
	}

	/**
	 * Print the profiles sorted by manufacturers.
	 */
	private void printManufacturers() {
		for (String manufacturer : manufacturers.keySet()) {
			result.append("<h2>");
			Resource manufacturerResource = profiles.createResource(manufacturer);
			StmtIterator si = manufacturerResource.listProperties(RDFS.label);
			while (si.hasNext()) {
				Statement s = si.nextStatement();
				result.append(s.getLiteral());
				if (si.hasNext()) {
					result.append(" / ");
				}
			}
			result.append("</h2>\n");
			result.append("<p>\n<table width=\"100%\" border=\"1\"><tbody><tr><td width=\"20%\">Device</td><td width=\"20%\">Release</td><td width=\"40%\">URI</td><td width=\"20%\">Valid?</td></tr>\n");
			for (String manufacturerURL : manufacturers.get(manufacturer)) {
				ResIterator profilesIterBy = profiles.listSubjectsWithProperty(
						DeliSchema.manufacturedBy, profiles.getResource(manufacturerURL));
				TreeSet<String> sortedList = new TreeSet<String>();
				while (profilesIterBy.hasNext()) {
					Resource resource = profilesIterBy.nextResource();
					sortedList.add(resource.getURI());
				}

				for (String profileUri : sortedList) {
					Resource resource = profiles.createResource(profileUri);
					DeviceData device = new DeviceData(resource);

					boolean profileValid = false;
					if (results.containsKey(resource)) {
						Resource valid = results.get(resource);
						if (valid.equals(DeliSchema.Valid)) {
							profileValid = true;
						}
					}

					if (!profileValid) {
						result.append("<tr class=\"odd\">\n");
					} else {
						result.append("<tr>\n");
					}

					result.append("<td>" + device.getDeviceName() + "</td>\n");
					if (device.hasRelease()) {
						String release = device.getRelease();
						result.append("<td>" + release + "</td>");
					} else {
						result.append("<td></td>");
					}

					result.append("<td>\n");
					result.append("<a href=\"" + profileUri + "\">" + profileUri
							+ "</a>\n");
					result.append("</td>");
					
					if (results.containsKey(resource)) {
						Resource valid = results.get(resource);
						if (valid.equals(DeliSchema.Valid)) {
							result.append("<td>VALID</td>");
						} else if (valid.equals(DeliSchema.Invalid)) {
							linkToValidatorReport(profileUri, "INVALID");
						} else if (valid.equals(DeliSchema.Unretrievable)) {
							linkToValidatorReport(profileUri, "UNRETRIEVABLE");
						}
					} else {
						result.append("<td></td>");
					}
					result.append("</tr>\n");
				}
			}
			result.append("</table>\n");
		}
	}
	
	private void linkToValidatorReport(String profileUri, String comment) {
		try {
			URL theURL = new URL(profileUri);
			String filepath = "validator/"
					+ theURL.getHost() + theURL.getFile() +".html";
			result.append("<td><a href=\"" + filepath + "\">");
			result.append(comment);
			result.append("</a></td>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	final static String VALIDATOR_REPORTS =  "target/validator/";
}
