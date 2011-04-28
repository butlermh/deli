package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.ProfileProcessor;
import com.hp.hpl.deli.ValidateProfile;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;

/**
 * Read in a file of all known UAProf profiles and validate them.
 */
public class UAProfValidatorAll {
	private int validProfiles = 0;

	private int invalidProfiles = 0;

	private int unreachableProfiles = 0;

	private int invalidRDF = 0;

	private ProfileProcessor configuration;

	/**
	 * Provides a command line interface to the validator.
	 */
	public static void main(String[] args) {
		try {
			new UAProfValidatorAll();
		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	UAProfValidatorAll() throws IOException {
		String welcomeMsg = "DELI Validator http://delicon.sourceforge.net/ \n"
				+ "Running automated test for all profiles specified in profiles.n3 \n"
				+ "IMPORTANT If you run this behind a firewall you must configure proxy e.g. \n"
				+ "java -Dhttp.proxyHost=<hostaddress> -Dhttp.proxyPort=<hostport> com.hp.hpl.deli.UAProfValidatorAll \n \n";
		outputMsg(welcomeMsg);

		configuration = new ProfileProcessor(Constants.VALIDATOR_CONFIG_FILE);
		Class<UAProfValidatorAll.Worker> clazz = UAProfValidatorAll.Worker.class;
		Constructor<UAProfValidatorAll.Worker> ctor = null;
		try {
			ctor = clazz.getConstructor(UAProfValidatorAll.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		new Crawler(Constants.ALL_KNOWN_UAPROF_PROFILES, 100, ctor, this);
		outputMsg("Processing statistics:");
		outputMsg(validProfiles + " valid profiles");
		outputMsg(invalidProfiles + " invalid profiles");
		outputMsg(unreachableProfiles + " unreachable profiles");
		outputMsg(invalidRDF + " profiles which were invalid RDF/XML");
	}

	static void outputMsg(String s) {
		System.out.println(s);
	}

	class Worker extends CrawlerWorker {

		class ProcessProfile {
			private String profileUrl;

			private StringBuffer messages = new StringBuffer();

			private boolean profileValidFlag = true;

			private boolean unreachable = false;

			private Model model = ModelFactory.createDefaultModel();

			private JenaReader myARPReader = new JenaReader();

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

			public boolean process(Resource device) {
				DeviceData deviceData = new DeviceData(device);
				String manufacturer = deviceData.hasManufacturer() ? deviceData.getManufacturer() : "";
				String deviceName = deviceData.hasDeviceName() ? deviceData.getDeviceName() : "";

				if (deviceData.hasProvider()) {
					outputMsg("PROFILE NOT CREATED BY VENDOR - PROVIDER: " + deviceData.getProvider());
				}

				outputMsg("MANUFACTURER: " + manufacturer + "     DEVICE NAME:  "
						+ deviceName);
				String profileUri = device.getURI();
				if (profileUri.startsWith("http:")) {
					try {
						StringReader profile = new StringReader(UrlUtils.downloadFileToLocalDir(profileUri));
						outputMsg(profileUrl);
						try {
							myARPReader.read(model, profile, profileUrl);
							if (!unreachable) {
								ValidateProfile validated = null;
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

			void outputMsg(String s) {
				messages.append(s + "\n");
			}
		}

		public Worker() {
		}

		void processURI(Resource profileData) {
			new ProcessProfile().process(profileData);
		}

	}

}