package com.hp.hpl.deli;

import java.io.Serializable;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class represents a profile attribute.
 */
public class ProfileAttribute implements Serializable {
	private static final long serialVersionUID = 1L;

	private Resource name;

	private Resource component;

	private Vector<String> attributeValue = new Vector<String>();;

	private Vector<String> defaultAttributeValue = new Vector<String>();

	/**
	 * Get the attribute qname
	 *
	 *@return The attribute qname.
	 */
	protected Resource getName() {
		return name;
	}

	/**
	 * Get the profile attribute value.
	 *
	 *@return The attribute value.
	 */
	public Vector<String> getValue() {
		return attributeValue.size() == 0 ? null : attributeValue;
	}

	/**
	 * Get the profile default attribute value.
	 *
	 *@return The default attribute value.
	 */
	public Vector<String> getDefaultValue() {
		return defaultAttributeValue.size() == 0 ? null : defaultAttributeValue;
	}

	/**
	 * Does the attribute contain this literal value?
	 *
	 *@return Does the attribute contain this literal value?
	 */
	public boolean contains(String value) {
		return !attributeValue.isEmpty() ? attributeValue.contains(value) : defaultAttributeValue.contains(value);
	}

	/**
	 * Get the component that the attribute belongs to.
	 *
	 * @return The component.
	 */
	public String getComponent() {
		return component == null ? null : component.getLocalName();
	}

	/**
	 * Get the name of the attribute.
	 *
	 * @return The attribute name.
	 */
	public String getAttribute() {
		return name.getLocalName();
	}

	/**
	 * Get the resolution rule applied to the attribute.
	 *
	 * @return The resolution rule.
	 */
	public String getResolution() {
		return getAttributeProperty(getName(), Constants.RESOLUTION);
	}

	/**
	 * Get the collectionType of the attribute.
	 *
	 * @return The attribute collectionType.
	 */
	public String getCollectionType() {
		return getAttributeProperty(getName(), Constants.COLLECTIONTYPE);
	}

	/**
	 * Get the type of the attribute.
	 *
	 * @return The attribute type.
	 */
	public String getType() {
		return getAttributeProperty(getName(), Constants.type);
	}

	private String getAttributeProperty(Resource property, String string) {
		Resource resource = Workspace.getInstance().vocabulary.getAttributeProperty(property, string);
		return resource != null ? resource.getLocalName() : null;
	}

	/**
	 * Get the Uri for the attribute.
	 *
	 * @return The actual Uri used by DELI for this attribute. (Note that this may
	 * be different than the Uri sent with a profile, if the uri sent with a profile
	 * was an alias defined in the namespace configuration file).
	 *
	 */
	public String getUri() {
		return name.getNameSpace();
	}

	/**
	 * Set the attribute to a specific value.
	 *
	 *@param        property      The property QName
	 *@param        component     The component Qname
	 *@param        value                The value of the attribute.
	 *@param        isDefault        Is this a default value?
	 */
	protected void set(Resource property, Resource component, Vector<String> value, boolean isDefault) {
		name = property;
		this.component = component;

		if (isDefault) {
			defaultAttributeValue = value;
		} else {
			attributeValue = value;
		}
	}

	/**
	 * Retrieve the value from a profile attribute. This method
	 * enforces the rule that normal attribute values always override
	 * default attribute values.
	 *
	 *@return        The attribute value.
	 */
	public Vector<String> get() {
		return getValue() == null ? getDefaultValue() : getValue();
	}

	/**
	 * Converts the object to a String.
	 *
	 *@return        The ProfileAttribute as a String.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("\nAttribute:  " + getAttribute());
		result.append("\nComponent:  " + getComponent());
		result.append("\nResolution: " + getAttributeProperty(getName(), Constants.RESOLUTION));
		result.append("\ncollectionType:" + getAttributeProperty(getName(), Constants.COLLECTIONTYPE));
		result.append("\nType:       " + getAttributeProperty(getName(), Constants.type));
		result.append("\nUri:    " + getUri());

		if (Workspace.getInstance().printDefaults) {
			result.append("\nValue:      " + getValue());
			result.append("\nDefaultVal: " + getDefaultValue() + "\n");
		} else {
			result.append("\nResolvedVal:" + get() + "\n");
		}

		return result.toString();
	}

	/**
	 * Combine two profile attributes using the UAProf resolution rules.
	 *
	 *@param        a        The profile attribute.
	 */
	protected void set(ProfileAttribute a) {
		String resolutionRule = getAttributeProperty(getName(), Constants.RESOLUTION);

		if (resolutionRule.toLowerCase().equals(Constants.LOCKED)) {
			/* if attribute is locked then:
			- the original attribute must have either a default value
			or a non-default value so default values are always ignored
			- non-default values cannot override other non-default values
			- non-default values can still override defaults */
			if ((getValue() == null) && (a.getValue() != null)) {
				attributeValue = a.getValue();
			}
		} else if (resolutionRule.toLowerCase().equals(Constants.OVERRIDE)) {
			/* if attribute is overidden then:
			- the original attribute must have either a default value
			or a non-default value so default values are always ignored
			- non-default values always override other values */
			if (a.getValue() != null) {
				attributeValue = a.getValue();
			}
		} else if (resolutionRule.toLowerCase().equals(Constants.APPEND)) {
			/* if attribute is appended then:
			- the original attribute must have either a default value
			or a non-default value so default values are always ignored
			- non-default values always override default values
			- non-default values are appended to other non-default values */
			if (!getAttributeProperty(getName(), Constants.COLLECTIONTYPE).equals(Constants.SIMPLE)) {
				if ((a.getValue() != null) && (getValue() != null)) {
					for (String s : a.getValue()) {
						if (attributeValue.contains(s)) {
							attributeValue.add(s);
						}
					}
				} else if ((a.getValue() != null) && (getValue() == null)) {
					attributeValue = a.getValue();
				}
			}
		}
	}
}
