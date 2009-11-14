package com.hp.hpl.deli.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.Utils;
import com.hp.hpl.deli.Workspace;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * This class queries Google to see if it can identify any UAProf profiles that
 * are not listed in profiles.n3.
 */
public class ScrapeGoogle extends Utils {

	private final static String BASE = "http://purl.oclc.org/NET/butlermh/deli/";

	private final static String QUERY_SEP = "&";

	private final static String FILTER = "filter=0";

	private final static String START = "http://www.google.com/search?q=filetype:";

	private final static String END = "+uaprof&num=100&";

	private final static String RDF_STRING = START + "rdf" + END;

	private final static String XML_STRING = START + "xml" + END;

	private static Log log = LogFactory.getLog(ScrapeGoogle.class);

	private Model profiles;

	private Vector<String> exclude = new Vector<String>();

	private HashMap<String, String> manufacturers = new HashMap<String, String>();

	private int count = 0;

	// We ignore these hosts as they are not valid sources of UAProf
	// profiles or they are numeric versions of known profile providers
	private final static String[] EXCLUDE_LIST = { "www.cs.umbc.edu", "crschmidt.net", "burningdoor.com",
			"start.cefriel.it", "www.xxxwap.com", "139.91.183.30", "www2.cs.uh.edu", "www.thauvin.net",
			"213.249.206.60", "www.sun.com", "140.136.206.230", "wap.samsungmobile.com/uaprof/SUWON.xml", "gpf.pri.ee",
			"w3development.de", "www.w3.org", "194.204.48.96", "blog.livedoor.jp", "www.mail-archive.com",
			"www.port4.info", "www.hcs-systems.ch", "rzm-hamy-wsx.rz.uni-karlsruhe.de", "cmsdomino.brookes.ac.uk",
			"ggordillo.blogspot.com", "rynt02.rz.uni-karlsruhe.de", "www.ninebynine.org", "vs.sourceforge.net",
			"194.204.48.96", "attila.sdsu.edu", "www.it.lut.fi", "google.com", "youtube.com", "search?q=cache",
			"search?hl=", "?q=filetype", "/intl/en/about.html", "reference.com" };

	/**
	 * Command line interface.
	 *
	 * @param args Does not take any arguments.
	 */
	public static void main(String[] args) {
		new ScrapeGoogle();
	}

