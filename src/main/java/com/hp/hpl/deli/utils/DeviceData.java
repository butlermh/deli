package com.hp.hpl.deli.utils;	

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

class DeviceData {
	private Resource device;
	
	private String deviceName = null;
	
	private String manufacturerName = null;
	
	DeviceData(Resource device, String profile) {
		this(device);
		String manufacturerNameFromProfile = TagSoupProcessor.getTag(profile,
		"Vendor");
		String deviceNameFromProfile = TagSoupProcessor.getTag(profile, "Model");
		if (manufacturerNameFromProfile != null) {
			manufacturerName = manufacturerNameFromProfile;
		}
		if (deviceNameFromProfile != null) {
			deviceName = deviceNameFromProfile;
		}
	}
	
	DeviceData(Resource device) {
		this.device = device;
		if (device.hasProperty(DeliSchema.manufacturedBy)) {
			if (device.getProperty(DeliSchema.manufacturedBy).getResource().hasProperty(RDFS.label)) {
		this.manufacturerName = device.getProperty(DeliSchema.manufacturedBy).getResource().getProperty(RDFS.label).getString();
			}
		}
		if (device.hasProperty(DeliSchema.deviceName)) {
			this.deviceName = device.getProperty(DeliSchema.deviceName).getString();
		}
	}
		
	String getManufacturer() {
		return manufacturerName;
	}
	
	String getDeviceName() {
		return deviceName;
	}
	
	String getProvider() {
		return device.getProperty(DeliSchema.provider).getResource().getURI();
	}
	
	boolean hasProvider() {
		return device.hasProperty(DeliSchema.provider);
	}
	
	Resource getDevice() {
		return device;
	}

	boolean hasRelease() {
		return device.hasProperty(DeliSchema.release);
	}
	
	String getRelease() {
		return device.getProperty(DeliSchema.release).getString();
	}
}
