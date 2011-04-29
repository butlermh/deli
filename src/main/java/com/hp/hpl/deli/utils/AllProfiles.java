package com.hp.hpl.deli.utils;

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class AllProfiles {
	private Model profiles;

	AllProfiles(Model profiles) {
		this.profiles = profiles;
	}

	boolean isDeviceAlreadyKnown(String deviceURI) {
		Resource p = profiles.createResource(deviceURI);
		ResIterator profilesIter = profiles.listSubjectsWithProperty(RDF.type,
				DeliSchema.Profile);
		if (profilesIter.hasNext()) {
			return true;
		}
		p = profiles.createResource(deviceURI.toLowerCase());
		profilesIter = profiles.listSubjectsWithProperty(DeliSchema.uaprofUri, p);
		if (profilesIter.hasNext()) {
			return true;
		}
		return false;
	}

	Resource addProfile(String newURI) {
		Resource resource = profiles.createResource(newURI);
		profiles.add(resource, RDF.type, DeliSchema.Profile);
		return resource;
	}

	void fixMetadata(DeviceData deviceData, String manufacturer) {
		Resource device = deviceData.getDevice();
		Resource manufacturerResource = profiles.createResource(manufacturer);
		profiles.removeAll(device, DeliSchema.manufacturedBy, (RDFNode) null);
		profiles.add(device, DeliSchema.manufacturedBy, manufacturerResource);
		profiles.add(manufacturerResource, RDFS.label, deviceData.getManufacturer());
		profiles.removeAll(device, DeliSchema.deviceName, (RDFNode) null);
		profiles.add(device, DeliSchema.deviceName, deviceData.getDeviceName());
		profiles.add(device, RDF.type, DeliSchema.Profile);
	}
}
