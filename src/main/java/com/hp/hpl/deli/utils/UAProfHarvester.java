package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

// Is this class really needed? It could be combined with UAProfValidatorAll?

/**
 * This class loads all the profiles listed in profiles.n3 and creates a single
 * RDF file from them.
 */
public class UAProfHarvester {
	private static Log log = LogFactory.getLog(UAProfHarvester.class);

	private List<Resource> crawlDb;

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
	 * Command line interface.
	 * 
	 * @param args Does not require any arguments.
	 */
	public static void main(String[] args) {
		try {
			new UAProfHarvester();
		} catch (IOException f) {
			log.error("DELI error:" + f.toString());
		}
	}

	private UAProfHarvester() throws IOException {
		log.info("DELI UAProf Harvester");

		CreateCrawlDb createDb = new CreateCrawlDb(Constants.ALL_KNOWN_UAPROF_PROFILES);
		crawlDb = createDb.getCrawlDb();
		Class<UAProfHarvester.Worker> clazz = UAProfHarvester.Worker.class;
		Constructor<UAProfHarvester.Worker> ctor = null;
		try {
			ctor = clazz.getConstructor(UAProfHarvester.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		new Crawler(crawlDb, 10, ctor, this);

		// write out the profile data
		log.info("Writing out profile data");
		ModelUtils.writeModel(allProfileData.getModel(), Constants.ALL_PROFILES_RDF,
				"RDF/XML");
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
			String manufacturer = "";
			if (profileData.hasProperty(DeliSchema.manufacturedBy)) {
				Resource manufacturerURI = profileData.getProperty(DeliSchema.manufacturedBy)
					.getObject().asResource();
				manufacturer = ModelUtils.getPropertyString(manufacturerURI, RDFS.label);
			}
			
			if (profileData.hasProperty(DeliSchema.provider)) {
				String provider = ModelUtils.getPropertyUri(profileData, DeliSchema.provider);
				log.info("PROFILE NOT CREATED BY VENDOR - PROVIDER: " + provider);
			}
			
			String deviceName = "";
			if (profileData.hasProperty(DeliSchema.deviceName)) {
				deviceName = ModelUtils.getPropertyString(profileData, DeliSchema.deviceName);
			}
			log.info("MANUFACTURER: " + manufacturer + "     DEVICE NAME:  " + deviceName);
			return profileData.getURI();
		}
	}
}
