package com.hp.hpl.deli;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class processes the CC/PP vocabulary definition file.
 */
class SchemaCollection extends ModelUtils {
	/** Logging. */
	private static Log log = LogFactory.getLog(SchemaCollection.class);

	/** datatype expressions of validation */
	protected HashMap<String, String> datatypeExpressions = new HashMap<String, String>();

	/** A mapping of aliased schema namespaces on to real schema namespaces. */
	private Map<String, String> namespaceLookup = new HashMap<String, String>();

	/** A mapping of UAProf properties onto a vector describing each property. */
	private Map<Resource, Map<String, Resource>> propertyDescription = new HashMap<Resource, Map<String, Resource>>();

	/** Datatyping information. */
	private Map<List<String>, String> datatypesLookup = new HashMap<List<String>, String>();

	private List<String> datatypesDef = null;

	/** Datatype config file */
	protected String datatypeConfigFile = Constants.DATATYPE_CONFIG_FILE;

	/**
	 * The constructor reads in a vocabulary definition file and then processess
	 * the set of vocabularies it references.
	 * 
	 * @param configData The configuration data.
	 */
	SchemaCollection(DeliConfiguration configData) {
		ResIterator definitions = configData.getModel()
				.listSubjectsWithProperty(RDF.type,
						DeliSchema.NamespaceDefinition);
		if (!definitions.hasNext()) {
			log.info("Vocabulary: No schemas defined. Loading schemas dynamically.");
		} else {
			// process namespace definitions
			while (definitions.hasNext()) {
				processNamespaceDefinition(definitions.nextResource());
			}
		}
		datatypeConfigFile = configData.get(DeliSchema.datatypeConfigFile,
				datatypeConfigFile);
		try {
			datatypeExpression(datatypeConfigFile);
		} catch (Exception e) {
			log.error("Failed to load validator datatypes", e);
			log.info("Using defaults");
		}
	}

	/**
	 * Process a namespace definition.
	 * 
	 * @param defn The resource for the namespace definition.
	 */
	private void processNamespaceDefinition(Resource defn) {
		if (defn.hasProperty(DeliSchema.uri)) {
			// add namespace
			String URI = defn.getProperty(DeliSchema.uri).getResource()
					.getURI();
			namespaceLookup.put(URI, URI);
			// add alias namespaces
			if (defn.hasProperty(DeliSchema.aliasUri)) {
				StmtIterator stmts = defn.listProperties(DeliSchema.aliasUri);
				while (stmts.hasNext()) {
					String aliasUri = stmts.nextStatement().getResource()
							.getURI();
					namespaceLookup.put(aliasUri, URI);
				}
			}
			// process datatype definitions for UAProf 2.0 schemas
			if (defn.hasProperty(DeliSchema.datatypeUri)) {
				String datatypeUri = defn.getProperty(DeliSchema.datatypeUri)
						.getResource().getURI();
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

				String file = getPropertyString(defn,
						DeliSchema.schemaVocabularyFile);
				addSchemaFromFile(file, URI);
			}
		}
	}

	/**
	 * Add a schema from a file.
	 * 
	 * @param file The filename.
	 * @param URI The URI of the schema.
	 */
	void addSchemaFromFile(String file, String URI) {
		try {
			log.debug("Vocabulary: Processing UAProf schema vocabulary file: "
					+ file);
			addSchema(new Schema(ModelUtils.getResource(file), URI, URI,
					datatypesDef));
		} catch (Exception e) {
			log.error(
					"Vocabulary: Cannot load and process vocabulary schema from "
							+ file, e);
		}
	}

