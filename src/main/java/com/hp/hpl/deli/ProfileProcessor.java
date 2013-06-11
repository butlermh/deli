package com.hp.hpl.deli;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Processor that converts various inputs into a CC/PP Profile.
 */
public class ProfileProcessor {
	
	private final DeliConfiguration workspace;
	private final SchemaCollection vocabulary;
	
	/**
	 * Constructor.
	 * 
	 * @param filename The configuration file.
	 * @throws IOException Thrown if there is a problem loading the workspace.
	 */
	public ProfileProcessor(String filename) throws IOException {
		this.workspace = new DeliConfiguration(filename);
		this.vocabulary = new SchemaCollection(workspace);
	}
	
	/**
	 * Create a CC/PP profile.
	 * 
	 * @param reader A StringReader containing the profile.
	 * @return The profile.
	 */
	public Profile processProfile(StringReader reader) {
		return new ProcessProfile(this, false, reader).getProfile();
	}

	/**
	 * Create a CC/PP profile.
	 * 
	 * @param resource A URI pointing to the profile.
	 * @return The profile.
	 * @throws IOException Thrown if there is a problem processing the profile.
	 */
	public Profile processProfile(String resource) throws IOException {
		return new ProcessProfile(this, false, resource).getProfile();
	}

	/**
	 * Create a CC/PP profile.
	 * 
	 * @param in An InputStream containing the profile.
	 * @param uri The URI of the profile.
	 * @return The profile.
	 */
	public Profile processProfile(InputStream in, String uri) {
		return new ProcessProfile(this, false, in, uri).getProfile();
	}
	

	/**
	 * Create a CC/PP profile.
	 * 
	 * @param model The underlying RDF model.
	 * @return The profile.
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
