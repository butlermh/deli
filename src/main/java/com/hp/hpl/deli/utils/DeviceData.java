package com.hp.hpl.deli.utils;

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

class DeviceData {
	private Resource device;
	
	DeviceData(Resource device) {
		this.device = device;
	}
	
	Resource getManufacturerURI() {
		return device.getProperty(DeliSchema.manufacturedBy).getResource();
	}
	
	String getManufacturer() {
		return device.getProperty(DeliSchema.manufacturedBy).getResource().getProperty(RDFS.label).getString();
	}
	
	String getDeviceName() {
		return device.getProperty(DeliSchema.deviceName).getString();
	}
	
	String getProvider() {
		return device.getProperty(DeliSchema.provider).getResource().getURI();
	}
	
	boolean hasManufacturer() {
		return device.hasProperty(DeliSchema.manufacturedBy);
	}

	boolean hasProvider() {
		return device.hasProperty(DeliSchema.provider);
	}
	
	boolean hasDeviceName() {
		return device.hasProperty(DeliSchema.deviceName);
	}
	
	boolean hasRelease() {
		return device.hasProperty(DeliSchema.release);
	}
	
	String getRelease() {
		return device.getProperty(DeliSchema.release).getString();
	}
}
