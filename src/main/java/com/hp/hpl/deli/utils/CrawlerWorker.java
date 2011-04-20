package com.hp.hpl.deli.utils;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;

abstract class CrawlerWorker extends Thread {

	private List<Resource> crawlDb = null;

	public CrawlerWorker() {}
	
	void setCrawlDb(List<Resource> crawlDb) {
		this.crawlDb = crawlDb;
	}
	
	public void run() {
		while (!crawlDb.isEmpty()) {
			Resource uri = null;
			synchronized (crawlDb) {
				uri = crawlDb.get(0);
				crawlDb.remove(0);
			}
			try {
				processURI(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	abstract void processURI(Resource uri);
}
