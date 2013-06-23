package com.hp.hpl.deli;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * A UAProf schema.
 */
class Schema {
	private static final Log log = LogFactory.getLog(Schema.class);
	private final Model vocabularySchema = ModelFactory.createDefaultModel();
	private Property resolutionRuleProperty = null;
	private String prfUri;
	private String schemaName;

	/** Datatyping information. */
	private final Map<List<String>, String> datatypesLookup = new HashMap<List<String>, String>();

	/** A mapping of UAProf properties onto a vector describing each property. */
	private final Map<Resource, Map<String, Resource>> propertyDescription = new HashMap<Resource, Map<String, Resource>>();

	/**
	 * Constructor.
	 * 
	 * @param url The schema URL.
	 * @param datatypesDef The datatype definition.
	 */
	Schema(String url, List<String> datatypesDef)
	{
		// read the document in from a URL
		this(url, url);
		vocabularySchema.read(url);
		init(datatypesDef);
	}

	/**
	 * This method traverses a UAProf RDF schema vocabulary definition file
	 * using Jena.
	 *
	 * @param in The schema file to be processed.
	 * @param prfUri The URI of the namespace to be associated with the
	 *            schema.
	 * @param schemaName The name of the schema.
	 * @param datatypesDef The datatype definitions.
	 * @throws IOException Thrown if there is a problem reading the schema.
	 */
	Schema(InputStream in, String prfUri, String schemaName, List<String> datatypesDef)
			throws IOException {
		this(prfUri, schemaName);
		vocabularySchema.read(in, prfUri);
		init(datatypesDef);
	}
	
	private Schema(String prfUri, String schemaName) {
		this.prfUri = prfUri;
		this.schemaName = schemaName;
	}

	private void init(List<String> datatypesDef) {
		// loop over all the schema URIs being used by this vocabulary

		// are we processing a UAProf 2.0 vocabulary?
		if (datatypesLookup.containsValue(prfUri)) {
			resolutionRuleProperty = vocabularySchema.createProperty(prfUri + "ResolutionRule");

			// temporarily attach instances of these datatypes to the
			// vocabulary schema so we can retrieve datatype information
			Iterator<String> datatypesDefIterator = datatypesDef.iterator();
			while (datatypesDefIterator.hasNext()) {
				vocabularySchema.createTypedLiteral(datatypesDefIterator.next());
			}
		}
		// process the properties
		ResIterator attributeList = vocabularySchema.listSubjectsWithProperty(RDFS.domain);
		while (attributeList.hasNext()) {
			processProperty((Resource) attributeList.next());
		}
	}

