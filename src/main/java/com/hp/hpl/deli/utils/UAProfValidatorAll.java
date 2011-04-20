package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;

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
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Read in a file of all known UAProf profiles and validate them.
 */
public class UAProfValidatorAll {
	int validProfiles = 0;

	int invalidProfiles = 0;

	int unreachableProfiles = 0;

	int invalidRDF = 0;

	private ProfileProcessor configuration;

	/**
	 * Provides a command line interface to the validator.
	 * 
	 * @param args a list of profiles to validate separated by whitespace
	 */
	public static void main(String[] args) {
		UAProfValidatorAll v = null;
		try {
			v = new UAProfValidatorAll();
			v.processAllProfiles();
		} catch (Exception f) {
			f.printStackTrace();
			outputMsg("DELI error:" + f.toString());
		}
	}

	public UAProfValidatorAll() throws IOException {
		configuration = new ProfileProcessor(Constants.VALIDATOR_CONFIG_FILE);
	}

	static void outputMsg(String s) {
		System.out.println(s);
	}

	private void processAllProfiles() throws IOException {
		String welcomeMsg = "DELI Validator http://delicon.sourceforge.net/ \n"
				+ "Running automated test for all profiles specified in profiles.n3 \n"
				+ "IMPORTANT If you run this behind a firewall you must configure proxy e.g. \n"
				+ "java -Dhttp.proxyHost=<hostaddress> -Dhttp.proxyPort=<hostport> com.hp.hpl.deli.UAProfValidatorAll \n \n";

		outputMsg(welcomeMsg);

		CreateCrawlDb createDb = new CreateCrawlDb(Constants.ALL_KNOWN_UAPROF_PROFILES);
		List<Resource> crawlDb = createDb.getCrawlDb();

		Class<UAProfValidatorAll.Worker> clazz = UAProfValidatorAll.Worker.class;
		Constructor<UAProfValidatorAll.Worker> ctor = null;
		try {
			ctor = clazz.getConstructor(UAProfValidatorAll.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		new Crawler(crawlDb, 100, ctor, this);
		outputMsg("Processing statistics:");
		outputMsg(validProfiles + " valid profiles");
		outputMsg(invalidProfiles + " invalid profiles");
		outputMsg(unreachableProfiles + " unreachable profiles");
		outputMsg(invalidRDF + " profiles which were invalid RDF/XML");
	}

	class Worker extends CrawlerWorker {

		class ProcessProfile {
			String profileUrl;

			StringBuffer messages = new StringBuffer();

			boolean validRDF = true;

			boolean profileValidFlag = true;

			boolean unreachable = false;

			JenaReader myARPReader = new JenaReader();

			Model model = ModelFactory.createDefaultModel();

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
						if (validRDF) {
							validRDF = false;
						}
						profileValidFlag = false;
					}

					public void fatalError(Exception e) {
						e.printStackTrace();
						error(e);
					}
				});
			}

			public boolean process(Resource profileData) {
				StringReader profile;
				String manufacturer = "";
				if (profileData.hasProperty(DeliSchema.manufacturedBy)) {
					Resource manufacturerURI = profileData
							.getProperty(DeliSchema.manufacturedBy).getObject()
							.asResource();
					manufacturer = ModelUtils.getPropertyString(manufacturerURI,
							RDFS.label);
				}

				if (profileData.hasProperty(DeliSchema.provider)) {
					String provider = ModelUtils.getPropertyUri(profileData,
							DeliSchema.provider);
					outputMsg("PROFILE NOT CREATED BY VENDOR - PROVIDER: " + provider);
				}

				String deviceName = "";
				if (profileData.hasProperty(DeliSchema.deviceName)) {
					deviceName = ModelUtils.getPropertyString(profileData,
							DeliSchema.deviceName);
				}
				outputMsg("MANUFACTURER: " + manufacturer + "     DEVICE NAME:  "
						+ deviceName);
				String profileUri = profileData.getURI();
				if (profileUri.startsWith("http:")) {
					try {
						profile = new StringReader(downloadFileToLocalDir(profileUri));
						outputMsg(profileUrl);
						try {
							myARPReader.read(model, profile, profileUrl);
							return validate();
						} catch (JenaException e) {
							outputMsg("Could not parse profile " + profileUrl);
							profileValidFlag = false;
							invalidRDF++;
						} catch (Exception e) {
							outputMsg("Could not load profile " + profileUrl);
							unreachable = true;
							profileValidFlag = false;
							unreachableProfiles++;
							outputMsg("PROFILE IS UNREACHABLE");
						}
					} catch (IOException io) {
						outputMsg("Could not retrieve " + profileUri);
						unreachableProfiles++;
					}
				}
				return false;
			}

			boolean validate() {
				ValidateProfile validated = null;
				if (!unreachable) {
					try {
						validated = new ValidateProfile(configuration, model);
						profileValidFlag = validated.isProfileValid();
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
						outputMsg("PROFILE IS VALID");
						validProfiles++;
					} else {
						outputMsg("PROFILE IS NOT VALID");
						invalidProfiles++;
					}
				}

				System.out.println(messages.toString());
				return profileValidFlag;
			}

			void outputMsg(String s) {
				messages.append(s + "\n");
			}

			String downloadFileToLocalDir(String uri) throws IOException {
				InputStream streamToURL = null;
				StringBuffer result = new StringBuffer();

				// open InputStream to URL
				URL theURL = new URL(uri);
				streamToURL = theURL.openStream();

				// open OutputStream to local file
				String filepath = "build/manufacturerProfiles/" + theURL.getHost()
						+ theURL.getFile();
				String path = filepath.substring(0, filepath.lastIndexOf('/'));
				new File(path).mkdirs();
				File localFile = new File(filepath);
				localFile.createNewFile();
				FileOutputStream theFileStream = new FileOutputStream(localFile);

				int ch;
				while ((ch = streamToURL.read()) != -1) {
					result.append((char) ch);
					theFileStream.write(ch);
				}
				streamToURL.close();
				theFileStream.close();
				return result.toString();
			}

		}

		public Worker() {
		}

		void processURI(Resource profileData) {
			new ProcessProfile().process(profileData);
		}

	}

}
