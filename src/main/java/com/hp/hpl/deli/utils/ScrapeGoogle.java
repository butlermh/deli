package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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

	private static Log log = LogFactory.getLog(ScrapeGoogle.class);

	private Model profiles;

	private Vector<String> exclude = new Vector<String>();

	private HashMap<String, String> manufacturers = new HashMap<String, String>();

	private HashMap<String, String> manufacturerHostnames = new HashMap<String, String>();

	private int count = 0;

	private Map<String, String> fixManufacturers = new HashMap<String, String>();

	private final static String[] oldManufacturers = { "sonyericsson", "nokia",
			"toshiba", "samsung", "panasonic", "Research_In_Motion_Ltd.", "motorola",
			"sharp", "LG", "siemens", "alcatel", "High_Tech_Computer_Corporation",
			"amoi", "Kyocera", "Kyocera_Wireless_Corporation", "Kyocera_Wireless_Corp",
			"ZTE", "ZTE_Corporation", "Acer_Incorporated", "Acer", "Vodafone", "Pantech",
			"Zonda", "Ericsson", "FLY", "Huawei", "INQ", "Modelabs", "Openwave", "Palm",
			"Philips", "Haier", "ASUSTeK_COMPUTER_INC.", "NEC" };

	private final static String[] newManufacturers = { "http://www.sonyericsson.com",
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

	// We ignore these hosts as they are not valid sources of UAProf
	// profiles
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
		try {
			Model model = ModelUtils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);
			new CreateHTML(model);
			new ScrapeGoogle(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class SynchronizedModel {
		private Model model = ModelFactory.createDefaultModel();

		public synchronized void add(Model m) {
			model.add(m);
		}

		public synchronized Model getModel() {
			return model;
		}
	}

	private SynchronizedModel allProfileData = new SynchronizedModel();

	/**
	 * Constructor.
	 */
	ScrapeGoogle(Model prfs) throws IOException {
		log.info("This program retrieves all known profiles, builds a single RDF model from that data, and then creates a dendrogram of the properties used.");

		ProcessProperties p = new ProcessProperties();
		HashMap<String, HashMap<String, Integer>> properties = p
				.createPropertyStructure(prfs);
		StringBuffer result = p.printResults(properties);
		UrlUtils.savePage(Constants.PROPERTIES_OUTPUT_FILE, result);
		String manufacturerString = BASE + "manufacturers#";
		for (int i = 0; i < oldManufacturers.length; i++) {
			String oldManufacturer = (manufacturerString + oldManufacturers[i])
					.toLowerCase();
			fixManufacturers.put(oldManufacturer, newManufacturers[i]);
		}

		for (String s : EXCLUDE_LIST) {
			exclude.add(s);
		}

		// build a hashmap for hosts on to manufacturers names from the data
		// in profiles.n3
		profiles = ModelUtils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);
		ResIterator profilesIter = profiles
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			crawlDb.add(resource);
			Resource m = (Resource) resource.getProperty(DeliSchema.manufacturedBy)
					.getObject();
			if (ModelUtils.getPropertyString(m, RDFS.label) != null) {
				String manufacturer = ModelUtils.getPropertyString(m, RDFS.label);
				try {
					URI theProfile = new URI(resource.getURI());
					String profileHostname = theProfile.getHost();
					manufacturers.put(profileHostname, manufacturer);
					manufacturerHostnames.put(manufacturer, m.getURI());
				} catch (URISyntaxException use) {
					use.printStackTrace();
				}
			}
		}

		// query google for files of type XML and RDF that mention the term
		// UAProf

		final String QUERY_SEP = "&";
		final String FILTER = "filter=0";
		final String START = "http://www.google.com/search?q=filetype:";
		final String END = "+uaprof&num=100&";

		final String RDF_STRING = START + "rdf" + END;
		processPage(RDF_STRING + FILTER);
		for (int j = 1; j < 43; j++) {
			processPage(RDF_STRING + "start=" + j + "00" + QUERY_SEP + FILTER);
		}
		final String XML_STRING = START + "xml" + END;
		processPage(XML_STRING + FILTER);
		for (int i = 1; i < 16; i++) {
			processPage(XML_STRING + "start=" + i + "00" + QUERY_SEP + FILTER);
		}

		// create a threaded web crawler to retrieve all the profiles

		Class<ScrapeGoogle.Worker> clazz = ScrapeGoogle.Worker.class;
		Constructor<ScrapeGoogle.Worker> ctor = null;
		try {
			ctor = clazz.getConstructor(ScrapeGoogle.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		new Crawler(crawlDb, 100, ctor, this);

		// write out the profile data
		log.info("Writing out profile data");
		ModelUtils.writeModel(allProfileData.getModel(), Constants.ALL_PROFILES_RDF,
				"RDF/XML");

		// print a summary statistic of how many UAProf profiles were found

		System.out.println("\nFound " + count + " new profiles.");
		System.out.println("Writing out profile data");
		RDFWriter writer = profiles.getWriter("N3");
		writer.setProperty("allowBadURIs", "true");
		String filepath = Constants.ALL_KNOWN_UAPROF_PROFILES_OUTPUT;
		String path = filepath.substring(0, filepath.lastIndexOf('/'));
		new File(path).mkdirs();
		OutputStream out = null;
		try {
			out = new FileOutputStream(filepath);
			writer.write(profiles, out, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
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
			String inputString = UrlUtils.getURL(pageUri);
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

		JenaReader arpReader = ModelUtils.configureArp();

		public Worker() {
			super();
		}

		void processURI(Resource device) {
			StringBuffer messages = new StringBuffer();
			try {
				URI deviceURI = new URI(device.getURI());
				DeviceData deviceData = new DeviceData(device);

				String profileUri = device.getURI();
				String profile = UrlUtils.getURL(device.getURI());
				Model pModel = ModelFactory.createDefaultModel();
				arpReader.read(pModel, new StringReader(profile), profileUri);
				allProfileData.add(pModel);

				String manufacturerName = null;
				String host = deviceURI.getHost();
				if (deviceData.hasManufacturer()) {
					manufacturerName = deviceData.getManufacturer();
				} else if (manufacturers.containsKey(host)) {
					manufacturerName = manufacturers.get(host);
				} else {
					manufacturerName = getTagSoup(profile, ":Vendor>", "<");
				}

				String deviceName = deviceData.hasDeviceName() ? deviceData.getDeviceName()
						: getTagSoup(profile, ":Model>", "<");

				if (!deviceData.hasManufacturer()) {
					Resource manufacturerResource = null;
					String manufacturerLowerCase = manufacturerName.toLowerCase().trim();
					String manufacturer = null;
					synchronized (this) {
						if (fixManufacturers.containsKey(manufacturerLowerCase)) {
							manufacturer = fixManufacturers.get(manufacturerLowerCase);
						} else if (manufacturerHostnames.containsKey(manufacturerName)) {
							manufacturer = manufacturerHostnames.get(manufacturerName);
						} else {
							manufacturer = BASE + "manufacturers#"
									+  manufacturerName.replace(" ", "_");
						}
						manufacturerResource = profiles.createResource(manufacturer);
						profiles.add(device, DeliSchema.manufacturedBy,
								manufacturerResource);
						profiles.add(manufacturerResource, RDFS.label, manufacturerName);
						profiles.add(device, DeliSchema.deviceName,
								profiles.createLiteral(deviceName));
						profiles.add(device, RDF.type, DeliSchema.Profile);
					}
				}

				messages.append("MANUFACTURER: " + manufacturerName + "     DEVICE NAME:  "
						+ deviceName);
				if (deviceData.hasProvider()) {
					messages.append("PROFILE NOT CREATED BY VENDOR - PROVIDER: "
							+ deviceData.getProvider());
				}

			} catch (Exception ie) {
				ie.printStackTrace();
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
			int begin = profile.indexOf(startTag);
			int end = profile.indexOf(endTag, begin + startTag.length());
			if (begin >= 0 && end >= 0) {
				return profile.substring(begin + startTag.length(), end).trim();
			}
			return null;
		}
	}

}