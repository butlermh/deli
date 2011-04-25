package com.hp.hpl.deli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

abstract class AbstractProcessProfile {
	/** Logging. */
	static Log log = LogFactory.getLog(AbstractProcessProfile.class);

	/** The current component. */
	Resource currentComponent = null;

	/** The name for CC/PP components. */
	String componentString = "component";

	Model model = ModelFactory.createDefaultModel();

	JenaReader arpReader = ModelUtils.configureArp();

	ProfileProcessor configuration;

	/** Is the block currently being processed a default block? */
	boolean currentlyProcessingDefaults = false;

	/** Process all RDF namespaces, irrespective of whether a schema exists? */
	boolean processUnconfiguredNamespaces = true;

	/** The profile does not have type statements on components */
	boolean profileOmitsTypeInformationFromComponents = false;
	
	StringBuffer validationMessages = new StringBuffer();
	
	void outputMsg(String s) {
		validationMessages.append(s + "\n");
	}

	protected AbstractProcessProfile(ProfileProcessor configuration, 
			boolean currentlyProcessingDefaults) {
		this.currentlyProcessingDefaults = currentlyProcessingDefaults;
		this.configuration = configuration;
		DeliConfiguration workspace = configuration.getWorkspace();
		processUnconfiguredNamespaces = workspace.get(
				DeliSchema.processUnconfiguredNamespaces, processUnconfiguredNamespaces);
		componentString = workspace.get(DeliSchema.componentProperty, componentString);
	}

	void processModel() {
		NsIterator namespaces = model.listNameSpaces();
		if (!namespaces.hasNext()) {
			noNamespaces();
		}
		// for each namespace
		while (namespaces.hasNext()) {
			String theNamespace = namespaces.nextNs();
			if (!theNamespace.equals(RDF.getURI())) {
				processEachNamespace(theNamespace);
			}
		}
	}

	void noNamespaces() {
		outputMsg("The profile does not contain any RDF information - use W3C RDF Validator");
	}

	void processEachNamespace(String theNamespace) {
		processComponents(theNamespace);
	}

	void processComponents(String theNamespace) {
		Property componentProperty = model.createProperty(theNamespace + componentString);
		NodeIterator componentList = model.listObjectsOfProperty(componentProperty);
		// for each component
		while (componentList.hasNext()) {
			Resource component = componentList.next().asResource();

			if (component.hasProperty(RDF.type)) {
				currentComponent = component.getProperty(RDF.type).getResource();
			} else {
				currentComponent = null;
				if (profileOmitsTypeInformationFromComponents == false) {
					outputMsg("WARNING: Profile omits type information");
					this.profileOmitsTypeInformationFromComponents = true;
				}
			}
			new ProcessComponent(component);
		}
	}

	class ProcessComponent {
		Resource component;

		ProcessComponent(Resource component) {
			this.component = component;
			NsIterator namespaces = model.listNameSpaces();
			// for each namespace
			while (namespaces.hasNext()) {
				String vocabularyURI = namespaces.nextNs();
				if (!vocabularyURI.equals(RDF.getURI())) {
					processDefaultsAndComponents(vocabularyURI);
				}
			}
			// for each property attached to this node
			StmtIterator theProperties = component.listProperties();
			while (theProperties.hasNext()) {
				Statement statement = theProperties.nextStatement();
				if (!statement.getPredicate().equals(RDF.type)) {
					processProperties(statement);
				}
			}
		}

		void processDefaultsAndComponents(String vocabularyURI) {
			String defaults = vocabularyURI.toLowerCase() + "defaults";
			StmtIterator theProperties = component.listProperties();
			// for each property
			while (theProperties.hasNext()) {
				Statement statement = theProperties.nextStatement();
				Property predicate = statement.getPredicate();
				if (!predicate.equals(RDF.type)) {
					if (predicate.getURI().toLowerCase().equals(defaults)) {
						processDefaultBlock(statement);
					} else {
						if (currentComponent == null) {
							extractComponentName(predicate, vocabularyURI);
						}
					}
				}
			}
		}

		void extractComponentName(Property predicate, String vocabularyURI) {
			if (!predicate.getLocalName().equals(componentString)) {
				String property = predicate.toString();
				String propertyNamespace = predicate.getNameSpace();
				SchemaCollection vocabulary = configuration.getVocabulary();
				if (vocabularyURI.equals(propertyNamespace)) {
					if ((vocabulary.getNamespaceLookup(vocabularyURI) != null)
							|| processUnconfiguredNamespaces) {
						outputMsg("Component is null for: " + property);
						try {
//							Vector<Map<String, Resource>> props = vocabulary
//								.getAttPropertiesWithAttName(property);
//							outputMsg("Determining component from vocabulary");
//							currentComponent = props.firstElement().get(
//									Constants.COMPONENT);
							currentComponent = 	vocabulary.getAttribute(predicate.asResource()).get(Constants.COMPONENT);
						} catch (VocabularyException ve) {
							outputMsg("Determining component from local ID");
							currentComponent = model.createResource(vocabularyURI
									+ component.getLocalName());
						}
					}
				}
			}
		}
	}

	void processDefaultBlock(Statement statement) {
		// Found a default block, process it recursively.
		StmtIterator iter = statement.getResource().listProperties();
		if (!iter.hasNext()) {
			String url = statement.getResource().getURI();
			outputMsg("Default block references another profile - retrieving profile "
					+ url);
			retrieveDefaultProfile(url);
		} else {
			// Process the default subtree
			currentlyProcessingDefaults = true;
			new ProcessComponent(statement.getResource());
			currentlyProcessingDefaults = false;
		}
	}

	abstract void processProperties(Statement theStatement);

	abstract void retrieveDefaultProfile(String url);
}
