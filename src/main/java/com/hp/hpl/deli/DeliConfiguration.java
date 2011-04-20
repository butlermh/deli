package com.hp.hpl.deli;

import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Stores configuration data for the CC/PP or UAProf processor.
 */
class DeliConfiguration {
	private Model model;

	DeliConfiguration(String filename) throws IOException {
		model = ModelUtils.loadModel(filename);
	}

	/**
	 * @return the model
	 */
	Model getModel() {
		return model;
	}

	long get(Property p, long current) {
		long HOURINMILLISECONDS = 1000 * 60 * 60;
		StmtIterator si2 = model.listStatements(null, p, (RDFNode) null);
		return si2.hasNext() ? si2.nextStatement().getLong() * HOURINMILLISECONDS
				: current;
	}

	int get(Property p, int current) {
		StmtIterator si = model.listStatements(null, p, (RDFNode) null);
		return si.hasNext() ? si.nextStatement().getInt() : current;
	}

	boolean get(Property p, boolean current) {
		StmtIterator si = model.listStatements(null, p, (RDFNode) null);
		return si.hasNext() ? si.nextStatement().getBoolean() : current;
	}

	String get(Property p, String current) {
		StmtIterator si = model.listStatements(null, p, (RDFNode) null);
		return si.hasNext() ? si.nextStatement().getString() : current;
	}
}