package com.hp.hpl.deli;

import java.io.IOException;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.shared.JenaException;

/**
 * Check that a UAProf profile is valid.
 */
public class UAProfValidator {

	private boolean profileValidFlag = true;

	private boolean unreachable = false;

	private JenaReader myARPReader = new JenaReader();

	private Model model = ModelFactory.createDefaultModel();

	private ProfileProcessor configuration;

	/**
	 * Provides a command line interface to the validator.
	 * 
	 * @param args a profile to be validated by the processor.
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 1) {
				new UAProfValidator(args[0]);
			} else {
				System.out
						.println("Must supply one argument: UAProfValidator <uaprofilefile>");
			}

		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	public UAProfValidator(String profileUrl) throws IOException {
		outputMsg("Loading profile " + profileUrl);
		configuration = new ProfileProcessor(Constants.VALIDATOR_CONFIG_FILE);
		myARPReader.setProperty("WARN_RESOLVING_URI_AGAINST_EMPTY_BASE", "EM_IGNORE");
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

		try {
			myARPReader.read(model, ModelUtils.getResource(profileUrl), "");
		} catch (JenaException e) {
			outputMsg("Could not parse profile " + profileUrl);
			profileValidFlag = false;
		} catch (Exception e) {
			outputMsg("Could not load profile " + profileUrl);
			unreachable = true;
			profileValidFlag = false;
			outputMsg("PROFILE IS UNREACHABLE");
		}
		if (!unreachable && profileValidFlag) {
			try {
				profileValidFlag = new ValidateProfile(configuration, model)
						.isProfileValid();
			} catch (Exception e) {
				e.printStackTrace();
				profileValidFlag = false;
				outputMsg(e.toString());
			}

			if (profileValidFlag) {
				outputMsg("PROFILE IS VALID");
			} else {
				outputMsg("PROFILE IS NOT VALID");
			}
		}
	}

	void outputMsg(String s) {
		System.out.println(s);
	}
}