package com.hp.hpl.deli;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * A profile attribute.
 */
public class ProfileAttribute extends AbstractProcessAttribute {
	private static Log log = LogFactory.getLog(ProfileAttribute.class);
	
	private SchemaCollection vocabulary;
	
	private Resource name;

	private Resource component;

	private Vector<String> attributeValue = new Vector<String>();

	private Vector<String> defaultAttributeValue = new Vector<String>();
	
	ProfileAttribute(ProfileProcessor configuration, Statement attributeStatement,
			Resource currentComponent, boolean isDefault) {
		boolean processUndefinedAttributes = configuration.getWorkspace().get(
				DeliSchema.processUndefinedAttributes, false);
		Resource attribute = attributeStatement.getPredicate();
		this.name = vocabulary.getRealNamespace(attribute);
		this.component = vocabulary.getRealNamespace(currentComponent);
		this.vocabulary = configuration.getVocabulary();
		RDFNode value = attributeStatement.getObject();
		// this property in the vocabulary?
		try {
			vocabulary.getAttribute(name);
		} catch (VocabularyException ve) {
			if (processUndefinedAttributes) {
				String collectionType = determineContainerType(value);
				vocabulary.addAttributeToVocabulary(attribute, currentComponent.getURI(),
						collectionType);
			}
		}
		// get values
		Vector<String> attributeValues = new Vector<String>();
		if (value instanceof Literal) {
			attributeValues.add(value.asLiteral().getString().trim());
		} else {
			try {
			NodeIterator i = getContainerIterator(value.asResource());
				while (i.hasNext()) {
					RDFNode theNode = i.nextNode();
					if (theNode instanceof Literal) {
						attributeValues.add(theNode.asLiteral().getString().trim());
					} else {
						attributeValues.add(theNode.asResource().getURI().trim());
					}
				}
			} catch (ProfileProcessingException ve) {//
			}
		}
		if (isDefault) {
			defaultAttributeValue = attributeValues;
		} else {
			attributeValue = attributeValues;
		}
	}

	/**
	 * Get the attribute URI
	 * 
	 * @return The attribute URI.
	 */
	public Resource getName() {
		return name;
	}

	/**
	 * Get the profile attribute value.
	 * 
	 * @return The attribute value.
	 */
	public Vector<String> getValue() {
		return attributeValue.size() == 0 ? null : attributeValue;
	}

	/**
	 * Get the profile default attribute value.
	 * 
	 * @return The default attribute value.
	 */
	public Vector<String> getDefaultValue() {
		return defaultAttributeValue.size() == 0 ? null : defaultAttributeValue;
	}

	/**
	 * Does the attribute contain this literal value?
	 * 
	 * @return Does the attribute contain this literal value?
	 */
	public boolean contains(String value) {
		return attributeValue.size() > 0 ? attributeValue.contains(value)
				: defaultAttributeValue.contains(value);
	}

	/**
	 * Get the component that the attribute belongs to.
	 * 
	 * @return The component.
	 */
	public Resource getComponent() {
		return component == null ? null : component;
	}

	/**
	 * Get the resolution rule applied to the attribute.
	 * 
	 * @return The resolution rule.
	 */
	public String getResolution() throws VocabularyException {
		return getAttributeProperty(Constants.RESOLUTION);
	}

	/**
	 * Get the collectionType of the attribute.
	 * 
	 * @return The attribute collectionType.
	 */
	public String getCollectionType() throws VocabularyException {
		return getAttributeProperty(Constants.COLLECTIONTYPE);
	}

	/**
	 * Get the type of the attribute.
	 * 
	 * @return The attribute type.
	 */
	public String getType() throws VocabularyException {
		return getAttributeProperty(Constants.TYPE);
	}

	/**
	 * Helper method to get a vocabulary property.
	 * 
	 * @param string
	 * @return
	 */
	private String getAttributeProperty(String string) throws VocabularyException {
		return vocabulary.getAttributeProperty(getName(), string).getLocalName();
	}

	/**
	 * Retrieve the value from a profile attribute. This method enforces the
	 * rule that normal attribute values always override default attribute
	 * values.
	 * 
	 * @return The attribute value.
	 */
	public Vector<String> get() {
		return getValue() == null ? getDefaultValue() : getValue();
	}

	/**
	 * Converts the object to a String.
	 * 
	 * @return The ProfileAttribute as a String.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("\nAttribute:  	 " + getName());
		result.append("\nComponent:  	 " + getComponent());
		result.append("\nValue:      	 " + getValue());
		result.append("\nDefaultVal: 	 " + getDefaultValue());
		try {
		result.append("\nResolution: 	 " + getResolution());
		result.append("\nCollectionType:  " + getCollectionType());
		result.append("\nType:       	 " + getType() +"\n");
		} catch (VocabularyException ve) {
			ve.printStackTrace();
		}

		return result.toString();
	}

	/**
	 * Combine two profile attributes using the UAProf resolution rules.
	 * 
	 * @param b The profile attribute.
	 */
	void mergeAttribute(ProfileAttribute b) {
		String resolutionRule = Constants.OVERRIDE;
		try {
			resolutionRule = getResolution().toLowerCase();
		} catch (VocabularyException ve) {
			log.error("Could not get resolution rule for " + b.toString());
		}

		if (resolutionRule.equals(Constants.LOCKED)) {
			/*
			 * if attribute is locked then: 
			 * value B changes value A only if value A is a default
			 */
			if ((getValue() == null) && (b.getValue() != null)) {
				attributeValue = b.getValue();
			}
		} else if (resolutionRule.equals(Constants.OVERRIDE)) {
			/*
			 * if attribute is override then: 
			 * value B changes value A only if value B is not a default
			 */
			if (b.getValue() != null) {
				attributeValue = b.getValue();
			}
		} else if (resolutionRule.equals(Constants.APPEND)) {
			/*
			 * if attribute is appended then: 
			 * non-default values always override default values
			 * non-default values are appended to other non-default values
			 */
			try {
			if (!getCollectionType().equals(
					Constants.SIMPLE)) {
				if ((b.getValue() != null) && (getValue() != null)) {
					for (String s : b.getValue()) {
						if (attributeValue.contains(s)) {
							attributeValue.add(s);
						}
					}
				} else if ((b.getValue() != null) && (getValue() == null)) {
					attributeValue = b.getValue();
				}
			} } catch (VocabularyException ppe) {
				ppe.printStackTrace();
			}
		}
	}
}
