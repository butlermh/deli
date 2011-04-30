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
import java.util.HashMap;

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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;

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
	
	private AllProfiles profiles ;

	private SynchronizedModel allProfileData = new SynchronizedModel();

	private HashMap<Resource, Resource> results = new HashMap<Resource, Resource>();

	/**
	 * Command line interface.
	 * 
	 * @param args Does not take any arguments.
	 */
	public static void main(String[] args) {
		try {
			Model model = ModelUtils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);
			new ProcessUAProfMetadata(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor.
	 */
	ProcessUAProfMetadata(Model model) throws IOException {
		this.profiles = new AllProfiles(model);
		createPropertiesFile(model);
		queryGoogle();
		doWebCrawl();

		log.info("Writing out profile data");
		ModelUtils.writeModel(allProfileData.getModel(), Constants.ALL_PROFILES_RDF,
				"RDF/XML");
		ModelUtils.writeModel(model, Constants.ALL_KNOWN_UAPROF_PROFILES_OUTPUT, "N3");

		// print a summary statistic of how many UAProf profiles were found
		log.info("Processing statistics:");
		log.info(validProfiles + " valid profiles");
		log.info(invalidProfiles + " invalid profiles");
		log.info(unreachableProfiles + " unreachable profiles");
		log.info(invalidRDF + " profiles which were invalid RDF/XML");
		new CreateHTML(model, results);
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
				profiles.addDeviceIfNotAlreadyKnown(newURI);
				next = end;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			System.exit(0);
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
		new Crawler(profiles.getCrawlDb(), 100, ctor, this);
	}

	class Worker extends CrawlerWorker {

		class ProcessProfile {

			private StringBuffer messages = new StringBuffer();

			private boolean profileValidFlag = true;

			private boolean unreachable = false;

			private JenaReader myARPReader = new JenaReader();

			private String profile;
			
			private String deviceURI;

			ProcessProfile(Resource device) {
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
				this.deviceURI = device.getURI();
				try {
					profile = UrlUtils.getURL(deviceURI).trim();
					if (profile.contains("<html") || profile.contains("<head>")) {
						// this is HTML not a UAProf profile
						device.removeProperties();
					} else if (!profile.contains("rdf") && !profile.contains("RDF")) {
						device.removeProperties();
					} else if (!profile.contains("uaprof") && !profile.contains("UAPROF")) {
						device.removeProperties();
					} else if (!profile.contains("openmobilealliance") && !profile.contains("wapforum")) {
						device.removeProperties();
					} else {
						validate(device);
						profiles.fixMetadata(device, profile);
					}
				} catch (IOException io) {
					outputMsg("Could not retrieve " + device.getURI());
					device.removeProperties();
					unreachableProfiles++;
				}
				System.out.println(messages.toString());
			}

			private void validate(Resource device) {
				Model model = ModelFactory.createDefaultModel();
				try {
					myARPReader.read(model, new StringReader(profile), deviceURI);
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
							results.put(device, DeliSchema.Valid);
						} else {
							outputMsg("PROFILE IS NOT VALID");
							invalidProfiles++;
							results.put(device, DeliSchema.Invalid);
							makeValidatorReport();
						}
					}
				} catch (JenaException e) {
					outputMsg("Could not parse profile " + deviceURI);
					profileValidFlag = false;
					invalidRDF++;
					results.put(device, DeliSchema.Invalid);
					makeValidatorReport();
				} catch (Exception e) {
					outputMsg("Could not load profile " + deviceURI);
					unreachable = true;
					profileValidFlag = false;
					unreachableProfiles++;
					results.put(device, DeliSchema.Unretrievable);
					makeValidatorReport();
					outputMsg("PROFILE IS UNREACHABLE");
				}
			}

			void makeValidatorReport() {
				try {
					FileOutputStream theFileStream = null;
					Writer out = null;
					try {
						URL theURL = new URL(deviceURI);
						String filepath = CreateHTML.VALIDATOR_REPORTS + theURL.getHost()
								+ theURL.getFile() + ".html";
						String path = filepath.substring(0, filepath.lastIndexOf('/'));
						File directory = new File(path);
						if (!directory.exists()) {
							directory.mkdirs();
						}
						StringBuffer result = new StringBuffer();
						result.append("<html>\n");
						result.append("<head><title>Validator report for "
								+ deviceURI + "</title></head>\n");
						result.append("<body>\n");
						result.append("<h1>Validator output for " + deviceURI
								+ "</h1>\n");
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

			void outputMsg(String s) {
				messages.append(s + "\n");
			}
		}

		public Worker() {
			super();
		}

		void processURI(Resource device) {
			new ProcessProfile(device);
		}
	}
}