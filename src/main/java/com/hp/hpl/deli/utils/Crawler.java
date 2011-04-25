package com.hp.hpl.deli.utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A class that creates several threads for web crawling tasks.
 */
class Crawler {

	private List<Resource> crawlDb = Collections
			.synchronizedList(new LinkedList<Resource>());

	Crawler(String configFile, int maxThread,
			Constructor<? extends CrawlerWorker> theWorker, Object parent)
			throws IOException {
		this(ModelUtils.loadModel(configFile), maxThread, theWorker, parent);
	}

	Crawler(Model profiles, int maxThread,
			Constructor<? extends CrawlerWorker> theWorker, Object parent) {
		ResIterator profilesIter = profiles.listSubjectsWithProperty(RDF.type,
				DeliSchema.Profile);
		while (profilesIter.hasNext()) {
			crawlDb.add(profilesIter.nextResource());
		}
		initThreads(maxThread, theWorker, parent);
	}

	/**
	 * @return the crawlDb
	 */
	// List<Resource> getCrawlDb() {
	// return crawlDb;
	// }

	/**
	 * @param crawlDb The crawl database.
	 * @param maxThread The number of threads.
	 * @param theWorker The inner class that implements a worker that does the
	 *            crawling.
	 * @param parent The parent class of the worker.
	 */
	Crawler(List<Resource> crawlDb, int maxThread,
			Constructor<? extends CrawlerWorker> theWorker, Object parent) {
		this.crawlDb = crawlDb;
		initThreads(maxThread, theWorker, parent);
	}

	private void initThreads(int maxThread,
			Constructor<? extends CrawlerWorker> theWorker, Object parent) {
		LinkedList<CrawlerWorker> threads = new LinkedList<CrawlerWorker>();
		for (int i = 0; i < maxThread; i++) {
			try {
				CrawlerWorker thread = (CrawlerWorker) theWorker.newInstance(parent);
				thread.setCrawlDb(crawlDb);
				thread.start();
				threads.add(thread);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		try {
			for (int j = 0; j < maxThread; j++) {
				threads.get(j).join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
