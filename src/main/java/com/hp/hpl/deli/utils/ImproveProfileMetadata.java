package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ImproveProfileMetadata {

	Model profiles;

	Model transformed;
		
	String manufacturerString = "http://purl.oclc.org/NET/butlermh/deli/manufacturers#";

	ImproveProfileMetadata() throws IOException {
		profiles = ModelUtils.loadModel(Constants.ALL_KNOWN_UAPROF_PROFILES);

		transformed = ModelFactory.createDefaultModel();

		Map<String, String> fixManufacturers = new HashMap<String, String>();
		for (int i = 0; i < ScrapeGoogle.oldManufacturers.length; i++) {
			String oldManufacturer = (manufacturerString + ScrapeGoogle.oldManufacturers[i])
					.toLowerCase();
			fixManufacturers.put(oldManufacturer, ScrapeGoogle.newManufacturers[i]);
		}

		ResIterator profilesIter = profiles
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			if (resource.hasProperty(DeliSchema.uaprofUri)) {
				String theProfile = ModelUtils.getPropertyUri(resource,
						DeliSchema.uaprofUri);
				if (theProfile.startsWith("http://")) {
					Resource manufacturer = resource
							.getProperty(DeliSchema.manufacturedBy).getObject()
							.asResource();
					String manufacturerS = manufacturer.getURI().toLowerCase();
					Resource manufacturerF;
					if (fixManufacturers.containsKey(manufacturerS)) {
						manufacturerF = transformed.createResource(fixManufacturers
								.get(manufacturerS));
					} else {
						manufacturerF = manufacturer;
					}
					Resource tProfile = transformed.createResource(theProfile);
					transformed.add(transformed.createStatement(tProfile,
							DeliSchema.manufacturedBy, manufacturerF));
					transformed.add(tProfile, RDF.type, DeliSchema.Profile);

					if (manufacturer.hasProperty(RDFS.label)) {
						String manufacturerName = ModelUtils.getPropertyString(
								manufacturer, RDFS.label);
						transformed.add(transformed.createStatement(manufacturerF,
								RDFS.label, manufacturerName));
						transformed.add(transformed.createStatement(manufacturerF,
								RDF.type, DeliSchema.Manufacturer));
					}

					String deviceName = ModelUtils.getPropertyString(resource,
							DeliSchema.deviceName);
					transformed.add(transformed.createStatement(tProfile,
							DeliSchema.deviceName,
							transformed.createTypedLiteral(deviceName)));

					if (resource.hasProperty(DeliSchema.provider)) {
						Resource provider = resource.getProperty(DeliSchema.provider)
								.getObject().asResource();
						transformed.add(transformed.createStatement(tProfile,
								DeliSchema.provider, provider));
					}

					if (resource.hasProperty(DeliSchema.supportsUAProf)) {
						String supportsUAProf = ModelUtils.getPropertyString(resource,
								DeliSchema.supportsUAProf);
						if (!supportsUAProf.equals("true")) {
							transformed.add(transformed.createStatement(tProfile,
									DeliSchema.supportsUAProf, supportsUAProf));
						}
					}

					if (resource.hasProperty(DeliSchema.useragent)) {
						String useragent = ModelUtils.getPropertyString(resource,
								DeliSchema.useragent);
						transformed.add(transformed.createStatement(tProfile,
								DeliSchema.useragent,
								transformed.createTypedLiteral(useragent)));
					}

					if (resource.hasProperty(DeliSchema.release)) {
						String release = ModelUtils.getPropertyString(resource,
								DeliSchema.release);
						transformed.add(transformed.createStatement(tProfile,
								DeliSchema.release,
								transformed.createTypedLiteral(release)));
					}
				}
			}
		}

		writeTransformedModel();
	}

	void writeTransformedModel() {
		System.out.println("Writing out profile data");
		transformed.setNsPrefix("deli", DeliSchema.NS);
		transformed.setNsPrefix("rdf", RDF.getURI());
		transformed.setNsPrefix("rdfs", RDFS.getURI());
		transformed.setNsPrefix("blackberry",
				"http://www.blackberry.net/go/mobile/profiles/uaprof/");
		RDFWriter writer = transformed.getWriter("N3");
		writer.setProperty("allowBadURIs", "true");
		String filepath = Constants.ALL_KNOWN_UAPROF_PROFILES + ".transformed";
		String path = filepath.substring(0, filepath.lastIndexOf('/'));
		new File(path).mkdirs();
		OutputStream out = null;
		try {
			out = new FileOutputStream(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.write(transformed, out, null);
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			new ImproveProfileMetadata();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
