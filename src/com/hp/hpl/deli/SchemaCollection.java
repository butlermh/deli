package com.hp.hpl.deli;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class processes the CC/PP vocabulary definition file.
 */
/**
 * @author butlermh
 *
 */
class SchemaCollection extends Utils implements Serializable {
	/** Logging. */
	private static Log log = LogFactory.getLog(SchemaCollection.class);

	/** Comment for <code>serialVersionUID</code> */
	private static final long serialVersionUID = 1L;

	/** A mapping of aliased schema namespaces on to real schema namespaces. */
	private Map<String, String> namespaceLookup = new HashMap<String, String>();

	/** A mapping of UAProf properties onto a vector describing each property. */
	private Map<Resource, Map<String, Resource>> propertyDescription = new HashMap<Resource, Map<String, Resource>>();

	/** Datatyping information. */
	private Map<List<String>, String> datatypesLookup = new HashMap<List<String>, String>();

	/**
	 * The constructor reads in a vocabulary definition file and then processess
	 * the set of vocabularies it references.
	 *
	 * @param configFile The filename of the configuration file.
	 */
	protected SchemaCollection(String configFile) {
		Model configData = ModelFactory.createDefaultModel();

		try {
			configData.read(Workspace.getInstance().getResource(configFile), "", "N3");
		} catch (Exception e) {
			log.error("DELI Failed to load namespace/vocabulary configuration file from: " + configFile, e);
		}
		ResIterator definitions = configData.listSubjectsWithProperty(RDF.type, DeliSchema.NamespaceDefinition);
		if (!definitions.hasNext()) {
			log.info("Vocabulary: No schemas defined.");
		} else {
			// process namespace definitions
			while (definitions.hasNext()) {
				processNamespaceDefinition(definitions.nextResource());
			}
		}
	}

	/**
	 * Process a namespace definition.
	 *
	 * @param defn The resource for the namespace definition.
	 */
	private void processNamespaceDefinition(Resource defn) {
		if (defn.hasProperty(DeliSchema.uri)) {
			List<String> datatypesDef = null;
			// add namespace
			String URI = getPropertyUri(defn, DeliSchema.uri);
			namespaceLookup.put(URI, URI);
			// add alias namespaces
			if (defn.hasProperty(DeliSchema.aliasUri)) {
				StmtIterator stmts = defn.listProperties(DeliSchema.aliasUri);
				while (stmts.hasNext()) {
					String aliasUri = ((Resource) stmts.nextStatement().getObject()).getURI();
					namespaceLookup.put(aliasUri, URI);
				}
			}
			// process datatype definitions for UAProf 2.0 schemas
			if (defn.hasProperty(DeliSchema.datatypeUri)) {
				String datatypeUri = getPropertyUri(defn, DeliSchema.datatypeUri);
				if (defn.hasProperty(DeliSchema.datatypeFile)) {
					datatypesDef = loadDatatypeDef(defn, datatypeUri);
					datatypesLookup.put(datatypesDef, URI);

					log.debug("VocabularyConfig: Defined types are: ");
					for (String it : datatypesDef) {
						log.debug(" - " + it);
					}
				}
			}
			// load and process the schema
			if (defn.hasProperty(DeliSchema.schemaVocabularyFile)) {

				String file = getPropertyString(defn, DeliSchema.schemaVocabularyFile);
				try {
					log.debug("Vocabulary: Processing UAProf schema vocabulary file: " + file);
					addSchema(new Schema(Workspace.getInstance().getResource(file), URI, URI, datatypesDef));
				} catch (Exception e) {
					log.error("Vocabulary: Cannot load and process vocabulary schema from " + file, e);
				}
			}
		}
	}

	/**
	 * Add a schema to the set of schemas
	 *
	 * @param sp
	 */
	void addSchema(Schema sp) {
		propertyDescription.putAll(sp.getPropertyDescription());
		datatypesLookup.putAll(sp.getDatatypesLookup());
	}

	/**
	 * Get a datatype definition
	 *
	 * @param defn the resource corresponding to the namespace definition.
	 * @param datatypeUri the datatype URI.
	 * @return the datatype definition
	 */
	private List<String> loadDatatypeDef(Resource defn, String datatypeUri) {
		String datatypeFile = getPropertyString(defn, DeliSchema.datatypeFile);
		List<String> result = null;
		// if it is a UAProf 2.0 schema, load in datatype
		// definitions
		try {
			TypeMapper tm = TypeMapper.getInstance();
			FileReader fr = new FileReader(Workspace.getPath() + datatypeFile);
			result = XSDDatatype.loadUserDefined(datatypeUri, fr, null, tm);
		} catch (DatatypeFormatException e) {
			log.error("DELI could not process datatype configuration file: " + datatypeFile, e);
		} catch (FileNotFoundException e) {
			log.error("DELI Failed to load datatype configuration file: " + datatypeFile, e);
		}
		return result;
	}

	/**
	 * This method returns a given value for an attribute property where the
	 * attribute is specified by the attribute qname and its component qname.
	 *
	 * @param attributeQName The QName of the attribute.
	 * @param paramName The required Parameter
	 */
	protected Resource getAttributeProperty(Resource attributeQName, String paramName) {
		Resource alias = getRealQName(attributeQName);
		if (propertyDescription.containsKey(alias)) {
			return getRealQName(propertyDescription.get(alias).get(paramName));
		}

		return null;
	}

