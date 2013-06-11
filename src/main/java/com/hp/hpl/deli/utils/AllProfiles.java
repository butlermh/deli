package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class AllProfiles {
	/** Logging. */
	static Log log = LogFactory.getLog(AllProfiles.class);

	private Model profiles;

	private Manufacturers manufacturers;

	private ExcludeHosts excludedHost = new ExcludeHosts();

	private List<Resource> crawlDb = Collections
			.synchronizedList(new LinkedList<Resource>());

	HashSet<String> unretrievableProfiles = new HashSet<String>();

	AllProfiles(Model profiles) {
		this.profiles = profiles;
		this.manufacturers = new Manufacturers(profiles);
		ResIterator profilesIter = profiles
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			if (!excludedHost.excludedHost(resource.getURI())) {
				crawlDb.add(resource);
			} else {
				resource.removeProperties();
			}
		}
		try {
			String profilesTxt = UrlUtils.loadLocalFile(
					"config/unretrievableProfiles.txt").toString();
			String[] values = profilesTxt.split("\n");
			for (String url : values) {
				unretrievableProfiles.add(url.trim());
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	List<Resource> getCrawlDb() {
		return crawlDb;
	}

	void addDeviceIfNotAlreadyKnown(String deviceURI) {
		if (!excludedHost.excludedHost(deviceURI)) {
			if (deviceURI.startsWith("http")) {
				try {
					new URI(deviceURI);
					Resource p = profiles.createResource(deviceURI);
					StmtIterator profilesIter = p.listProperties(RDF.type);
					if (!profilesIter.hasNext()) {
						p = profiles.createResource(deviceURI.toLowerCase());
						profilesIter = p.listProperties(RDF.type);
						if (!profilesIter.hasNext()) {
							if (!unretrievableProfiles.contains(deviceURI.trim())) {
								Resource resource = profiles.createResource(deviceURI);
								profiles.add(resource, RDF.type, DeliSchema.Profile);
								System.out.println("Adding new profile "
										+ resource.getURI());
								crawlDb.add(resource);
							} else {
								System.out.println("Excluding " + deviceURI + " because it is unretrievable");
							}
						}
					}
				} catch (Exception invalidURI) {
					//
				}
			}
		}
	}

	void fixMetadata(Resource device, String profile) {
		DeviceData deviceData = new DeviceData(device, profile);
		if (deviceData.getManufacturer() != null) {
			String manufacturer = manufacturers.get(deviceData.getManufacturer());
			if (manufacturer != null) {
				Resource manufacturerResource = profiles.createResource(manufacturer);
				profiles.removeAll(device, DeliSchema.manufacturedBy, (RDFNode) null);
				profiles.add(device, DeliSchema.manufacturedBy, manufacturerResource);
				profiles.add(manufacturerResource, RDFS.label,
						deviceData.getManufacturer());
				profiles.removeAll(device, DeliSchema.deviceName, (RDFNode) null);
				profiles.add(device, DeliSchema.deviceName, deviceData.getDeviceName());
				profiles.add(device, RDF.type, DeliSchema.Profile);
			} else {
				log.info("ERROR: Could not find manufacturer URI for ["
						+ deviceData.getManufacturer() + "] for " + device.getURI());
			}
		}
	}
}
