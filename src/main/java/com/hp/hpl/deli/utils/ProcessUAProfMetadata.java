package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Creates a list of all the UAProf properties that are used in all known
 * profiles. 
 */
public class ProcessUAProfMetadata {

	private static Log log = LogFactory.getLog(ProcessUAProfMetadata.class);

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

	private Model createModelOfAllProfiles(Model model) throws IOException {
		Class<ProcessUAProfMetadata.Worker> clazz = ProcessUAProfMetadata.Worker.class;
		Constructor<ProcessUAProfMetadata.Worker> ctor = null;
		try {
			ctor = clazz.getConstructor(ProcessUAProfMetadata.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		new Crawler(model, 10, ctor, this);

		// write out the profile data
		log.info("Writing out profile data");
		ModelUtils.writeModel(allProfileData.getModel(), Constants.ALL_PROFILES_RDF,
				"RDF/XML");
		return allProfileData.getModel();
	}

	private ProcessUAProfMetadata(Model prfs) throws IOException {
		log.info("This program retrieves all known profiles, builds a single RDF model from that data, and then creates a dendrogram of the properties used.");
		Model model = createModelOfAllProfiles(prfs);
		ProcessProperties p = new ProcessProperties();
		HashMap<String, HashMap<String, Integer>> properties = p
				.createPropertyStructure(model);
		StringBuffer result = p.printResults(properties);
		UrlUtils.savePage(Constants.PROPERTIES_OUTPUT_FILE, result);
	}

	class Worker extends CrawlerWorker {
		JenaReader arpReader = ModelUtils.configureArp();

		public Worker() {
			super();
		}

		void processURI(Resource profileData) {
			try {
				if (profileData.hasProperty(DeliSchema.uaprofUri)) {
					String profileUri = processProfileDatabase(profileData);
					Model model = ModelFactory.createDefaultModel();
					InputStream in = ModelUtils.getResource(profileUri);
					arpReader.read(model, in, profileUri);
					allProfileData.add(model);
				}
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
		
		public String processProfileDatabase(Resource profileData) {
			DeviceData device = new DeviceData(profileData);
			String manufacturer = device.hasManufacturer() ? device.getManufacturer() : "";
			String deviceName = device.hasDeviceName() ? device.getDeviceName() : "";

			if (device.hasProvider()) {
				log.info("PROFILE NOT CREATED BY VENDOR - PROVIDER: " + device.getProvider());
			}

			log.info("MANUFACTURER: " + manufacturer + "     DEVICE NAME:  " + deviceName);
			return profileData.getURI();
		}
	}

	public static void main(String[] args) {
		try {
			Model model = ModelUtils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);
			new ProcessUAProfMetadata(model);
			new CreateHTML(model);
		} catch (Exception e) {
			//
		}
	}
}
