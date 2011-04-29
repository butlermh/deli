package com.hp.hpl.deli.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
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

	AllProfiles(Model profiles) {
		this.profiles = profiles;
		this.manufacturers = new Manufacturers(profiles);
		ResIterator profilesIter = profiles
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			crawlDb.add(resource);
		}
	}

	List<Resource> getCrawlDb() {
		return crawlDb;
	}

	void addDeviceIfNotAlreadyKnown(String deviceURI) {
		if (!excludedHost.excludedHost(deviceURI)) {
			if (deviceURI.startsWith("http")) {
				Resource p = profiles.createResource(deviceURI);
				ResIterator profilesIter = profiles.listSubjectsWithProperty(RDF.type,
						DeliSchema.Profile);
				if (!profilesIter.hasNext()) {
					p = profiles.createResource(deviceURI.toLowerCase());
					profilesIter = profiles.listSubjectsWithProperty(
							DeliSchema.uaprofUri, p);
					if (!profilesIter.hasNext()) {
						Resource resource = profiles.createResource(deviceURI);
						profiles.add(resource, RDF.type, DeliSchema.Profile);
						crawlDb.add(resource);
					}
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
