package com.hp.hpl.deli;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class is a representation of a CC/PP profile as a Vector of
 * ProfileAttribute.
 */
public class Profile implements Serializable {

	/** Comment for <code>serialVersionUID</code> */
	private static final long serialVersionUID = 1L;

	/** Logging. */
	private static Log log = LogFactory.getLog(Profile.class);

	/** The profile data. */
	private Vector<ProfileAttribute> data = new Vector<ProfileAttribute>();

	/** Attribute order. */
	private HashMap<Resource, Vector<Integer>> attributePosition;

	/** Is the profile valid */
	protected boolean isProfileValid = true;

	/** Validate the profile? */
	private boolean validateProfile = false;

	SchemaCollection vocabulary = Workspace.getInstance().vocabulary;

	/**
	 * Create a new profile from a resource. This method does not use the
	 * profile cache.
	 *
	 * @param resource The resource representing a profile.
	 */
	public Profile(String resource) {
		log.debug("Profile: Processing profile reference");
		Vector<ProfileAttribute> unresolved = new Vector<ProfileAttribute>();
		ProcessProfile profile = new ProcessProfile();
		unresolved.addAll(profile.process(resource));
		finishConstruction(unresolved);
	}

	/**
	 * Used for validating profiles.
	 *
	 * @param model The profile model to validate.
	 */
	public Profile(Model model) {
		log.debug("Profile: validating model");
		this.validateProfile = true;
		Vector<ProfileAttribute> unresolved = new Vector<ProfileAttribute>();
		ProcessProfile profile = new ProcessProfile();
		unresolved.addAll(profile.processModel(model));
		finishConstruction(unresolved);
	}

	/**
	 * Create a new profile from a HTTP Request. This method caches profile
	 * references but not profile-diffs.
	 *
	 * @param request The HTTP request.
	 */
	public Profile(HttpServletRequest request) {
		log.info("Profile: Processing HTTP request");
		ProcessHttpRequest theRequest = new ProcessHttpRequest(request);
		Vector<String> profileReferences = theRequest.getReferenceVector();
		Vector<ProfileAttribute> unresolved = new Vector<ProfileAttribute>();
		for (String s : profileReferences) {
			log.info("Retrieving profile " + s);
			unresolved.addAll(Workspace.getInstance().profileCache.get(s).get());
		}
		if (theRequest.getDiffVector().size() > 0) {
			for (String s : theRequest.getDiffVector()) {
				StringReader reader = new StringReader(s);
				ProcessProfile profile = new ProcessProfile();
				unresolved.addAll(profile.process(reader));
			}
		}
		finishConstruction(unresolved);
	}

