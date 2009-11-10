package com.hp.hpl.deli.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.Utils;
import com.hp.hpl.deli.Workspace;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

// REVIEWED 10/04/08

/**
 * Generate a HTML page from the list of all known UAProf profiles.
 */
public class CreateHTML extends Utils {

	/** Map of manufacturers onto URIs */
	private HashMap<String, HashSet<String>> manufacturers = new HashMap<String, HashSet<String>>();

	/** The RDF model containing the data on all profiles. */
	private Model profiles = null;
	
	private StringBuffer result = new StringBuffer();

	public static void main(String[] args) {
		new CreateHTML();
	}

	/**
	 * Constructor.
	 */
	public CreateHTML() {
		Workspace.getInstance().configure(null, Constants.VALIDATOR_CONFIG_FILE);
		profiles = Utils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);

		String datenewformat = new SimpleDateFormat("dd MMMMM yyyy").format(new Date());
		result.append("<html>\n<head>\n<title>List of UAProfile profiles " + datenewformat + "</title>\n</head>\n");
		result.append("<body>\n<h1>List of UAProfile profiles " + datenewformat + "</h1>\n");

		getManufacturers();
		printManufacturers();

		result.append("</body>\n</html>");
		
		OutputStream out = null;
		try {
			out = new FileOutputStream(Constants.PROFILE_LIST_OUTPUT);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		// out.
		byte[] bytes = result.toString().getBytes();
		try {
			out.write(bytes);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Extract all the manufacturer names from the profile data.
	 */
	private void getManufacturers() {
		Resource resource = null;
		ResIterator profilesIter = profiles.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			resource = profilesIter.nextResource();
			if (resource.hasProperty(DeliSchema.uaprofUri)) {
				Resource m = (Resource) resource.getProperty(DeliSchema.manufacturedBy).getObject();
				String manufacturer = getPropertyString(m, RDFS.label);
				if (manufacturer != null && m.getURI() != null) {
					if (manufacturer.trim().length() > 0) {
						HashSet<String> uris = null;
						if (manufacturers.containsKey(manufacturer)) {
							uris = manufacturers.get(manufacturer);
						} else {
							uris = new HashSet<String>();
						}
						uris.add(m.getURI());
						manufacturers.put(manufacturer, uris);
					}
				}
			}
		}
	}

	/**
	 * Print the profiles sorted by manufacturers.
	 */
	private void printManufacturers() {
		for (String manufacturer : manufacturers.keySet()) {
			result.append("<h2>" + manufacturer + "</h2>\n");
			result.append("<p>\n<table width=\"100%\" border=\"1\"><tbody><tr><td width=\"25%\">Device</td><td width=\"25%\">Release</td><td width=\"50%\">URI</td></tr>\n");
			for (String manufacturerURL : manufacturers.get(manufacturer)) {
				ResIterator profilesIterBy = profiles.listSubjectsWithProperty(DeliSchema.manufacturedBy, profiles
						.getResource(manufacturerURL));
				while (profilesIterBy.hasNext()) {
					Resource resource = profilesIterBy.nextResource();
					String deviceName = getPropertyString(resource, DeliSchema.deviceName);
					String release = getPropertyString(resource, DeliSchema.release);
					String profileUri = getPropertyUri(resource, DeliSchema.uaprofUri);

					result.append("<tr>\n");
					result.append("<td>" + deviceName + "</td>\n");
					if (resource.hasProperty(DeliSchema.release)) {
						result.append("<td>" + release + "</td><td>\n");
					} else {
						result.append("<td></td><td>\n");
					}
					result.append("<a href=\"" + profileUri + "\">" + profileUri + "</a>\n");
					result.append("</td></tr>\n");
				}
			}
			result.append("</table>\n");
		}
	}
}