	/**
	 * Add a schema to the set of schemas
	 * 
	 * @param schema The schema.
	 */
	void addSchema(Schema schema) {
		propertyDescription.putAll(schema.getPropertyDescription());
		datatypesLookup.putAll(schema.getDatatypesLookup());
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
			Reader fr = new InputStreamReader(
					ModelUtils.getResource(datatypeFile));
			result = XSDDatatype.loadUserDefined(datatypeUri, fr, null, tm);
		} catch (DatatypeFormatException e) {
			log.error("DELI could not process datatype configuration file: "
					+ datatypeFile, e);
		} catch (IOException ie) {
			log.error("DELI could not process datatype configuration file: "
					+ datatypeFile, ie);
		}
		return result;
	}

	/**
	 * This method returns a given value for an attribute property where the
	 * attribute is specified by the attribute qname and its component qname.
	 * 
	 * @param attributeQName The QName of the attribute.
	 * @param paramName The required Parameter
	 * @return The value of the attribute property.
	 * @throws VocabularyException Thrown if there is a problem reading the
	 *             property.
	 */
	public Resource getAttributeProperty(Resource attributeQName,
			String paramName) throws VocabularyException {
		Resource alias = getRealQName(attributeQName);
		if (propertyDescription.containsKey(alias)) {
			return getRealQName(propertyDescription.get(alias).get(paramName));
		}

		throw new VocabularyException(attributeQName.getURI()
				+ " is unknown in the vocabulary");
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
	 * This method attempts to get an attributes property set, given only a
	 * QName. It therefore assumes that there is only one set of properties for
	 * the attribute name specified. If there are infact more, the first set
	 * encountered is all that is returned anyway.
	 * 
	 * @param attributeQName The attribute QName as a URI
	 * @return A map of property names on to resources.
	 * @throws VocabularyException thrown if the attribute is not known.
	 */
	Map<String, Resource> getAttribute(Resource attributeQName)
			throws VocabularyException {
		Resource aliasAttributeQName = getRealQName(attributeQName);
		if (propertyDescription.containsKey(aliasAttributeQName)) {
			return propertyDescription.get(aliasAttributeQName);
		}
		throw new VocabularyException(attributeQName + " is not known.");
	}

	/**
	 * This method allows the processor to add a new attribute to the
	 * vocabulary, when it encounters attributes that are not defined in the
	 * vocabulary definition.
	 * 
	 * @param qualifiedAttribute The qualified attribute.
	 * @param currentComponent The qualified name of the component, if known.
	 * @param collectionType The collection type, if known.
	 */
	protected void addAttributeToVocabulary(Resource qualifiedAttribute,
			String currentComponent, String collectionType) {
		// NOTE: since we cannot add a null-valued entry to a hashmap, we simply
		// do not add
		// the component if it has a null value. otherwise, when .get(key) is
		// called, it returns
		// null. which is the same as adding the entry with the null value.
		Resource temp = getRealNamespace(qualifiedAttribute);

		String theURI = temp.getNameSpace();

		if (theURI == null) {
			theURI = getRealNamespace(currentComponent);
		}

		if (theURI != null) {
			String correctedCurrentComponent = (currentComponent == null) ? (theURI + "Unknown")
					: getRealNamespace(currentComponent);
			String correctedCllectionType = (collectionType == null) ? Constants.SIMPLE
					: collectionType;

			HashMap<String, Resource> properties = new HashMap<String, Resource>();

			properties.put(Constants.ATTRIBUTE, temp);
			properties.put(Constants.COMPONENT, getRealQName(ResourceFactory
					.createResource(correctedCurrentComponent)));
			properties.put(
					Constants.TYPE,
					getRealQName(ResourceFactory.createResource(theURI
							+ Constants.LITERAL)));
			properties.put(
					Constants.RESOLUTION,
					getRealQName(ResourceFactory.createResource(theURI
							+ Constants.OVERRIDE)));
			properties.put(
					Constants.COLLECTIONTYPE,
					getRealQName(ResourceFactory.createResource(theURI
							+ correctedCllectionType)));

			propertyDescription.put(properties.get(Constants.ATTRIBUTE),
					properties);
		} else {
			log.error("Fatal error: cannot create new attribute when namespace is null");
		}
	}

	/**
	 * Get the real namespace from an alias.
	 * 
	 * @param alias The alias.
	 * @return The real namespace.
	 */
	protected Resource getRealNamespace(Resource alias) {
		return ResourceFactory.createResource(getRealNamespace(alias.getURI()));
	}

	/**
	 * Get the actual namespace from a given alias.
	 * 
	 * @param alias the aliased namespace
	 * @return The _actual_ namespace being used for the vocabulary.
	 */
	String getRealNamespace(String alias) {
		if (alias == null) {
			throw new NullPointerException();
		}

		Resource temp = ResourceFactory.createResource(alias);
		String fragment = temp.getLocalName();
		String aliasNamespace = temp.getNameSpace();

		if (aliasNamespace != null) {
			aliasNamespace = aliasNamespace.endsWith("#") ? aliasNamespace
					: (aliasNamespace + "#");

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
	 * Is this namespace recognized?
	 * 
	 * @param namespace The namespace.
	 * @return Is this namespace recognized?
	 */
	boolean knownNamespace(String namespace) {
		return namespaceLookup.containsKey(namespace);
	}

	/**
	 * Convert a namespace to its canonical form.
	 * 
	 * @param namespace The namespace to lookup.
	 * @return Returns the namespaceLookup.
	 */
	String getNamespaceLookup(String namespace) {
		return namespaceLookup.get(namespace);
	}

	/**
	 * Read a XML Schema datatype configuration file.
	 * 
	 * @param configFile The XML Schema datatype configuration file.
	 * @throws ParserConfigurationException Thrown if there is a problem creating the XMl parser.
	 * @throws IOException Thrown if there is a problem reading the configuration file.
	 * @throws SAXException Thrown if there is a problem parsing the file.
	 */
	void datatypeExpression(String configFile) throws ParserConfigurationException, IOException, SAXException {
		Document document = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = dbf.newDocumentBuilder();
		InputSource inputSource = ModelUtils.getInputSource(configFile);
		document = parser.parse(inputSource);
		Node config = document;

		NodeList children = document.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("validator")) {
				config = children.item(i);
				break;
			}
		}

		if (config != null) {
			// Find all <datatype> elements
			children = config.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeName().equals("datatype")) {
					setDatatypeFromConfig(children.item(i));
				}
			}
		} else {
			for (String[] dt : Constants.DATATYPES) {
				datatypeExpressions.put(dt[0], dt[1]);
			}
		}

	}

	/**
	 * Adds a datatype definition using the given element from an XML
	 * configuration document
	 * 
	 * @param datatype The XML element containing the datatype information
	 * @throws IOException thrown if there is a problem with the config file
	 */
	void setDatatypeFromConfig(Node datatype) throws IOException {
		String name = null;
		String expression = null;

		NodeList children = datatype.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node currentChild = children.item(i);

			if (currentChild.getNodeName().equals("name")) {
				name = currentChild.getFirstChild().getNodeValue();
			} else if (currentChild.getNodeName().equals("expression")) {
				expression = currentChild.getFirstChild().getNodeValue();
			}
		}

		if ((name != null) && (expression != null)) {
			datatypeExpressions.put(name, expression);
		} else {
			throw new IOException("Datatype config file is invalid");
		}
	}
}