	/**
	 * This finishes construction by applying the resolution rule to any
	 * duplicate attributes.
	 */
	private void finishConstruction(Vector<ProfileAttribute> unresolved) {
		if (Workspace.getInstance().printProfileBeforeMerge) {
			log.info(this.toString());
		}

		// MERGE DUPLICATE ENTRIES APPROPRIATELY
		if (attributePosition == null) {
			attributePosition = new HashMap<Resource, Vector<Integer>>();
		}

		for (ProfileAttribute pa : unresolved) {
			if (attributePosition.containsKey(pa.getName())) {
				Vector<Integer> list = attributePosition.get(pa.getName());
				boolean resolved = false;
				for (int currentIntRef : list) {
					ProfileAttribute currentPa = (ProfileAttribute) data.get(currentIntRef);

					Resource paURI = vocabulary.getAttributeProperty(pa.getName(), Constants.COMPONENT);
					Resource currentPaURI = vocabulary.getAttributeProperty(currentPa.getName(), Constants.COMPONENT);

					if ((paURI != null) && (currentPaURI != null) && paURI.equals(currentPaURI)) {
						((ProfileAttribute) data.get(currentIntRef)).set(pa);
						resolved = true;
					}
				}

				if (!resolved) {
					(attributePosition.get(pa.getName())).add(new Integer(data.size()));
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

	public Vector<ProfileAttribute> get() {
		return data;
	}

	public ProfileAttribute get(int i) {
		return data.get(i);
	}

	public int size() {
		return data.size();
	}

	/**
	 * Retrieve a profile attribute with a specific name.
	 *
	 * @param attributeName The attribute qname.
	 * @return The profile attribute.
	 */

	// note this returns the _first_ attribute with that qname!! (if it exists).
	public ProfileAttribute getAttribute(Resource attributeName) {
		if (attributePosition.get(attributeName) != null) {
			Integer i = (attributePosition.get(attributeName)).firstElement();
			return (ProfileAttribute) data.get(i.intValue());
		}
		return null;
	}

	/**
	 * Retrieve a profile attribute with a specific name. (assumes that the
	 * attribute name is unique, regardless of URI: returns the first value
	 * encountered with qname fragment equal to that passed in).
	 *
	 * @param attributeName the UNQUALIFIED attribute name (ie just the
	 *            fragment)
	 * @return The profile attribute
	 */
	public ProfileAttribute getAttribute(String attributeName) {
		if (attributePosition == null) {
			attributePosition = new HashMap<Resource, Vector<Integer>>();
		}

		Set<Resource> keys = attributePosition.keySet();
		for (Resource qn : keys) {
			if (qn.getLocalName().equals(attributeName)) {
				return data.get(((Integer) (attributePosition.get(qn)).firstElement()).intValue());
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
		for (ProfileAttribute p : data) {
			result.append(p.toString());
		}
		return result.toString();
	}

	public class ProcessProfile extends Utils {

		/** The resulting profile attributes. */
		private Vector<ProfileAttribute> profileAttributes;

		/** The Jena model containing the profile. */
		private Model model = null;

		/** The current componnent. */
		private String currentComponent = null;

		/** The profile does not have type statements on components */
		private boolean profileOmitsTypeInformationFromComponents = false;

		/** Is the block currently being processed a default block? */
		private boolean currentlyProcessingDefaults = false;

		/** The ARP parser. */
		private JenaReader arpReader;

		public ProcessProfile() {
			this(false);
		}

		private ProcessProfile(boolean isDefaultProfile) {
			profileAttributes = new Vector<ProfileAttribute>();
			currentlyProcessingDefaults = isDefaultProfile;
			arpReader = new JenaReader();
			arpReader.setErrorHandler(new RDFErrorHandler() {
				// ARP parser error handling routines
				public void warning(Exception e) {//
				}

				public void error(Exception e) {//
				}

				public void fatalError(Exception e) {//
				}
			});
			arpReader.setProperty("WARN_RESOLVING_URI_AGAINST_EMPTY_BASE", "EM_IGNORE");
		}

		public Vector<ProfileAttribute> process(StringReader reader) {
			model = ModelFactory.createDefaultModel();
			try {
				arpReader.read(model, reader, "");
			} catch (Exception e) {
				log.error("JenaProcessProfile: Could not process profile-diff", e);
			}
			return processModel(model);
		}

		public Vector<ProfileAttribute> process(String resource) {
			model = ModelFactory.createDefaultModel();

			try {
				InputStream in = Workspace.getInstance().getResource(resource);

				if (resource.startsWith("http")) {
					arpReader.read(model, in, resource);
				} else {
					arpReader.read(model, in, "");
				}
			} catch (Exception e) {
				log.error("JenaProcessProfile: Could not load profile " + resource, e);
				e.printStackTrace();
			}

			return processModel(model);
		}

		/**
		 * @return the root of the jena model
		 * @throws Exception
		 */
		private Resource findRoot(Model model) {
			Resource root = null;
			StmtIterator si = model.listStatements();
			if (si.hasNext()) {
				if (si.nextStatement() != null) {
					RDFNode currentNode = si.nextStatement().getObject();

					while (root == null) {
						Selector s = new SelectorImpl((Resource) null, (Property) null, currentNode);
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
		 * Converts the Jena data structure to a vector of attributes.
		 *
		 * @return The processed vector of attributes.
		 */
		protected Vector<ProfileAttribute> processModel(Model model) {
			this.model = model;

			NsIterator namespaces = model.listNameSpaces();

			if (!namespaces.hasNext()) {
				log.info("The profile does not contain any RDF information - use W3C RDF Validator");
				isProfileValid = false;
			}

			while (namespaces.hasNext()) {
				String theNamespace = namespaces.nextNs();

				if (!theNamespace.equals(RDF.getURI())) {
					Property componentProperty = model.createProperty(theNamespace
							+ Workspace.getInstance().componentProperty);

					if (!vocabulary.getRealNamespace(theNamespace).equals(theNamespace)) {
						log.info("WARNING: The profile uses namespace aliasing");
						log.info("Aliased namespace is:    " + theNamespace);
						log.info("Real namespace is: " + vocabulary.getRealNamespace(theNamespace));
					} else if (vocabulary.getRealNamespace(theNamespace) == null){
						// profile uses unknown namespace
						log.debug("Profile uses unknown namespace: " + theNamespace);
						try {
							log.debug("Attempting to load new schema");
							vocabulary.addSchema(theNamespace);
							log.debug("Loaded schema " + theNamespace + "succesfully");
						} catch (Exception e) {
							log.debug("Failed to load schema " + theNamespace);
						}
					} else {
						log.debug("The profile uses " + theNamespace);
					}

					NodeIterator componentList = model.listObjectsOfProperty(componentProperty);

					if (componentList.hasNext() || validateProfile) {
						while (componentList.hasNext()) {
							Resource component = (Resource) componentList.next();

							if (component.hasProperty(RDF.type)) {
								currentComponent = getPropertyUri(component, RDF.type);
							} else {
								currentComponent = null;
								if (profileOmitsTypeInformationFromComponents == false) {
									log.error("WARNING: Profile omits type information");
									this.profileOmitsTypeInformationFromComponents = true;
								}
							}

							processAttributeList(component);
						}
					}
				}
			}
			if (validateProfile) {
				StmtIterator rootProperties = findRoot(model).listProperties();
				while (rootProperties.hasNext()) {
					Statement s = rootProperties.nextStatement();
					if (!s.getPredicate().getLocalName().equals("component")) {
						System.out.println("Error: " + s.getPredicate().toString()
								+ " there is a problem with components");
						isProfileValid = false;
					}
				}
			}
			return profileAttributes;
		}

		private void processAttributeList(Resource current) {

			// for each namespace
			NsIterator namespaces = model.listNameSpaces();
			while (namespaces.hasNext()) {
				String vocabularyURI = namespaces.nextNs();
				if (!vocabularyURI.equals(RDF.getURI())) {
					processNamespace(vocabularyURI, current);
				}
			}

			// for each property attached to this node
			StmtIterator theProperties = current.listProperties();
			while (theProperties.hasNext()) {
				Statement statement = theProperties.nextStatement();

				if (!statement.getPredicate().equals(RDF.type)) {
					new ProcessAttribute(statement);
				}
			}
		}

		private void processNamespace(String vocabularyURI, Resource current) {
			Property defaultProperty = model.createProperty(vocabularyURI + "defaults");
			Property capsDefaultProperty = model.createProperty(vocabularyURI + "Defaults");
			StmtIterator theProperties = current.listProperties();

			while (theProperties.hasNext()) {
				Statement statement = theProperties.nextStatement();
				Property predicate = statement.getPredicate();
				String property = predicate.toString();
				String propertyNamespace = predicate.getNameSpace();

				if (!predicate.equals(RDF.type)) {
					if (predicate.equals(defaultProperty) || predicate.equals(capsDefaultProperty)) {
						processDefaultBlock(statement);
					} else {
						if (!validateProfile
								&& !predicate.getLocalName().equals(Workspace.getInstance().componentProperty)) {
							if (((vocabulary.getNamespaceLookup(vocabularyURI) != null) || Workspace.getInstance().processUnconfiguredNamespaces)
									&& vocabularyURI.equals(propertyNamespace)) {
								if (currentComponent == null) {
									log.error("Component is null for: " + property);

									Vector<Map<String, Resource>> props = vocabulary
											.getAttPropertiesWithAttName(property);

									if (props != null) {
										// only one entry for this qname, so use
										// its component
										log.info("Determining component from vocabulary");
										currentComponent = (((Resource) (props.firstElement()).get(Constants.COMPONENT))
												.toString());
									} else {
										// try to get it from the rdf:ID or
										// rdf:about
										log.info("Determining component from local ID");
										currentComponent = (vocabularyURI + current.getLocalName());
									}
								}
							}
						}
					}
				}
			}
		}

		private void processDefaultBlock(Statement statement) {
			// Found a default block, process it recursively.
			StmtIterator iter = ((Resource) statement.getObject()).listProperties();

			if (!iter.hasNext()) {
				String url = statement.getObject().toString();
				log.info("Default block references another profile - retrieving profile " + url);

				if (validateProfile) {
					UAProfValidator tempValidator = new UAProfValidator();

					if (!tempValidator.process(url)) {
						log.error("Referenced profile " + url + " is invalid");
						isProfileValid = false;
					}
				} else {
					ProcessProfile tempProfile = new ProcessProfile(true);
					profileAttributes.addAll(tempProfile.process(url));
				}
			} else {
				// Process the default subtree
				currentlyProcessingDefaults = true;

				processAttributeList((Resource) statement.getObject());
				currentlyProcessingDefaults = false;
			}
		}

		class ProcessAttribute {
			private String attributeName;

			private Statement attributeStatement;

			private Resource ccppType;

			private Resource ccppCollectionType;

			private Resource qn;

			public void validatorError(String component, String attribute, String message) {
				System.out.println("Error [C: " + component + ", A: " + attribute + "] " + message);
				isProfileValid = false;
			}

			ProcessAttribute(Statement attributeStatement) {
				this.attributeStatement = attributeStatement;
				currentComponent = vocabulary.getRealNamespace(currentComponent);

				String qualifiedAttribute = attributeStatement.getPredicate().toString();
				qn = ResourceFactory.createResource(vocabulary.getRealNamespace(qualifiedAttribute));
				attributeName = attributeStatement.getPredicate().getLocalName();

				Resource currentComponentUri = null;

				if (currentComponent != null) {
					currentComponentUri = ResourceFactory.createResource(vocabulary.getRealNamespace(currentComponent));
				}

				Resource ccppComponent = vocabulary.getAttributeProperty(qn, Constants.COMPONENT);
				ccppCollectionType = vocabulary.getAttributeProperty(qn, Constants.COLLECTIONTYPE);
				ccppType = vocabulary.getAttributeProperty(qn, Constants.type);

				Map<String, Resource> attribute = vocabulary.getAttribute(qn);

				if (attribute == null) {
					validatorError(currentComponent, qualifiedAttribute, "Attribute not defined in vocabulary");

					if (!validateProfile && Workspace.getInstance().processUndefinedAttributes) {
						String collectionType = determineCollectionType(attributeStatement);
						vocabulary.addAttributeToVocabulary(qualifiedAttribute, currentComponent, collectionType);
					}
				} else if (validateProfile) {
					processValues();

					if ((ccppComponent != null) && (currentComponent != null)) {
						// Nasty hack here, to overcome the fact UAProf can
						// never make
						// up its mind whether
						// CcppAccept lives in BrowserUA or SoftwarePlatform
						if (!(ccppComponent.getURI().equals(currentComponent)) && (currentComponent != null)
								&& !(attributeName.equals("CcppAccept"))
								&& !(attributeName.equals("CcppAccept-Language"))
								&& !(attributeName.equals("CcppAccept-Charset"))) {
							validatorError(currentComponent, attributeName,
									"Attribute is in wrong component should be " + ccppComponent.toString());
						}
					}
				}

				if (!validateProfile
						&& (((attribute == null) && Workspace.getInstance().processUndefinedAttributes) || (attribute != null))) {
					Vector<String> attributeValue = getValues();

					if (vocabulary.getAttributeProperty(qn, Constants.COMPONENT) == null) {
						System.out.println("Could not retrieve " + attributeName);
						System.out.println(qn.toString());

						if (currentComponentUri != null) {
							log.info(currentComponentUri.toString());
						}
					}

					ProfileAttribute newAttribute = new ProfileAttribute();
					newAttribute.set(qn, currentComponentUri, attributeValue, currentlyProcessingDefaults);
					profileAttributes.add(newAttribute);
				}
			}

			private void processValues() {
				if (ccppCollectionType.getLocalName().equals(Constants.SIMPLE)) {
					checkDatatype(attributeStatement.getObject());
				} else {
					validateContainer(Constants.BAG);
					validateContainer(Constants.SEQ);
				}
			}

			private void validateContainer(String container) {
				if (ccppCollectionType.getLocalName().equals(container)) {
					if (!(attributeStatement.getObject() instanceof Resource)) {
						validatorError(currentComponent, attributeName, "does not match collection type " + container);
					} else {

						if (!(getContainer(attributeStatement, container).hasNext())) {
							validatorError(currentComponent, attributeName, "does not match collection type "
									+ container);
						} else {
							NodeIterator i = getContainerIterator(attributeStatement);

							if (i != null) {
								while (i.hasNext()) {
									checkDatatype(i.nextNode());
								}
							}
						}
					}
				}
			}

			private void checkDatatype(RDFNode value) {
				if (!ccppType.getLocalName().equals("Any")) {
					if (!(value instanceof Literal)) {
						validatorError(currentComponent, attributeName, "Expected literal found resource");
					} else {
						checkRDFdatatype((Literal) value);

						if (Workspace.getInstance().datatypeValidationOn) {
							String valueString = ((Literal) value).getLexicalForm();
							String type = ccppType.getLocalName();
							String regExpression = (String) (Workspace.getInstance().datatypeExpressions.get(type));

							if (regExpression == null) {
								validatorError(currentComponent, attributeName, "Datatype error: " + type
										+ " is not defined");
							} else if (!valueString.matches(regExpression)) {
								validatorError(currentComponent, attributeName, "Datatype error:" + valueString
										+ " does not match datatype " + type);
							}
						}
					}
				}
			}

			private void checkRDFdatatype(Literal object) {
				if (object.getDatatype() != null) {
					String profileDtype = object.getDatatypeURI();
					String vocabDtype = vocabulary.getAttributeProperty(qn, Constants.type).toString();

					if (!vocabDtype.toLowerCase().equals(profileDtype.toLowerCase())) {
						validatorError(currentComponent, attributeName, " datatypes do not match " + profileDtype + " "
								+ vocabDtype);
					}
				} else {
					String prfURI = ResourceFactory.createResource(qn.toString()).getNameSpace();
					if (vocabulary.usesRDFDatatyping(prfURI)) {
						validatorError(currentComponent, attributeName,
								" UAProf 2 profile omits RDF datatyping information");
					}
				}
			}

			private Vector<String> getValues() {
				Vector<String> attributeValue = new Vector<String>();

				if (attributeStatement.getObject() instanceof Literal) {
					Literal object = attributeStatement.getLiteral();
					attributeValue.add(new String(object.getValue().toString().trim()));
					checkRDFdatatype(object);
				} else {
					NodeIterator i = getContainerIterator(attributeStatement);

					if (i != null) {
						while (i.hasNext()) {
							RDFNode theNode = i.nextNode();
							if (theNode instanceof Literal) {
								Literal object = (Literal) theNode;
								attributeValue.add(new String(object.getValue().toString().trim()));
								checkRDFdatatype(object);
							} else {
								String object = ((Resource) theNode).getURI();
								attributeValue.add(new String(object.trim()));
							}
						}
					}
				}

				return attributeValue;
			}

			protected String determineCollectionType(Statement statement) {
				if (!(statement.getObject() instanceof Literal)) {
					StmtIterator iterSeq = getContainer(statement, Constants.SEQ);
					if (iterSeq.hasNext()) {
						return Constants.SEQ;
					}

					StmtIterator iterBag = getContainer(statement, Constants.BAG);
					if (iterBag.hasNext()) {
						return Constants.BAG;
					}
				}
				return Constants.SIMPLE;
			}

			private StmtIterator getContainer(Statement statement, String container) {
				Selector s = new SelectorImpl((Resource) statement.getObject(), RDF.type, statement.getModel()
						.createResource(RDF.getURI() + container));
				return statement.getModel().listStatements(s);
			}

			private NodeIterator getContainerIterator(Statement statement) {
				Model model = statement.getModel();
				Resource object = (Resource) statement.getObject();
				if (getContainer(statement, Constants.SEQ).hasNext()) {
					return model.getSeq(object).iterator();
				} else if (getContainer(statement, Constants.BAG).hasNext()) {
					return model.getBag(object).iterator();
				}
				return null;
			}
		}
	}

	/**
	 * @return Returns the isProfileValid.
	 */
	boolean isProfileValid() {
		return isProfileValid;
	}
}
