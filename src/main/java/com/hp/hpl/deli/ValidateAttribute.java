package com.hp.hpl.deli;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

class ValidateAttribute extends AbstractProcessAttribute {
	private SchemaCollection vocabulary;

	Resource attribute;

	private RDFNode object;

	private Resource currentComponent;

	private boolean isProfileValid;

	/** Is datatype validation on? */
	private boolean datatypeValidationOn = true;

	private boolean printedWarningAboutDatatyping = false;

	StringBuffer validationMessages = new StringBuffer();

	void outputMsg(String s) {
		validationMessages.append(s + "\n");
	}

	ValidateAttribute(ProfileProcessor configuration, Statement attributeStatement,
			Resource currentComponent, boolean currentlyProcessingDefaults,
			boolean isProfileValid, boolean printedWarningAboutDatatyping) {
		this.attribute = attributeStatement.getPredicate();
		this.vocabulary = configuration.getVocabulary();
		this.isProfileValid = isProfileValid;
		this.object = attributeStatement.getObject();
		this.currentComponent = currentComponent;
		this.printedWarningAboutDatatyping = printedWarningAboutDatatyping;

		DeliConfiguration workspace = configuration.getWorkspace();
		datatypeValidationOn = workspace.get(DeliSchema.datatypeValidationOn,
				datatypeValidationOn);
		try {
			vocabulary.getAttribute(vocabulary.getRealNamespace(attribute));
			validateAttribute();
		} catch (VocabularyException ve) {
			if (attribute.getURI().startsWith("http://device.sprintpcs.com/namespace/xpcs")) {
				// sprint have defined their own namespace but not published a schema
			} else {
				validatorError("Attribute not defined in vocabulary");
			}
		}
	}

	void validateAttribute() {
		try {
			String vocabularyCollectionType = vocabulary.getAttributeProperty(attribute,
					Constants.COLLECTIONTYPE).getLocalName();
			if (vocabularyCollectionType.equals(Constants.SIMPLE)) {
				checkDatatype(object);
			} else {
				if (!(object instanceof Resource)) {
					validatorError("is not a container.");
				} else {
					if (vocabularyCollectionType.equals(Constants.BAG)) {
						validateContainer(Constants.BAG);
					} else if (vocabularyCollectionType.equals(Constants.SEQ)) {
						validateContainer(Constants.SEQ);
					}
				}
			}
		} catch (VocabularyException ve) {
			outputMsg(ve.getMessage());
		}
		try {
			Resource vocabularyComponent = vocabulary.getAttributeProperty(attribute,
					Constants.COMPONENT);
			if ((vocabularyComponent != null) && (currentComponent != null)) {
				// In UAProf CcppAccept can live in BrowserUA or
				// SoftwarePlatform
				if (!(vocabularyComponent.getURI().equals(vocabulary.getRealNamespace(
						currentComponent).getURI()))
						&& !(attribute.getLocalName().startsWith("CcppAccept"))) {
					validatorError("Attribute is in wrong component should be "
							+ vocabularyComponent.toString());
				}
			}
		} catch (VocabularyException ve) {
			outputMsg(ve.getMessage());
			outputMsg("Could not get component for " + attribute.getURI()
					+ " from vocabulary");
		}
	}

	void validateContainer(String container) {
		if (!isContainer(object.asResource(), container)) {
			validatorError("does not match collection type " + container);
		} else {
			try {
				NodeIterator i = getContainerIterator(object.asResource());
				while (i.hasNext()) {
					checkDatatype(i.nextNode());
				}
			} catch (ProfileProcessingException ppe) {//
			}
		}
	}

	void validatorError(String message) {
		outputMsg("Error [C: " + currentComponent + ", A: " + attribute + "] " + message);
		isProfileValid = false;
	}

	/**
	 * @return the validationMessages
	 */
	public StringBuffer getValidationMessages() {
		return validationMessages;
	}

	/**
	 * @return the isProfileValid
	 */
	boolean isProfileValid() {
		return isProfileValid;
	}

	void checkDatatype(RDFNode valueToCheck) {
		try {
			Resource vocabularyType = vocabulary.getAttributeProperty(attribute,
					Constants.TYPE);
			String type = vocabularyType.getLocalName();
			if (!type.equals("Any")) {
				if (!(valueToCheck instanceof Literal)) {
					validatorError("Expected literal found resource");
				} else {
					Resource normalizedAttribute = vocabulary.getRealNamespace(attribute);
					Literal value = valueToCheck.asLiteral();
					if (value.getDatatype() != null) {
						// does datatype match schema
						String profileDatatype = value.getDatatypeURI();
						try {
							String vocabularyDatatype = vocabulary.getAttributeProperty(
									normalizedAttribute, Constants.TYPE).getURI();
							if (!vocabularyDatatype.toLowerCase().equals(
									profileDatatype.toLowerCase())) {
								validatorError("datatypes do not match "
										+ profileDatatype + " " + vocabularyDatatype);
							}
						} catch (VocabularyException ve) {//
						}
					} else {
						// no datatyping information - error!
						String prfURI = normalizedAttribute.getNameSpace();
						if (vocabulary.usesRDFDatatyping(prfURI)) {
							if (!printedWarningAboutDatatyping) {
								outputMsg("Warning: UAProf 2 profile omits RDF datatyping information");
							}
							printedWarningAboutDatatyping = true;
							isProfileValid = false;
						}
					}
					// check the value matches the XML Schema datatype
					if (datatypeValidationOn) {
						String valueString = value.getLexicalForm().trim();
						String regExpression = vocabulary.datatypeExpressions.get(type);

						if (regExpression == null) {
							validatorError("Datatype error: " + type + " is not defined");
						} else if (!valueString.matches(regExpression)) {
							validatorError("Datatype error:" + valueString
									+ " does not match datatype " + type);
						}
					}
				}
			}
		} catch (VocabularyException ve) {
		}
	}

	public boolean printedWarningAboutDatatyping() {
		return printedWarningAboutDatatyping;
	}
}
