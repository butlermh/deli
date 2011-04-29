package com.hp.hpl.deli.utils;

import hirondelle.web4j.util.EscapeChars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.deli.ProfileProcessor;
import com.hp.hpl.deli.ValidateProfile;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * This class queries Google to see if it can identify any UAProf profiles that
 * are not listed in profiles.n3.
 */
public class ProcessUAProfMetadata {

	final static String BASE = "http://purl.oclc.org/NET/butlermh/deli/";

	private static Log log = LogFactory.getLog(ProcessUAProfMetadata.class);

	private int validProfiles = 0;

	private int invalidProfiles = 0;

	private int unreachableProfiles = 0;

	private int invalidRDF = 0;

	private ProfileProcessor configuration;

	private Model profiles;

	private SynchronizedModel allProfileData = new SynchronizedModel();

	private Manufacturers manufacturers;

	private List<Resource> crawlDb = Collections
			.synchronizedList(new LinkedList<Resource>());

	private ExcludeHosts excludedHost = new ExcludeHosts();

	/**
	 * Command line interface.
	 * 
	 * @param args Does not take any arguments.
	 */
	public static void main(String[] args) {
		try {
			Model model = ModelUtils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);
			new ProcessUAProfMetadata(model);
			new CreateHTML(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean validate = false;

	/**
	 * Constructor.
	 */
	ProcessUAProfMetadata(Model prfs) throws IOException {
		this.profiles = prfs;
		createPropertiesFile(prfs);
		manufacturers = new Manufacturers(prfs);

		ResIterator profilesIter = prfs
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			crawlDb.add(resource);
		}

		// queryGoogle();
		doWebCrawl();

		log.info("Writing out profile data");
		ModelUtils.writeModel(allProfileData.getModel(), Constants.ALL_PROFILES_RDF,
				"RDF/XML");
		ModelUtils.writeModel(prfs, Constants.ALL_KNOWN_UAPROF_PROFILES_OUTPUT, "N3");

		// print a summary statistic of how many UAProf profiles were found
		outputMsg("Processing statistics:");
		outputMsg(validProfiles + " valid profiles");
		outputMsg(invalidProfiles + " invalid profiles");
		outputMsg(unreachableProfiles + " unreachable profiles");
		outputMsg(invalidRDF + " profiles which were invalid RDF/XML");
	}

	private void createPropertiesFile(Model prfs) throws IOException {
		ProcessProperties p = new ProcessProperties();
		HashMap<String, HashMap<String, Integer>> properties = p
				.createPropertyStructure(prfs);
		StringBuffer result = p.printResults(properties);
		UrlUtils.savePage(Constants.PROPERTIES_OUTPUT_FILE, result);
	}

	private void queryGoogle() {
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
	}

	private void doWebCrawl() throws IOException {
		// create a threaded web crawler to retrieve all the profiles
		configuration = new ProfileProcessor(Constants.VALIDATOR_CONFIG_FILE);
		Class<ProcessUAProfMetadata.Worker> clazz = ProcessUAProfMetadata.Worker.class;
		Constructor<ProcessUAProfMetadata.Worker> ctor = null;
		try {
			ctor = clazz.getConstructor(ProcessUAProfMetadata.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		new Crawler(crawlDb, 100, ctor, this);
	}

	static void outputMsg(String s) {
		System.out.println(s);
	}

	// do we already know about this profile or is it on an excluded host?

	private boolean isDeviceAlreadyKnown(String deviceURI) {
		if (excludedHost.excludedHost(deviceURI)) {
			return true;
		}
		if (!deviceURI.startsWith("http")) {
			return true;
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
					profiles.add(resource, RDF.type, DeliSchema.Profile);
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

		class ProcessProfile {

			private String profileUrl;

			private StringBuffer messages = new StringBuffer();

			private boolean profileValidFlag = true;

			private boolean unreachable = false;

			private JenaReader myARPReader = new JenaReader();

			private Resource device;

			private DeviceData deviceData;

			private String profile;

			private String deviceName;

			private String manufacturerName;

			private Model model;

			ProcessProfile() {
				myARPReader.setProperty("WARN_RESOLVING_URI_AGAINST_EMPTY_BASE",
						"EM_IGNORE");
				myARPReader.setErrorHandler(new RDFErrorHandler() {
					// ARP parser error handling routines
					public void warning(Exception e) {
						outputMsg("RDF parser warning:" + e.getMessage());
					}

					public void error(Exception e) {
						outputMsg("RDF parser error:" + e.getMessage());
						profileValidFlag = false;
					}

					public void fatalError(Exception e) {
						e.printStackTrace();
						error(e);
					}
				});
			}

			private void validate() {
				model = ModelFactory.createDefaultModel();
				try {
					myARPReader.read(model, new StringReader(profile), device.getURI());
					if (!unreachable) {
						ValidateProfile validated = null;
						try {
							validated = new ValidateProfile(configuration, model);
							profileValidFlag = validated.isProfileValid();
							allProfileData.add(model);
						} catch (Exception e) {
							profileValidFlag = false;
							outputMsg(e.toString());
							e.printStackTrace();
						}

						if (validated != null) {
							if (validated.getValidationMessages() != null) {
								messages.append(validated.getValidationMessages());
							}
						}
						if (profileValidFlag) {
							validProfiles++;
							device.removeAll(DeliSchema.validatorResult);
							device.addProperty(DeliSchema.validatorResult,
									DeliSchema.Valid);
						} else {
							outputMsg("PROFILE IS NOT VALID");
							invalidProfiles++;
							device.removeAll(DeliSchema.validatorResult);
							device.addProperty(DeliSchema.validatorResult,
									DeliSchema.Invalid);
							makeValidatorReport();
						}
					}
				} catch (JenaException e) {
					outputMsg("Could not parse profile " + profileUrl);
					profileValidFlag = false;
					invalidRDF++;
					device.removeAll(DeliSchema.validatorResult);
					device.addProperty(DeliSchema.validatorResult, DeliSchema.Invalid);
					makeValidatorReport();
				} catch (Exception e) {
					outputMsg("Could not load profile " + profileUrl);
					unreachable = true;
					profileValidFlag = false;
					unreachableProfiles++;
					device.removeAll(DeliSchema.validatorResult);
					device.addProperty(DeliSchema.validatorResult,
							DeliSchema.Unretrievable);
					makeValidatorReport();
					outputMsg("PROFILE IS UNREACHABLE");
				}
			}

			void makeValidatorReport() {
				try {
					FileOutputStream theFileStream = null;
					Writer out = null;
					try {
						URL theURL = new URL(profileUrl);
						String filepath = CreateHTML.VALIDATOR_REPORTS
								+ theURL.getHost() + theURL.getFile() +".html";
						String path = filepath.substring(0, filepath.lastIndexOf('/'));
						File directory = new File(path);
						if (!directory.exists()) {
							directory.mkdirs();
						}
						StringBuffer result = new StringBuffer();
						result.append("<html>\n");
						result.append("<head><title>Validator report for " + profileUrl
								+ "</title></head>\n");
						result.append("<body>\n");
						result.append("<h1>Validator output for " + profileUrl +"</h1>\n");
						result.append("<pre>\n");
						result.append(EscapeChars.forXML(messages.toString()));
						result.append("</pre>\n");
						result.append("<p><a href=\"mailto:markhenrybutler@gmail.com\">Problems with this report?</a></p>");
						result.append("<h1>Original profile</h1>\n");
						result.append("<pre>\n");
						result.append(EscapeChars.forXML(profile));
						result.append("</pre>\n");
						result.append("</body>\n");
						result.append("</html>\n");
						File localFile = new File(filepath);
						localFile.createNewFile();
						theFileStream = new FileOutputStream(localFile);
						out = new OutputStreamWriter(theFileStream);
						out.write(result.toString());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (out != null)
							out.close();
						if (theFileStream != null)
							theFileStream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void fixMetadata() {
				String manufacturerNameFromProfile = TagSoupProcessor.getTagSoup(profile,
						":Vendor>", "<");
				if (manufacturerNameFromProfile == null
						|| manufacturerNameFromProfile.length() == 0) {
					manufacturerNameFromProfile = TagSoupProcessor.getBackwardTagSoup(
							profile, ">", "</prf:Vendor");
				}
				String deviceNameFromProfile = TagSoupProcessor.getTagSoup(profile,
						":Model>", "<");
				if (deviceNameFromProfile == null || deviceNameFromProfile.length() == 0) {
					deviceNameFromProfile = TagSoupProcessor.getBackwardTagSoup(profile,
							">", "</prf:Model");
				}

				if (deviceNameFromProfile != null) {
					profiles.removeAll(device, DeliSchema.deviceName, (RDFNode) null);
					profiles.add(device, DeliSchema.deviceName, deviceNameFromProfile);
				}

				if (manufacturerNameFromProfile != null
						&& manufacturerNameFromProfile.length() > 0) {
					manufacturerName = manufacturerNameFromProfile;
				} else if (deviceData.hasManufacturer()) {
					if (deviceData.hasManufacturerLabel()) {
						manufacturerName = deviceData.getManufacturer();
					}
				}

				if (deviceNameFromProfile != null && deviceNameFromProfile.length() > 0) {
					deviceName = deviceNameFromProfile;
				} else {
					deviceName = deviceData.getDeviceName();
				}
				if (manufacturerName != null) {
					String manufacturer = manufacturers.get(manufacturerName);
					if (manufacturer != null) {
						Resource manufacturerResource = profiles
								.createResource(manufacturer);
						profiles.removeAll(device, DeliSchema.manufacturedBy,
								(RDFNode) null);
						profiles.add(device, DeliSchema.manufacturedBy,
								manufacturerResource);
						profiles.add(manufacturerResource, RDFS.label, manufacturerName);
						profiles.add(device, DeliSchema.deviceName, deviceName);
						profiles.add(device, RDF.type, DeliSchema.Profile);
					} else {
						outputMsg("ERROR: Could not find manufacturer URI for ["
								+ manufacturerName + "] for " + device);
					}
				}
			}

			public void process(Resource device) {
				this.device = device;
				try {
					deviceData = new DeviceData(device);
					profileUrl = device.getURI();
					profile = UrlUtils.getURL(device.getURI()).trim();
					if (profile.contains("<html") && profile.contains("<head>")) {
						// we picked up some HTML by mistake
						device.removeProperties();
					} else {
						validate();
					}
					fixMetadata();

				} catch (IOException io) {
					outputMsg("Could not retrieve " + device.getURI());
					device.removeProperties();
					unreachableProfiles++;
				}

				System.out.println(messages.toString());
			}

			void outputMsg(String s) {
				messages.append(s + "\n");
			}
		}

		public Worker() {
			super();
		}

		void processURI(Resource device) {
			new ProcessProfile().process(device);
		}
	}
}