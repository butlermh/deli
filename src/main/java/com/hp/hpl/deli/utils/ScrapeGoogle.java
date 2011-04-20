package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
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
public class ScrapeGoogle {

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

	private HashMap<String, String> manufacturerHostnames = new HashMap<String, String>();

	private int count = 0;

	final static String USER_AGENT = "User-Agent";

	final static String USER_AGENT_STRING = "Mozilla/5.0 (X11; U; Linux i686; rv:1.7.3) Gecko/20041020 Firefox/0.10.1";

	Map<String, String> fixManufacturers = new HashMap<String, String>();

	final static String[] oldManufacturers = { "sonyericsson", "nokia", "toshiba",
			"samsung", "panasonic", "Research_In_Motion_Ltd.", "motorola", "sharp", "LG",
			"siemens", "alcatel", "High_Tech_Computer_Corporation", "amoi", "Kyocera",
			"Kyocera_Wireless_Corporation", "Kyocera_Wireless_Corp", "ZTE",
			"ZTE_Corporation", "Acer_Incorporated", "Acer", "Vodafone", "Pantech",
			"Zonda", "Ericsson", "FLY", "Huawei", "INQ", "Modelabs", "Openwave", "Palm",
			"Philips", "Haier", "ASUSTeK_COMPUTER_INC.", "NEC" };

	final static String[] newManufacturers = { "http://www.sonyericsson.com",
			"http://www.nokia.com", "http://www.toshiba.com", "http://www.samsung.com",
			"http://www.panasonic.com", "http://www.rim.com", "http://www.motorola.com",
			"http://www.sharp.com", "http://www.lg.com", "http://www.siemens.com",
			"http://www.alcatel-mobilephones.com/", "http://www.htc.com/",
			"http://www.amoi.com/", "http://www.kyocera-wireless.com/",
			"http://www.kyocera-wireless.com/", "http://www.kyocera-wireless.com/",
			"http://www.zte.com.cn/", "http://www.zte.com.cn/", "http://www.acer.com/",
			"http://www.acer.com/", "http://www.vodafone.com", "http://www.pantech.com/",
			"http://www.zondatelecom.com/", "http://www.ericsson.com/",
			"http://www.fly-phone.com/", "http://www.huawei.com/",
			"http://www.inqmobile.com/", "http://www.modelabs.com/",
			"http://www.openwave.com", "http://www.palm.com", "http://www.philips.com/",
			"http://www.haier.com/", "http://www.asus.com", "http://www.nec.com" };

	/**
	 * Get the contents of a particular URL as a String.
	 * 
	 * @param pageUri the URL.
	 * @return the contents as a String.
	 * @throws Exception
	 */
	static String getURL(String pageUri) throws Exception {
		StringBuffer input = new StringBuffer();
		URL u = new URL(pageUri);
		URLConnection conn = u.openConnection();
		// have to fake the user agent to get results back from Google
		conn.setRequestProperty(USER_AGENT, USER_AGENT_STRING);
		conn.connect();

		InputStream s = conn.getInputStream();
		int ch;
		while ((ch = s.read()) != -1) {
			input.append((char) ch);
		}
		s.close();
		return input.toString();
	}

