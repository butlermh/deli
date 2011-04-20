package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

class CreateCrawlDb {
	
	private List<Resource> crawlDb = Collections
	.synchronizedList(new LinkedList<Resource>());

	CreateCrawlDb(String configFile) throws IOException {
		Model profiles = ModelUtils.loadModel(configFile);
		
		// retrieve all the profiles
		ResIterator profilesIter = profiles.listSubjectsWithProperty(RDF.type,
				DeliSchema.Profile);
		while (profilesIter.hasNext()) {
			crawlDb.add(profilesIter.nextResource());
		}
	}

	/**
	 * @return the crawlDb
	 */
	List<Resource> getCrawlDb() {
		return crawlDb;
	}
}
