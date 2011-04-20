package com.hp.hpl.deli;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A CC/PP profile represented as a set of Attributes.
 */
public class Profile {
	/** The profile data. */
	Vector<Attribute> data = new Vector<Attribute>();

	/** Attribute order. */
	HashMap<Resource, Vector<Integer>> attributePosition;

	public Vector<Attribute> get() {
		return data;
	}

	public Attribute get(int i) {
		return data.get(i);
	}

	public int size() {
		return data.size();
	}

	public Profile(SchemaCollection vocabulary, Vector<Attribute> attributes) {
		// MERGE DUPLICATE ENTRIES APPROPRIATELY
		if (attributePosition == null) {
			attributePosition = new HashMap<Resource, Vector<Integer>>();
		}

		for (Attribute pa : attributes) {
			Resource pName = pa.getName();
			if (attributePosition.containsKey(pName)) {
				Vector<Integer> list = attributePosition.get(pName);
				boolean resolved = false;
				for (int currentIntRef : list) {
					Attribute currentPa = (Attribute) data.get(currentIntRef);

					try {
						Resource paURI = vocabulary.getAttributeProperty(pName,
							Constants.COMPONENT);
						Resource currentPaURI = vocabulary.getAttributeProperty(
							currentPa.getName(), Constants.COMPONENT);
						if (paURI.equals(currentPaURI)) {
							((Attribute) data.get(currentIntRef)).mergeAttribute(pa);
							resolved = true;
						}
					} catch (VocabularyException ve) {//
					}
				}

				if (!resolved) {
					(attributePosition.get(pName)).add(new Integer(data.size()));
					data.add(pa);
				}
			} else {
				Vector<Integer> v = new Vector<Integer>();
				v.add(new Integer(data.size()));
				attributePosition.put(pa.getName(), v);
				data.add(pa);
			}
		}
	}

	/**
	 * Retrieve a profile attribute with a specific name.
	 * 
	 * @param attributeName The attribute qname.
	 * @return The profile attribute. Returns null is no such attribute. 
	 */

	// note this returns the _first_ attribute with that qname!! (if it exists).
	public Attribute getAttribute(Resource attributeName) {
		if (attributePosition.get(attributeName) != null) {
			Integer i = (attributePosition.get(attributeName)).firstElement();
			return (Attribute) data.get(i.intValue());
		}
		return null;
	}

	/**
	 * Retrieve a profile attribute with a specific name. (assumes that the
	 * attribute name is unique, regardless of URI: returns the first value
	 * encountered with qname fragment equal to that passed in).
	 * 
	 * @param attributeName the UNQUALIFIED attribute name (I.E. just the
	 *            fragment)
	 * @return The profile attribute. Returns null if no such attribute. 
	 */
	public Attribute getAttribute(String attributeName) {
		if (attributePosition == null) {
			attributePosition = new HashMap<Resource, Vector<Integer>>();
		}

		Set<Resource> keys = attributePosition.keySet();
		for (Resource qn : keys) {
			if (qn.getLocalName().equals(attributeName)) {
				return data.get(((Integer) (attributePosition.get(qn)).firstElement())
						.intValue());
			}
		}

		return null;
	}

	/**
	 * Converts the object to a String.
	 * 
	 * @return The Profile as a String.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (Attribute p : data) {
			result.append(p.toString());
		}
		return result.toString();
	}
}
