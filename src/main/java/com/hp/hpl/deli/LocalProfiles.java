package com.hp.hpl.deli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class lets you override incoming UAProf profiles
 */
class LocalProfiles extends ModelUtils {
	private static Log log = LogFactory.getLog(LocalProfiles.class);

	/*
	 * Unfortunately due to user-agent cloaking we cannot use an efficient
	 * structure like a HashMap to associate profile references with user-agent
	 * strings. Instead we use a vector and scan for user-agents in a specific
	 * order e.g. Opera pretends to be IE and Netscape, so check for that first
	 * IE pretends to be Netscape so check for that next Finally Netscape is
	 * honest so check for that.
	 * 
	 * There is lots of scope for improvement here. For example Cocoon 2 uses
	 * the Accept header as a fallback if it does not recognise the user-agent
	 * string. Morphis can read AvantGo headers which contain CC/PP-like
	 * information about the target device. Techniques like this could be
	 * incorporated in future revisions.
	 */

	/** The useragent strings for each device. */
	private Vector<String> useragents = new Vector<String>();

	/** The corresponding profile references for each local profile. */
	private HashMap<String, String> profileRefs = new HashMap<String, String>();

	LocalProfiles(ProfileProcessor configuration) throws IOException {
		String localProfilesFile = configuration.getWorkspace().get(DeliSchema.localProfilesFile, "");
		Model model = ModelUtils.loadModel(localProfilesFile);
		finishConstruction(model);
	}

	/**
	 * The constructor reads in a local profile file.
	 * 
	 * @param localProfile The local profile file.
	 */
	LocalProfiles(String localProfilesFile) throws IOException {
		Model model = ModelUtils.loadModel(localProfilesFile);
		finishConstruction(model);
	}

	private void finishConstruction(Model model) {
		/** Local profile path. */
		String localProfilesPath = null;
		StmtIterator si = model.listStatements(null, DeliSchema.localProfilesPath,
				(RDFNode) null);
		localProfilesPath = si.hasNext() ? si.nextStatement().getString()
				: localProfilesPath;
		ResIterator profilesIter = model.listSubjectsWithProperty(RDF.type,
				DeliSchema.Profile);
		while (profilesIter.hasNext()) {
			Resource p = profilesIter.nextResource();

			if (p.hasProperty(DeliSchema.useragent)
					&& p.hasProperty(DeliSchema.uaprofUri)) {
				// Real profile
				profileRefs.put(getPropertyString(p, DeliSchema.useragent),
						p.getProperty(DeliSchema.uaprofUri).getResource().getURI());
				useragents.add(getPropertyString(p, DeliSchema.useragent));
			}

			if (p.hasProperty(DeliSchema.file) && p.hasProperty(DeliSchema.useragent)) {
				// Local Profile
				String path = localProfilesPath + "/"
						+ getPropertyString(p, DeliSchema.file);
				profileRefs.put(getPropertyString(p, DeliSchema.useragent), path);
				useragents.add(getPropertyString(p, DeliSchema.useragent));
			}
		}
	}

	/**
	 * This method retrieves the profile URL for the device based on the
	 * user-agent string.
	 * 
	 * @param s The user-agent string.
	 * @return The profile URL.
	 */
	String getReference(String s) throws UnknownUserAgentException {
		for (String u : useragents) {
			if (s.toLowerCase().lastIndexOf(u.toLowerCase()) != -1) {
				log.info("Useragent string " + s + " maps on to " + profileRefs.get(u));
				return profileRefs.get(u);
			}
		}
		throw new UnknownUserAgentException("No such mapping in LocalProfiles");
	}
}