	private void processProperty(Resource attribute) {
		HashMap<String, Resource> properties = new HashMap<String, Resource>();
		String attributeName = attribute.getLocalName();

		if ((!attributeName.equals("Defaults")) && (!attributeName.equals("ResolutionRule"))) {
			String collectionTypeName = Constants.SIMPLE;
			String resolutionRule = Constants.OVERRIDE;
			String attributeType = Constants.DEFAULT_ATTRIBUTE_TYPE;
			boolean ccppTypeFound = false;

			NodeIterator rangeList = vocabularySchema.listObjectsOfProperty(attribute, RDFS.range);
			while (rangeList.hasNext()) {
				Resource rangeEntry = (Resource) rangeList.next();
				String sRangeEntry = rangeEntry.toString();

				if (sRangeEntry.endsWith(Constants.BAG) || sRangeEntry.endsWith(Constants.SEQ)) {
					collectionTypeName = rangeEntry.getLocalName();
				}

				if (sRangeEntry.endsWith(Constants.BOOLEAN) || sRangeEntry.endsWith(Constants.DIMENSION)
						|| sRangeEntry.endsWith(Constants.NUMBER) || sRangeEntry.endsWith(Constants.LITERAL)) {
					properties.put(Constants.TYPE, ResourceFactory.createResource(sRangeEntry));
					ccppTypeFound = true;
				}
			}



			if (!ccppTypeFound) {
				properties.put(Constants.TYPE, ResourceFactory.createResource(prfUri + attributeType));
			}
			Resource attName = ResourceFactory.createResource(prfUri + attributeName);
			properties.put(Constants.ATTRIBUTE, attName);
			
			try {
				String componentName = getComponentName(attribute);
				properties.put(Constants.COMPONENT, ResourceFactory.createResource(componentName));
			} catch (VocabularyException ve) {
				ve.printStackTrace();
			}
			
			try {
				if (datatypesLookup.containsValue(prfUri)) {
					resolutionRule = getUAProf2ResolutionRule(attribute);
				} else {
					// UAProf 1.0 - get the comment field
					String commentString = getCommentString(attribute);
					collectionTypeName = getCollectionTypeFromComments(commentString,
							attributeName, collectionTypeName);
					StringTokenizer str = new StringTokenizer(commentString, " \t|\n");

					while (str.hasMoreTokens()) {
						String current = str.nextToken();

						if (current.equals("Type:")) {
							if (attributeType.equals(Constants.DEFAULT_ATTRIBUTE_TYPE)) {
								attributeType = str.nextToken().trim();
								if (!attributeType.equals(Constants.BOOLEAN)
										&& !attributeType.equals(Constants.LITERAL)
										&& !attributeType.equals(Constants.NUMBER)
										&& !attributeType.equals("Dimension")) {
									log.error("FATAL ERROR LOADING VOCABULARY: "
											+ attributeName
											+ " does not have a valid type "
											+ attributeType);
								}
							}

							properties.put(Constants.TYPE, ResourceFactory
									.createResource(prfUri + attributeType));
							ccppTypeFound = true;
						}

						if (current.equals("Resolution:")) {
							resolutionRule = str.nextToken().trim();
						}
					}
				}
				properties.put(Constants.RESOLUTION,
						ResourceFactory.createResource(prfUri + resolutionRule));
			} catch (VocabularyException ve) {
				ve.printStackTrace();
			}
			properties.put(Constants.COLLECTIONTYPE, ResourceFactory.createResource(prfUri + collectionTypeName));
			if (propertyDescription.containsKey(attName)) {
				log.error(attName.getURI() + " is already defined");
			} else {
				propertyDescription.put(attName, properties);
			}
		}
	}

	private NodeIterator nodeIterator(Resource attribute, Property property)
			throws VocabularyException {
		NodeIterator nodeIterator = vocabularySchema.listObjectsOfProperty(attribute,
				property);
		if (nodeIterator.hasNext()) {
			return nodeIterator;
		}
		throw new VocabularyException(attribute + " is not known");
	}

	private String getUAProf2ResolutionRule(Resource attribute)
			throws VocabularyException {
		// UAProf 2.0 - get the resolution Rule
		NodeIterator resolutionRuleList = nodeIterator(attribute, resolutionRuleProperty);
		Literal resolution = resolutionRuleList.next().asLiteral();
		return resolution.getValue().toString();
	}

	private String getComponentName(Resource attribute) throws VocabularyException {
		// Get the component name
		NodeIterator componentList = nodeIterator(attribute, RDFS.domain);
		return componentList.nextNode().asResource().getURI();
	}

	private String getCollectionTypeFromComments(String commentString, String attributeName,
			String collectionTypeName) {
		String correctedCollectionTypeName = collectionTypeName;
		if (commentString.indexOf("(bag)") > 0) {
			if (!correctedCollectionTypeName.equals(Constants.BAG)) {
				log.warn("Vocabulary inconsistency warning: " + attributeName + " only declares it is a "
						+ Constants.BAG + " in the comments field for schema " + schemaName);
			}
			correctedCollectionTypeName = Constants.BAG;
		} else if (commentString.indexOf("(sequence)") > 0) {
			if (!correctedCollectionTypeName.equals(Constants.SEQ)) {
				log.warn("Vocabulary inconsistency warning: " + attributeName + " only declares it is a "
						+ Constants.SEQ + " in the comments field for schema" + schemaName);
			}
			correctedCollectionTypeName = Constants.SEQ;
		}
		return correctedCollectionTypeName;
	}

	private String getCommentString(Resource attribute) {
		StringBuffer comment = new StringBuffer();
		NodeIterator commentList = vocabularySchema.listObjectsOfProperty(attribute, RDFS.comment);
		while (commentList.hasNext()) {
			comment.append(commentList.nextNode().toString());
		}
		return comment.toString();
	}

	/**
	 * @return Returns the datatypesLookup.
	 */
	Map<List<String>, String> getDatatypesLookup() {
		return datatypesLookup;
	}

	/**
	 * @return Returns the propertyDescription.
	 */
	Map<Resource, Map<String, Resource>> getPropertyDescription() {
		return propertyDescription;
	}
}
