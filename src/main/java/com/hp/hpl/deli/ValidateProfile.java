package com.hp.hpl.deli;

import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;

/**
 * Validate a profile.
 */
public class ValidateProfile extends AbstractProcessProfile {
	private boolean isProfileValid = true;
	
	private boolean printedWarningAboutDatatyping = false;

	/**
	 * @return the validationMessages
	 */
	public StringBuffer getValidationMessages() {
		return validationMessages;
	}

	public ValidateProfile(ProfileProcessor configuration, Model model) {
		this(configuration, false, model);
	}

	private ValidateProfile(ProfileProcessor configuration,
			boolean currentlyProcessingDefaults, Model model) {
		super(configuration, currentlyProcessingDefaults);
		processUnconfiguredNamespaces = configuration.getWorkspace().get(
				DeliSchema.processUnconfiguredNamespaces, processUnconfiguredNamespaces);
		this.model = model;
		processModel();

		// check root
		Resource root = findRoot(model);
		if (root != null) {
			StmtIterator rootProperties = findRoot(model).listProperties();
			while (rootProperties.hasNext()) {
				Statement s = rootProperties.nextStatement();
				if (!s.getPredicate().getLocalName().equals("component")) {
					outputMsg("Error: " + s.getPredicate().toString()
							+ " there is a problem with components");
					isProfileValid = false;
				}
			}
		}
	}

	void noNamespaces() {
		outputMsg("The profile does not contain any RDF information - use W3C RDF Validator");
		isProfileValid = false;
	}

	void processEachNamespace(String theNamespace) {
		// check for namespace aliasing
		SchemaCollection vocabulary = configuration.getVocabulary();
		String correctNamespace = vocabulary.getRealNamespace(theNamespace);
		if (!correctNamespace.equals(theNamespace)) {
			outputMsg("WARNING: The profile uses namespace aliasing");
			outputMsg("Aliased namespace is:    " + theNamespace);
			outputMsg("Real namespace is: " + correctNamespace);
		} else if (!vocabulary.knownNamespace(theNamespace)) {
			outputMsg("The profile uses an unknown namespace: " + theNamespace);
		} 
		processComponents(theNamespace);
	}

	void processProperties(Statement statement) {
		ValidateAttribute pa = new ValidateAttribute(configuration, statement,
				currentComponent, currentlyProcessingDefaults, isProfileValid, printedWarningAboutDatatyping);
		validationMessages.append(pa.getValidationMessages());
		isProfileValid = pa.isProfileValid();
		printedWarningAboutDatatyping = pa.printedWarningAboutDatatyping();
	}

	void retrieveDefaultProfile(String url) {
		try {
			Model defaultModel = ModelFactory.createDefaultModel();
			arpReader.read(defaultModel, ModelUtils.getResource(url),
					url.startsWith("http") ? url : "");
			ValidateProfile tempProfile = new ValidateProfile(configuration, true,
					defaultModel);
			if (!tempProfile.isProfileValid) {
				outputMsg("Referenced profile " + url + " is invalid");
				isProfileValid = false;
			}
		} catch (IOException ie) {
			outputMsg("Could not retrieve referenced default profile " + url);
			isProfileValid = false;
		}
	}

	/**
	 * @return the root of the jena model
	 */
	static Resource findRoot(Model model) {
		Resource root = null;
		StmtIterator si = model.listStatements();
		if (si.hasNext()) {
			if (si.nextStatement() != null) {
				RDFNode currentNode = si.nextStatement().getObject();

				while (root == null) {
					Selector s = new SelectorImpl((Resource) null, (Property) null,
							currentNode);
					StmtIterator iter = model.listStatements(s);

					if (iter.hasNext()) {
						currentNode = iter.nextStatement().getSubject();
					} else {
						root = (Resource) currentNode;
					}
				}
			}
		}
		return root;
	}

	/**
	 * @return the isProfileValid
	 */
	public boolean isProfileValid() {
		return isProfileValid;
	}
}
