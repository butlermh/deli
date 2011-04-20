package com.hp.hpl.deli;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Processor that converts various inputs into a CC/PP Profile.
 */
public class ProfileProcessor {
	DeliConfiguration workspace;
	
	SchemaCollection vocabulary;
	
	/**
	 * @param filename The configuration file.
	 */
	public ProfileProcessor(String filename) throws IOException {
		workspace = new DeliConfiguration(filename);
		vocabulary = new SchemaCollection(workspace);
	}
	
	/**
	 * Create a CC/PP profile.
	 * 
	 * @param reader A StringReader containing the profile.
	 * @return
	 */
	public Profile processProfile(StringReader reader) {
		return new ProcessProfile(this, false, reader).getProfile();
	}

	/**
	 * Create a CC/PP profile.
	 * 
	 * @param resource A URI pointing to the profile.
	 * @return
	 */
	public Profile processProfile(String resource) throws IOException {
		return new ProcessProfile(this, false, resource).getProfile();
	}

	/**
	 * Create a CC/PP profile.
	 * 
	 * @param in An InputStream containing the profile.
	 * @param uri The URI of the profile.
	 * @return
	 */
	public Profile processProfile(InputStream in, String uri) {
		return new ProcessProfile(this, false, in, uri).getProfile();
	}
	

	/**
	 * Create a CC/PP profile.
	 * 
	 * @param model
	 * @return
	 */
	Profile processProfile(Model model) {
		return new ProcessProfile(this, false, model).getProfile();
	}

	/**
	 * @return the workspace
	 */
	DeliConfiguration getWorkspace() {
		return workspace;
	}

	/**
	 * @return the vocabulary
	 */
	SchemaCollection getVocabulary() {
		return vocabulary;
	}
}
