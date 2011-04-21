package com.hp.hpl.deli.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A class that creates several threads for web crawling tasks. 
 */
class Crawler {

	/**
	 * @param crawlDb	The crawl database.
	 * @param maxThread	The number of threads.
	 * @param theWorker The inner class that implements a worker that does the crawling.
	 * @param parent	The parent class of the worker.
	 */
	Crawler(List<Resource> crawlDb, int maxThread, Constructor theWorker, Object parent) {
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
