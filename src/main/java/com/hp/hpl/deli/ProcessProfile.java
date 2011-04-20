package com.hp.hpl.deli;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Create a Profile object.
 */
class ProcessProfile extends AbstractProcessProfile {
	/** The resulting profile attributes. */
	private Vector<ProfileAttribute> profileAttributes = new Vector<ProfileAttribute>();

	ProcessProfile(ProfileProcessor configuration,
			boolean currentlyProcessingDefaults, StringReader reader) {
		super(configuration, currentlyProcessingDefaults);
		arpReader.read(model, reader, "");
		processModel();
	}

	ProcessProfile(ProfileProcessor configuration,
			boolean currentlyProcessingDefaults, String resource) throws IOException {
		super(configuration, currentlyProcessingDefaults);
		arpReader.read(model, ModelUtils.getResource(resource),
				resource.startsWith("http") ? resource : "");
		processModel();
	}

	ProcessProfile(ProfileProcessor configuration,
			boolean currentlyProcessingDefaults, InputStream in, String uri) {
		super(configuration, currentlyProcessingDefaults);
		arpReader.read(model, in, uri.startsWith("http") ? uri : "");
		processModel();
	}

	ProcessProfile(ProfileProcessor configuration,
			boolean currentlyProcessingDefaults, Model model) {
		super(configuration, currentlyProcessingDefaults);
		this.model = model;
		processModel();
	}

	void processProperties(Statement statement) {
		profileAttributes.add(new ProfileAttribute(configuration, statement,
				currentComponent, currentlyProcessingDefaults));
	}

	void retrieveDefaultProfile(String url) {
		try {
			ProcessProfile tempProfile = new ProcessProfile(configuration, true, url);
			profileAttributes.addAll(tempProfile.getProfileAttributes());
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * @return the profileAttributes
	 */
	Vector<ProfileAttribute> getProfileAttributes() {
		return profileAttributes;
	}

	Profile getProfile() {
		return new Profile(configuration.getVocabulary(), profileAttributes);
	}
}
