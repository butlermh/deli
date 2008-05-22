package com.hp.hpl.deli.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.Utils;
import com.hp.hpl.deli.Workspace;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * This class loads all the profiles listed in profiles.n3 and creates a single RDF file from them.
 */
public class UAProfHarvester extends Utils {

	/**
	 * Command line interface.
	 * 
	 * @param args Does not require any arguments.
	 */
	public static void main(String[] args) {
		try {
			Workspace.getInstance().configure(null, Constants.VALIDATOR_CONFIG_FILE);
			Model profiles = Utils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);

			System.out.println("DELI UAProf Harvester");
			Model allProfileData = ModelFactory.createDefaultModel();
			ResIterator profilesIter = profiles.listSubjectsWithProperty(RDF.type, DeliSchema.Profile);
			while (profilesIter.hasNext()) {
				try {
					Resource profileData = profilesIter.nextResource();
					String manufacturer = getPropertyString((Resource) profileData.getProperty(DeliSchema.manufacturedBy).getObject(), RDFS.label);
					if (!manufacturer.equals("DELI")) {
						if (profileData.hasProperty(DeliSchema.uaprofUri)) {
							printProfileInformation(profileData, manufacturer);
							allProfileData.add(getProfile(profileData));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.toString());
				}
			}
			System.out.println("Writing out profile data");
			RDFWriter writer = allProfileData.getWriter("RDF/XML");
			writer.setProperty("allowBadURIs", "true");
			OutputStream out = new FileOutputStream(Constants.ALL_PROFILES_RDF);
			writer.write(allProfileData, out, null);
			out.close();

		} catch (IOException f) {
			System.out.println("DELI error:" + f.toString());
		}
	}

	/**
	 * Retrieve the profile.
	 * 
	 * @param p A resource with information about the profile.
	 * @return The profile.
	 */
	private static Model getProfile(Resource p) {
		JenaReader arpReader;
		arpReader = new JenaReader();
		arpReader.setErrorHandler(new RDFErrorHandler() {
			// ARP parser error handling routines
			public void warning(Exception e) {//
			}

			public void error(Exception e) {//
			}

			public void fatalError(Exception e) {//
			}
		});
		arpReader.setProperty("WARN_RESOLVING_URI_AGAINST_EMPTY_BASE", "EM_IGNORE");
		Model profile = ModelFactory.createDefaultModel();
		String profileUri = getPropertyUri(p, DeliSchema.uaprofUri);
		System.out.println(profileUri);
		InputStream in = Workspace.getInstance().getResource(profileUri);
		arpReader.read(profile, in, profileUri);
		return profile;
	}

	/**
	 * Print information about the profile.
	 * 
	 * @param p A resouce with information about the profile.
	 * @param manufacturer The manufacturer.
	 */
	private static void printProfileInformation(Resource p, String manufacturer) {
		if (p.hasProperty(DeliSchema.provider)) {
			String provider = getPropertyUri(p, DeliSchema.provider);
			System.out.println("PROFILE NOT CREATED BY VENDOR - PROVIDER: " + provider);
		}
		String deviceName = getPropertyString(p, DeliSchema.deviceName);
		System.out.println("MANUFACTURER: " + manufacturer);
		System.out.println("DEVICE NAME:  " + deviceName);
	}
}
