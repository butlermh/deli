package com.hp.hpl.deli.utils;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;

abstract public class ValidatorProcessProfile {
	boolean validRDF = true;

	boolean profileValidFlag = true;

	boolean unreachable = false;

	JenaReader myARPReader = new JenaReader();
	
	Model model = ModelFactory.createDefaultModel();

	ValidatorProcessProfile() {
		myARPReader.setProperty("WARN_RESOLVING_URI_AGAINST_EMPTY_BASE", "EM_IGNORE");
		myARPReader.setErrorHandler(new RDFErrorHandler() {
			// ARP parser error handling routines
			public void warning(Exception e) {
				outputMsg("RDF parser warning:" + e.getMessage());
			}

			public void error(Exception e) {
				outputMsg("RDF parser error:" + e.getMessage());
				if (validRDF) {
					validRDF = false;
				}
				profileValidFlag = false;
			}

			public void fatalError(Exception e) {
				e.printStackTrace();
				error(e);
			}
		});
	}
	
	abstract void outputMsg(String s);
}
