package com.hp.hpl.deli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Validate a profile using the schemas it references
 */
public class UAProfValidator extends Utils {

	public static String deliVersionNumber = "deli-x070105";

	private static Log log = LogFactory.getLog(UAProfValidator.class);

	private int validProfiles = 0;

	private int invalidProfiles = 0;

	private int unreachableProfiles = 0;

	private int invalidRDF = 0;

	private boolean currentProfileValidRDF;

	private boolean profileValidFlag = false;

	private boolean truststamp = false;

	private JenaReader myARPReader;

	UAProfValidator() {
		myARPReader = new JenaReader();
		myARPReader.setProperty("WARN_RESOLVING_URI_AGAINST_EMPTY_BASE", "EM_IGNORE");
		myARPReader.setErrorHandler(new RDFErrorHandler() {
			// ARP parser error handling routines
			public void warning(Exception e) {
				System.out.println("RDF parser warning:" + e.getMessage());
			}

			public void error(Exception e) {
				System.out.println("RDF parser error:" + e.getMessage());

				if (currentProfileValidRDF) {
					invalidRDF++;
					currentProfileValidRDF = false;
				}

				profileValidFlag = false;
			}

			public void fatalError(Exception e) {
				error(e);
			}
		});
	}

	void setTruststamp(boolean truststamp) {
		this.truststamp = truststamp;
	}

	boolean process(String profile) {
		if (!truststamp) {
			System.out.println("PROFILE:      " + profile);
		}

		profileValidFlag = true;
		currentProfileValidRDF = true;

		boolean unreachable = false;

		Model model = ModelFactory.createDefaultModel();

		// Read and parse the RDF document
		try {
			InputStream in = Workspace.getInstance().getResource(profile);
			myARPReader.read(model, in, "");
		} catch (JenaException e) {
			System.out.println("Could not parse profile " + profile);
			profileValidFlag = false;
			invalidRDF++;
		} catch (Exception e) {
			System.out.println("Could not load profile " + profile);
			profileValidFlag = false;
			unreachableProfiles++;
			System.out.println("PROFILE IS UNREACHABLE");
			unreachable = true;
		}

		if (!unreachable) {
			// Validate document

			if (truststamp) {
				Date today = new Date();
				System.out.println("OMA BAC-UAPROF " + deliVersionNumber + " validated - " + today);
				System.out.println();
			}

			try {
				Profile deliProfile = new Profile(model);
				profileValidFlag = deliProfile.isProfileValid();
			} catch (Exception e) {
				profileValidFlag = false;
				log.error(e.toString(), e);
			}

			if (profileValidFlag) {
				System.out.println("PROFILE IS VALID");
				validProfiles++;
			} else {
				System.out.println("PROFILE IS NOT VALID");
				invalidProfiles++;
			}
		}

		System.out.println();

		return profileValidFlag;
	}

	void results() {
		if (!truststamp) {
			System.out.println("Processing statistics:");
			System.out.println(validProfiles + " valid profiles");
			System.out.println(invalidProfiles + " invalid profiles");
			System.out.println(unreachableProfiles + " unreachable profiles");
			System.out.println(invalidRDF + " profiles which were invalid RDF/XML");
		}
	}

	private static String welcomeMsg = "DELI Validator http://delicon.sourceforge.net/ \n"
			+ "Running automated test for all profiles specified in profiles.rdf \n"
			+ "IMPORTANT If you run this behind a firewall you must configure proxy e.g. \n"
			+ "java -Dhttp.proxyHost=<hostaddress> -Dhttp.proxyPort=<hostport> com.hp.hpl.deli.UAProfValidator \n \n";

	/**
	 * Provides a command line interface to the validator.
	 * 
	 * @param args a list of profiles to validate seperated by whitespace
	 */
	public static void main(String[] args) {
		try {
			UAProfValidator v = new UAProfValidator();
			Workspace.getInstance().configure(null, Constants.VALIDATOR_CONFIG_FILE);

			if (args.length < 1) {
				// no arguments, so try to retrieve all the profiles in
				// profiles.rdf and validate them

				System.out.println(welcomeMsg);

				Model profiles = Utils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);
				ResIterator profilesIter = profiles.listSubjectsWithProperty(RDF.type, DeliSchema.Profile);

				while (profilesIter.hasNext()) {
					Resource p = profilesIter.nextResource();
					Resource m = (Resource) p.getProperty(DeliSchema.manufacturedBy).getObject();
					if (m.hasProperty(RDFS.label)) {
						if (!getPropertyString(m, RDFS.label).equals("DELI")) {

							if (p.getProperty(DeliSchema.uaprofUri) != null) {
								if (p.getProperty(DeliSchema.provider) != null) {
									System.out.println("PROFILE NOT CREATED BY VENDOR - PROVIDER: "
											+ p.getProperty(DeliSchema.provider).getProperty(RDFS.label).getObject()
													.toString());
								}
								System.out.println("MANUFACTURER: " + getPropertyString(m, RDFS.label));
								System.out.println("DEVICE NAME:  " + getPropertyString(p, DeliSchema.deviceName));

								String profileUri = getPropertyUri(p, DeliSchema.uaprofUri);
								v.process(profileUri);

								if (profileUri.startsWith("http:")) {
									download(profileUri);
								}
							}
						}
					}
				}
			} else {
				if (args[0].equals("truststamp") && args.length == 2) {
					v.setTruststamp(true);
					System.out.println("<!--");
					v.process(args[1]);
					System.out.println("-->");
				} else {
					for (String s : args) {
						v.process(s);
					}
				}
			}

			v.results();
		} catch (Exception f) {
			f.printStackTrace();
			System.out.println("DELI error:" + f.toString());
		}
	}

	static String download(String uri) {
		InputStream s = null;
		StringBuffer b = new StringBuffer();

		try {
			URL theURL = new URL(uri);
			String filepath = "build/manufacturerProfiles/" + theURL.getHost() + theURL.getFile();
			String path = filepath.substring(0, filepath.lastIndexOf('/'));
			File theParent = new File(path);
			theParent.mkdirs();

			File theFile = new File(filepath);
			theFile.createNewFile();

			FileOutputStream theFileStream = new FileOutputStream(theFile);

			s = theURL.openStream();

			int ch;

			while ((ch = s.read()) != -1) {
				b.append((char) ch);
				theFileStream.write(ch);
			}

			s.close();
			theFileStream.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return b.toString();
	}
}