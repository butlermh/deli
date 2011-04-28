package com.hp.hpl.deli.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

class SynchronizedModel {
	private Model model = ModelFactory.createDefaultModel();

	public synchronized void add(Model m) {
		model.add(m);
	}

	public synchronized Model getModel() {
		return model;
	}
}
