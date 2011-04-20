package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ListProperties {

	private static boolean useQnamesOnly = true;
	
	private StringBuffer result = new StringBuffer();

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

	Vector<PropertyEntry> sortPropertiesHashMap(HashMap<String, HashMap<String, Integer>> entries) {
		Vector<PropertyEntry> vector = new Vector<PropertyEntry>();
		for (String entryName : entries.keySet()) {
			HashMap<String, Integer> propertyValues = entries.get(entryName);
			PropertyEntry entry = new PropertyEntry(entryName, propertyValues);
			vector.add(entry);
		}
		Collections.sort(vector);
		return vector;
	}

	private void addPropertyValue(HashMap<String, HashMap<String, Integer>> properties, String property, RDFNode node) {
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

	private ListProperties() throws FileNotFoundException {
		Model profiles = ModelFactory.createDefaultModel();
		File input = new File(Constants.ALL_PROFILES_RDF);
		profiles.read(new FileInputStream(input), "");
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

		// print out results as dendrogram

		result.append("<html><head></head><body>\n");
		result.append("<table>\n");
		Vector<PropertyEntry> sortedProperties = sortPropertiesHashMap(properties);
		for (PropertyEntry property : sortedProperties) {
			result.append("<tr><td>" + property.name + "</td><td></td><td></td></tr>\n");
			HashMap<String, Integer> propertyValues = property.map;
			Vector<PropertyValueEntry> sortedValues = sortPropertyValuesHashMap(propertyValues);
			for (PropertyValueEntry entry : sortedValues) {
				result.append("<tr><td></td><td>" + entry.name + "</td><td>" + entry.frequency + "</td></tr>\n");
			}
		}
		result.append("</table></body></html>\n");
		OutputStream out = new FileOutputStream(Constants.PROPERTIES_OUTPUT_FILE);
		// out.
		byte[] bytes = result.toString().getBytes();
		try {
			out.write(bytes);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(PropertyEntry other) {
			return other.name.compareTo(name);
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