	/**
	 * Does this namespace use RDF datatyping?
	 *
	 * @param prfUri schema namespace
	 * @return does this schema use RDF datatyping?
	 */
	public boolean usesRDFDatatyping(String prfUri) {
		return datatypesLookup.containsValue(prfUri);
	}

	/*
	 * This method takes a URI representing a QName and returns the
	 * namespace-resolved URI based on DELI's namespace lookup table.
	 *
	 * @param theUri The unresolved URI
	 */
	private Resource getRealQName(Resource theUri) {
		Resource aliasQName = null;
		if (theUri != null) {
			String theUristring = theUri.toString();
			String realUri = getRealNamespace(theUristring);

			if (realUri != null) {
				aliasQName = ResourceFactory.createResource(realUri);
			} else {
				log.error("Fatal error: cannot find " + theUristring);
			}

		}

		return aliasQName;
	}

	/**
	 * This method returns a Vector of Maps representing the properties of
	 * Vocabulary Attributes with a given Attribute Name. The attribute name can
	 * either be qualified (in which case it will contain a hash character) or
	 * unqualified.
	 *
	 * @param name The attribute name as a string (eg 'ColorCapable').
	 */
	protected Vector<Map<String, Resource>> getAttPropertiesWithAttName(String name) {
		Vector<Map<String, Resource>> v = new Vector<Map<String, Resource>>();

		if (name.lastIndexOf("#") > 0) {
			// Qualified attribute name
			Resource aliasQName = null;

			String namespace = getRealNamespace(name);

			if (namespace != null) {
				aliasQName = ResourceFactory.createResource(namespace);
			}

			if (aliasQName != null) {
				v.add(propertyDescription.get(aliasQName));
			} else {
				return null;
			}
		}
		// Unqualified attribute name

		for (Resource qn : propertyDescription.keySet()) {
			if (qn.getLocalName().equals(name)) {
				v.add(propertyDescription.get(qn));
			}
		}

		return v;
	}

	/*
	 * This method attempts to get an attributes property set, given only a
	 * QName. It therefore assumes that there is only one set of properties for
	 * the attribute name specified. If there are infact more, the first set
	 * encountered is all that is returned anyway.
	 *
	 * @param attributeQName The attribute QName as a URI
	 */
	protected Map<String, Resource> getAttribute(Resource attributeQName) {
		Resource aliasAttributeQName = getRealQName(attributeQName);
		return propertyDescription.get(aliasAttributeQName);
	}

	/**
	 * This method allows the processor to add a new attribute to the
	 * vocabulary, when it encounters attributes that are not defined in the
	 * vocabulary definition.
	 *
	 * @param qualifiedAttribute
	 * @param currentComponent The qualified name of the component, if known.
	 * @param collectionType The collection type, if known.
	 */
	protected void addAttributeToVocabulary(String qualifiedAttribute, String currentComponent, String collectionType) {
		// NOTE: since we cannot add a null-valued entry to a hashmap, we simply
		// do not add
		// the component if it has a null value. otherwise, when .get(key) is
		// called, it returns
		// null. which is the same as adding the entry with the null value.
		qualifiedAttribute = getRealNamespace(qualifiedAttribute);

		Resource temp = ResourceFactory.createResource(qualifiedAttribute);
		String theURI = temp.getNameSpace();

		if (theURI == null) {
			theURI = getRealNamespace(currentComponent);
		}

		if (theURI != null) {
			currentComponent = (currentComponent == null) ? (theURI + "Unknown") : getRealNamespace(currentComponent);
			collectionType = (collectionType == null) ? Constants.SIMPLE : collectionType;

			HashMap<String, Resource> properties = new HashMap<String, Resource>();

			properties.put(Constants.ATTRIBUTE, ResourceFactory.createResource(qualifiedAttribute));
			properties.put(Constants.COMPONENT, getRealQName(ResourceFactory.createResource(currentComponent)));
			properties.put(Constants.type, getRealQName(ResourceFactory.createResource(theURI + Constants.LITERAL)));
			properties.put(Constants.RESOLUTION, getRealQName(ResourceFactory.createResource(theURI
					+ Constants.OVERRIDE)));
			properties.put(Constants.COLLECTIONTYPE, getRealQName(ResourceFactory.createResource(theURI
					+ collectionType)));

			propertyDescription.put(properties.get(Constants.ATTRIBUTE), properties);
		} else {
			log.error("Fatal error: cannot create new attribute when namespace is null");
		}
	}

	/**
	 * Get the actual namespace from a given alias.
	 *
	 * @param alias the aliased namespace
	 * @return The _actual_ namespace being used for the vocabulary.
	 */
	protected String getRealNamespace(String alias) {
		if (alias == null) {
			return null;
		}

		Resource temp = ResourceFactory.createResource(alias);
		String fragment = temp.getLocalName();
		String aliasNamespace = temp.getNameSpace();

		if (aliasNamespace != null) {
			aliasNamespace = aliasNamespace.endsWith("#") ? aliasNamespace : (aliasNamespace + "#");

			if (namespaceLookup.containsKey(aliasNamespace)) {
				String namespace = (String) namespaceLookup.get(aliasNamespace);

				if (fragment != null) {
					return namespace + fragment;
				}
				return namespace;
			}
		}
		return alias;
	}

	/**
	 * @return Returns the namespaceLookup.
	 */
	String getNamespaceLookup(String s) {
		return namespaceLookup.get(s);
	}
}