	// We ignore these hosts as they are not valid sources of UAProf
	// profiles or they are numeric versions of known profile providers
	private final static String[] EXCLUDE_LIST = { "www.cs.umbc.edu", "crschmidt.net",
			"burningdoor.com", "start.cefriel.it", "www.xxxwap.com", "139.91.183.30",
			"www2.cs.uh.edu", "www.thauvin.net", "213.249.206.60", "www.sun.com",
			"140.136.206.230", "wap.samsungmobile.com/uaprof/SUWON.xml", "gpf.pri.ee",
			"w3development.de", "www.w3.org", "194.204.48.96", "blog.livedoor.jp",
			"www.mail-archive.com", "www.port4.info", "www.hcs-systems.ch",
			"rzm-hamy-wsx.rz.uni-karlsruhe.de", "cmsdomino.brookes.ac.uk",
			"ggordillo.blogspot.com", "rynt02.rz.uni-karlsruhe.de", "www.ninebynine.org",
			"vs.sourceforge.net", "194.204.48.96", "attila.sdsu.edu", "www.it.lut.fi",
			"google.com", "youtube.com", "search?q=cache", "search?hl=", "?q=filetype",
			"/intl/en/about.html", "reference.com", "www.cems.uwe.ac.uk/" };

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
	ScrapeGoogle() {
		String manufacturerString = BASE + "manufacturers#";
		for (int i = 0; i < oldManufacturers.length; i++) {
			String oldManufacturer = (manufacturerString + oldManufacturers[i])
					.toLowerCase();
			fixManufacturers.put(oldManufacturer, newManufacturers[i]);
		}

		Resource resource = null;
		try {
			profiles = ModelUtils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);

			for (String s : EXCLUDE_LIST) {
				exclude.add(s);
			}

			// build a hashmap for hosts on to manufacturers names from the data
			// in profiles.rdf
			ResIterator profilesIter = profiles
					.listSubjectsWithProperty(DeliSchema.manufacturedBy);
			while (profilesIter.hasNext()) {
				resource = profilesIter.nextResource();
				crawlDb.add(resource);
				Resource m = (Resource) resource.getProperty(DeliSchema.manufacturedBy)
						.getObject();
				if (ModelUtils.getPropertyString(m, RDFS.label) != null) {
					String manufacturer = ModelUtils.getPropertyString(m, RDFS.label);
					URI theProfile = new URI(resource.getURI());
					String profileHostname = theProfile.getHost();
					manufacturers.put(profileHostname, manufacturer);
					manufacturerHostnames.put(manufacturer, m.getURI());
				}
			}

			// query google for files of type xml and rdf that mention the term
			// UAProf

			processPage(RDF_STRING + FILTER);
			for (int j = 1; j < 43; j++) {
				processPage(RDF_STRING + "start=" + j + "00" + QUERY_SEP + FILTER);
			}
			processPage(XML_STRING + FILTER);
			for (int i = 1; i < 16; i++) {
				processPage(XML_STRING + "start=" + i + "00" + QUERY_SEP + FILTER);
			}

			Class<ScrapeGoogle.Worker> clazz = ScrapeGoogle.Worker.class;
			Constructor<ScrapeGoogle.Worker> ctor = null;
			try {
				ctor = clazz.getConstructor(ScrapeGoogle.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			new Crawler(crawlDb, 100, ctor, this);

			// print a summary statistic of how many UAProf profiles were found

			System.out.println("\nFound " + count + " new profiles.");
		} catch (Exception e) {
			log.error(resource.toString(), e);
		}
		System.out.println("Writing out profile data");
		RDFWriter writer = profiles.getWriter("N3");
		writer.setProperty("allowBadURIs", "true");
		String filepath = Constants.ALL_KNOWN_UAPROF_PROFILES + ".updated";
		String path = filepath.substring(0, filepath.lastIndexOf('/'));
		new File(path).mkdirs();
		OutputStream out = null;
		try {
			out = new FileOutputStream(filepath);
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

	private List<Resource> crawlDb = Collections
			.synchronizedList(new LinkedList<Resource>());

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
		ResIterator profilesIter = profiles.listSubjectsWithProperty(
				DeliSchema.uaprofUri, p);
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

	void processPage(String pageUri) {
		String search = "href=\"";
		try {
			String inputString = getURL(pageUri);
			int next = 0;

			while ((next = inputString.indexOf(search, next)) != -1) {
				int end = inputString.indexOf("\"", next + search.length());
				end = (end == -1) ? inputString.indexOf("\">", next + search.length())
						: end;
				String newURI = inputString.substring((next + search.length()), end);
				if (!isDeviceAlreadyKnown(newURI)) {
					Resource resource = profiles.createResource(newURI);
					crawlDb.add(resource);
				}
				next = end;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			System.exit(0);
		}
	}

	class Worker extends CrawlerWorker {

		private String manufacturer = null;

		private URI device = null;

		private String model = null;

		private String newURI = null;

		public Worker() {
		}

		void processURI(Resource newURI) {
			try {
				this.newURI = newURI.getURI();
				device = new URI(this.newURI);
				manufacturer = manufacturers.get(device.getHost());
				if (manufacturer == null) {
					manufacturer = "SPECIFY-MANUFACTURER";
				}

				String profile = getURL(this.newURI);
				String claimedManufacturer = getTagSoup(profile, ":Vendor>", "<");

				if (manufacturer.equals("Blackberry") && claimedManufacturer != null) {
					manufacturer = claimedManufacturer;
				} else if (manufacturer.equals("SPECIFY-MANUFACTURER")
						&& claimedManufacturer != null) {
					manufacturer = claimedManufacturer;
				}

				model = getTagSoup(profile, ":Model>", "<");
				printDeviceData();
			} catch (Exception e) {
				// don't do anything
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
		private synchronized void printDeviceData() {
			Resource rProfile = profiles.createResource(newURI);
			String nospaceManufacturer = manufacturer.replace(" ", "_");
			Resource manufacturerResource = null;
			String cManufacturer = manufacturer.toLowerCase().trim();
			if (fixManufacturers.containsKey(cManufacturer)) {
				manufacturerResource = profiles.createResource(fixManufacturers
						.get(cManufacturer));
			} else if (manufacturerHostnames.containsKey(manufacturer)) {
				manufacturerResource = profiles.createResource(manufacturerHostnames
						.get(manufacturer));
			} else {
				if (!rProfile.hasProperty(DeliSchema.manufacturedBy)) {
					String manufacturerURL = BASE + "manufacturers#"
							+ nospaceManufacturer;
					manufacturerResource = profiles.createResource(manufacturerURL);
				}
			}

			if (rProfile.hasProperty(DeliSchema.deviceName)) {
				String currentModel = rProfile.getProperty(DeliSchema.deviceName)
						.getLiteral().getLexicalForm();
				if (currentModel.length() < model.length()) {
					rProfile.removeAll(DeliSchema.deviceName);
					profiles.add(profiles.createStatement(rProfile,
							DeliSchema.deviceName, profiles.createTypedLiteral(model)));
				}
			} else {
				profiles.add(profiles.createStatement(rProfile, DeliSchema.deviceName,
						profiles.createTypedLiteral(model)));
			}

			profiles.add(profiles.createStatement(rProfile, RDF.type, DeliSchema.Profile));

			System.out.println("<" + rProfile.getURI() + ">");
			System.out.println("  a deli:Profile ;");
			System.out.println("  deli:deviceName \"" + model + "\" ;");
			if (manufacturerResource != null) {
				profiles.add(profiles.createStatement(rProfile,
						DeliSchema.manufacturedBy, manufacturerResource));
				System.out.println("  deli:manufacturedBy <"
						+ manufacturerResource.getURI() + "> .");
				profiles.add(manufacturerResource, RDFS.label, manufacturer);
			}
		}

	}

}