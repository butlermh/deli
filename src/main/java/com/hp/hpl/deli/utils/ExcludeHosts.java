package com.hp.hpl.deli.utils;

import java.util.Vector;

class ExcludeHosts {
	// We ignore these hosts as they are not valid sources of UAProf
	// profiles
	private final static String[] EXCLUDE_LIST = { "www.cs.umbc.edu", "crschmidt.net",
			"burningdoor.com", "start.cefriel.it", "www.xxxwap.com", "139.91.183.30",
			"www2.cs.uh.edu", "www.thauvin.net", "213.249.206.60", "www.sun.com",
			"140.136.206.230", "wap.samsungmobile.com/uaprof/SUWON.xml", "gpf.pri.ee",
			"w3development.de", "www.w3.org", "194.204.48.96", "blog.livedoor.jp",
			"www.mail-archive.com", "www.port4.info", "www.hcs-systems.ch",
			"rzm-hamy-wsx.rz.uni-karlsruhe.de", "cmsdomino.brookes.ac.uk",
			"ggordillo.blogspot.com", "rynt02.rz.uni-karlsruhe.de", "www.ninebynine.org",
			"vs.sourceforge.net", "194.204.48.96", "attila.sdsu.edu", "www.it.lut.fi",
			"google.com", "youtube.com", "search?q=cache", "search?hl=", "?q=filetype",
			"/intl/en/about.html", "reference.com", "www.cems.uwe.ac.uk/",
			"support.t-mobile.com/", "www.lac.inf.puc-rio.br", "aiti.mit.edu",
			"mobile.allblogs.it", "bundesarchiv.de", "www.eurobuch.com", "www.mixx.com",
			"cfs-ash1.facebook.com", "www.sitemaps.org", "www.mainelawnmowers.co.uk",
			"www.nigeriamusicmovement.com", "www.kloster-bonnbach.de", "validator.openmobilealliance.org"};

	private Vector<String> excludeHosts = new Vector<String>();

	ExcludeHosts() {
		for (String s : EXCLUDE_LIST) {
			excludeHosts.add(s);
		}
	}

	boolean excludedHost(String deviceURI) {
		for (String next : excludeHosts) {
			if (deviceURI.contains(next)) {
				return true;
			}
		}
		return false;
	}
}
