package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Creates a list of all the UAProf properties that are used in all known
 * profiles. This file is called after UAProfHarverster, and will fail if
 * UAProfHarverster is not called first.
 */
public class ListProperties {

	private static Log log = LogFactory.getLog(ListProperties.class);

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

	private static boolean useQnamesOnly = true;

	Vector<PropertyValueEntry> sortPropertyValuesHashMap(HashMap<String, Integer> entries) {
		Vector<PropertyValueEntry> vector = new Vector<PropertyValueEntry>();
		for (String entryName : entries.keySet()) {
			int frequency = entries.get(entryName).intValue();
			PropertyValueEntry entry = new PropertyValueEntry(entryName, frequency);
			vector.add(entry);
		}
		Collections.sort(vector);
		return vector;
	}

	Vector<PropertyEntry> sortPropertiesHashMap(
			HashMap<String, HashMap<String, Integer>> entries) {
		Vector<PropertyEntry> vector = new Vector<PropertyEntry>();
		for (String entryName : entries.keySet()) {
			HashMap<String, Integer> propertyValues = entries.get(entryName);
			PropertyEntry entry = new PropertyEntry(entryName, propertyValues);
			vector.add(entry);
		}
		Collections.sort(vector);
		return vector;
	}

	private void addPropertyValue(HashMap<String, HashMap<String, Integer>> properties,
			String property, RDFNode node) {
		String litValue = ((Literal) node.as(Literal.class)).getLexicalForm().trim();
		if (properties.containsKey(property)) {
			HashMap<String, Integer> propertyValues = properties.get(property);
			if (propertyValues.containsKey(litValue)) {
				int count = ((Integer) propertyValues.get(litValue)).intValue();
				count++;
				propertyValues.put(litValue, new Integer(count));
			} else {
				propertyValues.put(litValue, new Integer(1));
			}
			properties.put(property, propertyValues);
		} else {
			HashMap<String, Integer> propertyValues = new HashMap<String, Integer>();
			propertyValues.put(litValue, new Integer(1));
			properties.put(property, propertyValues);
		}
	}

	private Model createModelOfAllProfiles() throws IOException {
		CreateCrawlDb createDb = new CreateCrawlDb(Constants.ALL_KNOWN_UAPROF_PROFILES);
		crawlDb = createDb.getCrawlDb();
		Class<ListProperties.Worker> clazz = ListProperties.Worker.class;
		Constructor<ListProperties.Worker> ctor = null;
		try {
			ctor = clazz.getConstructor(ListProperties.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		new Crawler(crawlDb, 10, ctor, this);

		// write out the profile data
		log.info("Writing out profile data");
		ModelUtils.writeModel(allProfileData.getModel(), Constants.ALL_PROFILES_RDF,
				"RDF/XML");
		return allProfileData.getModel();
	}
	
	private HashMap<String, HashMap<String, Integer>> createPropertyStructure(Model profiles) {
		HashMap<String, HashMap<String, Integer>> properties = new HashMap<String, HashMap<String, Integer>>();
		StmtIterator statements = profiles.listStatements();
		while (statements.hasNext()) {
			Statement statement = statements.nextStatement();
			if (!statement.getSubject().isAnon()) {
				String property;
				if (!useQnamesOnly)
					property = statement.getPredicate().getURI();
				else
					property = statement.getPredicate().getLocalName();
				RDFNode propertyValue = statement.getObject();
				if (propertyValue.canAs(Literal.class)) {
					addPropertyValue(properties, property, propertyValue);
				} else {
					if (propertyValue.canAs(Bag.class)) {
						Bag bag = (Bag) propertyValue.as(Bag.class);
						NodeIterator i = bag.iterator();
						while (i.hasNext()) {
							RDFNode node = i.nextNode();
							if (node.canAs(Literal.class)) {
								addPropertyValue(properties, property, node);
							}
						}
					} else if (propertyValue.canAs(Seq.class)) {
						Seq seq = (Seq) propertyValue.as(Seq.class);
						NodeIterator i = seq.iterator();
						while (i.hasNext()) {
							RDFNode node = i.nextNode();
							if (node.canAs(Literal.class)) {
								addPropertyValue(properties, property, node);
							}
						}
					}
				}
			}
		}
		return properties;
	}
	
	private StringBuffer printResults(HashMap<String, HashMap<String, Integer>> properties) {
		StringBuffer result = new StringBuffer();
		result.append("<html><head></head><body>\n");
		result.append("<table>\n");
		Vector<PropertyEntry> sortedProperties = sortPropertiesHashMap(properties);
		for (PropertyEntry property : sortedProperties) {
			result.append("<tr><td>" + property.name + "</td><td></td><td></td></tr>\n");
			HashMap<String, Integer> propertyValues = property.map;
			Vector<PropertyValueEntry> sortedValues = sortPropertyValuesHashMap(propertyValues);
			for (PropertyValueEntry entry : sortedValues) {
				result.append("<tr><td></td><td>" + entry.name + "</td><td>"
						+ entry.frequency + "</td></tr>\n");
			}
		}
		result.append("</table></body></html>\n");
		return result;
	}
	
	private ListProperties() throws IOException {
		log.info("This program retrieves all known profiles, builds a single RDF model from that data, and then creates a dendrogram of the properties used.");

		Model model = createModelOfAllProfiles();
		HashMap<String, HashMap<String, Integer>> properties = createPropertyStructure(model);
		StringBuffer result = printResults(properties);
		SavePage.savePage(Constants.PROPERTIES_OUTPUT_FILE, result);
	}

	private class PropertyValueEntry implements Comparable<PropertyValueEntry> {
		private String name;

		private int frequency;

		PropertyValueEntry(String name, int frequency) {
			this.name = name;
			this.frequency = frequency;
		}

		public int compareTo(PropertyValueEntry other) {
			int r = other.frequency - frequency;
			if (r == 0)
				return other.name.compareTo(name);
			return r;
		}
	}

	private class PropertyEntry implements Comparable<PropertyEntry> {
		private String name;

		private HashMap<String, Integer> map;

		PropertyEntry(String name, HashMap<String, Integer> map) {
			this.name = name;
			this.map = map;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(PropertyEntry other) {
			return other.name.compareTo(name);
		}
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
				Resource manufacturerURI = profileData
						.getProperty(DeliSchema.manufacturedBy).getObject().asResource();
				manufacturer = ModelUtils.getPropertyString(manufacturerURI, RDFS.label);
			}

			if (profileData.hasProperty(DeliSchema.provider)) {
				String provider = ModelUtils.getPropertyUri(profileData,
						DeliSchema.provider);
				log.info("PROFILE NOT CREATED BY VENDOR - PROVIDER: " + provider);
			}

			String deviceName = "";
			if (profileData.hasProperty(DeliSchema.deviceName)) {
				deviceName = ModelUtils.getPropertyString(profileData,
						DeliSchema.deviceName);
			}
			log.info("MANUFACTURER: " + manufacturer + "     DEVICE NAME:  " + deviceName);
			return profileData.getURI();
		}
	}

	public static void main(String[] args) {
		try {
			new ListProperties();
		} catch (Exception e) {
			//
		}
	}
}