	/**
	 * Constructor.
	 */
	public ScrapeGoogle() {
		Resource resource = null;
		try {
			Workspace.getInstance().configure(null, Constants.VALIDATOR_CONFIG_FILE);
			profiles = Utils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);

			for (String s : EXCLUDE_LIST) {
				exclude.add(s);
			}

			// build a hashmap for hosts on to manufacturers names from the data
			// in profiles.rdf
			ResIterator profilesIter = profiles.listSubjectsWithProperty(DeliSchema.manufacturedBy);
			while (profilesIter.hasNext()) {
				resource = profilesIter.nextResource();
				if (resource.hasProperty(DeliSchema.uaprofUri)) {
					URI theProfile = new URI(getPropertyUri(resource, DeliSchema.uaprofUri));
					Resource m = (Resource) resource.getProperty(DeliSchema.manufacturedBy).getObject();
					String manufacturer = getPropertyString(m, RDFS.label);
					String hostName = theProfile.getHost();
					manufacturers.put(hostName, manufacturer);
				}
			}

			// query google for files of type xml and rdf that mention the term
			// UAProf

			new ProcessPage(RDF_STRING + FILTER);
			for (int j = 1; j < 4; j++) {
				new ProcessPage(RDF_STRING + "start=" + j + "00" + QUERY_SEP + FILTER);
			}
			new ProcessPage(XML_STRING + FILTER);
			for (int i = 1; i < 9; i++) {
				new ProcessPage(XML_STRING + "start=" + i + "00" + QUERY_SEP + FILTER);
			}

			// print a summary statistic of how many UAProf profiles were found

			System.out.println("\nFound " + count + " new profiles.");
		} catch (Exception e) {
			log.error(resource.toString(), e);
		}
		System.out.println("Writing out profile data");
		RDFWriter writer = profiles.getWriter("N3");
		writer.setProperty("allowBadURIs", "true");
		OutputStream out = null;
		try {
			out = new FileOutputStream(Constants.ALL_KNOWN_UAPROF_PROFILES + ".updated");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.write(profiles, out, null);
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// do we already know about this profile or is it on an excluded host?

	private boolean isDeviceAlreadyKnown(String deviceURI) {
		for (String next : exclude) {
			if (deviceURI.contains(next)) {
				return true;
			}
			if (!deviceURI.startsWith("http")) {
				return true;
			}
		}
		Resource p = profiles.createResource(deviceURI);
		ResIterator profilesIter = profiles.listSubjectsWithProperty(DeliSchema.uaprofUri, p);
		if (profilesIter.hasNext()) {
			return true;
		}
		p = profiles.createResource(deviceURI.toLowerCase());
		profilesIter = profiles.listSubjectsWithProperty(DeliSchema.uaprofUri, p);
		if (profilesIter.hasNext()) {
			return true;
		}
		return false;
	}

	// retrieve the page, extract potential profile URLs, then print them out
	// if they are not already in profiles.rdf

	private class ProcessPage {

		private String manufacturer = null;

		private URI device = null;

		private String model = null;

		private String vendor = null;

		private String newURI = null;

		ProcessPage(String pageUri) {
			String search = "href=\"";
			try {
				String inputString = Utils.getURL(pageUri);
				int next = 0;

				while ((next = inputString.indexOf(search, next)) != -1) {
					int end = inputString.indexOf("\"", next + search.length());
					end = (end == -1) ? inputString.indexOf("\">", next + search.length()) : end;
					newURI = inputString.substring((next + search.length()), end);
					if (!isDeviceAlreadyKnown(newURI)) {
						device = new URI(newURI);
						manufacturer = (String) manufacturers.get(device.getHost());
						if (manufacturer == null) {
							manufacturer = "SPECIFY-MANUFACTURER";
						}
						String claimedManufacturer = null;
						vendor = "SPECIFY-VENDOR";

						try {
							String profile = Utils.getURL(newURI);
							claimedManufacturer = getTagSoup(profile, ":Vendor>", "<");

							if (manufacturer.equals("Blackberry") && claimedManufacturer != null) {
								manufacturer = claimedManufacturer;
							} else if (manufacturer.equals("SPECIFY-MANUFACTURER") && claimedManufacturer != null) {
								manufacturer = claimedManufacturer;
								vendor = "false";
							} else {
								vendor = claimedManufacturer.toLowerCase().equals(manufacturer.toLowerCase()) ? "true"
										: "false";
							}

							model = getTagSoup(profile, ":Model>", "<");
						} catch (Exception e) {
							// don't do anything
						}

						if (model != null) {
							printDeviceData();
							count++;
						}
					}
					next = end;
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
				System.exit(0);
			}
		}

		/**
		 * Tag soup approach to get data out of profile
		 *
		 * @param profile The profile as ASCII text
		 * @param startTag the start tag to look for
		 * @param endTag the end tag to look for
		 * @return the value
		 */
		private String getTagSoup(String profile, String startTag, String endTag) {
			String value = null;
			int begin = profile.indexOf(startTag);
			int end = profile.indexOf(endTag, begin + startTag.length());
			if (begin >= 0 && end >= 0) {
				value = profile.substring(begin + startTag.length(), end).trim();
			}
			return value;
		}

		/**
		 * Print out N3 information about a device
		 */
		private void printDeviceData() {
			String nospaceManufacturer = manufacturer.replace(" ", "_");
			String filename = device.getPath();
			String deviceURL = BASE + "devices#" + nospaceManufacturer + filename;
			String manufacturerURL = BASE + "manufacturers#" + nospaceManufacturer;
			Resource manufacturerResource1 = profiles.createResource(manufacturerURL);
			Resource manufacturerResource2 = profiles.createResource(manufacturerURL.toLowerCase());

			Resource rDevice = profiles.createResource(deviceURL);
			Resource rProfile = profiles.createResource(newURI);
			profiles.add(profiles.createStatement(rDevice, DeliSchema.deviceName, model));
			profiles.add(profiles.createStatement(rDevice, RDF.type, DeliSchema.Profile));
			profiles.add(profiles.createStatement(rDevice, DeliSchema.manufacturedBy, rDevice));
			profiles.add(profiles.createStatement(rDevice, DeliSchema.profileSuppliedByVendor, manufacturerResource1));
			profiles.add(profiles.createLiteralStatement(rDevice, DeliSchema.supportsUAProf, true));
			profiles.add(profiles.createStatement(rDevice, DeliSchema.uaprofUri, rProfile));
			// profiles.
			System.out.println("<" + deviceURL + ">");
			System.out.println("  a deli:Profile ;");
			System.out.println("  deli:deviceName \"" + model + "\" ;");
			System.out.println("  deli:manufacturedBy <" + manufacturerURL + "> ;");
			System.out.println("  deli:profileSuppliedByVendor \"" + vendor + "\" ;");
			System.out.println("# deli:release \"SPECIFY-RELEASE\" ;");
			System.out.println("  deli:supportsUAProf \"true\" ;");
			System.out.println("  deli:uaprofUri <" + newURI + "> .\n");

			if (!profiles.contains(manufacturerResource1, RDFS.label)
					&& !profiles.contains(manufacturerResource2, RDFS.label)) {
				// print out manufacturer description
				System.out.println(manufacturerURL);
				System.out.println("   rdfs:label \"" + manufacturer + "\" . \n");

				profiles.add(manufacturerResource2, RDFS.label, manufacturer);
			}
		}
	}
}