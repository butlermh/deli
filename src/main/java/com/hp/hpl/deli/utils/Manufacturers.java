package com.hp.hpl.deli.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.deli.DeliSchema;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

class Manufacturers {
	private final static String[] oldManufacturers = { "sonyericsson", "nokia",
			"toshiba", "samsung", "SAMSUNG", "panasonic", "Research_In_Motion_Ltd.",
			"motorola", "sharp", "LG", "siemens", "alcatel",
			"High_Tech_Computer_Corporation", "amoi", "Kyocera",
			"Kyocera_Wireless_Corporation", "Kyocera_Wireless_Corp", "ZTE",
			"ZTE_Corporation", "Acer_Incorporated", "Acer", "Vodafone", "Pantech",
			"Zonda", "Ericsson", "FLY", "Huawei", "INQ", "Modelabs", "Openwave", "Palm",
			"Philips", "Haier", "ASUSTeK_COMPUTER_INC.", "NEC",
			"IAC_OKWAP_Mobile_Communications", "Audiovox", "AUDIOVOX", "BenQ_Corporation" };

	private final static String[] newManufacturers = { "http://www.sonyericsson.com/",
			"http://www.nokia.com/", "http://www.toshiba.com/",
			"http://www.samsungmobile.com/", "http://www.samsungmobile.com/",
			"http://www.panasonic.com/", "http://www.rim.com/",
			"http://www.motorola.com/", "http://www.sharp-mobile.com/",
			"http://www.lg.com", "http://www.siemens.com/",
			"http://www.alcatel-mobilephones.com/", "http://www.htc.com/",
			"http://www.amoi.com/", "http://www.kyocera-wireless.com/",
			"http://www.kyocera-wireless.com/", "http://www.kyocera-wireless.com/",
			"http://www.zte.com.cn/", "http://www.zte.com.cn/", "http://www.acer.com/",
			"http://www.acer.com/", "http://www.vodafone.com/",
			"http://www.pantech.com/", "http://www.zondatelecom.com/",
			"http://www.ericsson.com/", "http://www.fly-phone.com/",
			"http://www.huawei.com/", "http://www.inqmobile.com/",
			"http://www.modelabs.com/", "http://www.openwave.com/",
			"http://www.palm.com/", "http://www.philips.com/", "http://www.haier.com/",
			"http://www.asus.com/", "http://www.nec.com/", "http://mobile.iac.com/",
			"http://www.audiovox.com/", "http://www.audiovox.com/",
			"http://www.benq.com/" };

	private HashMap<String, String> manufacturers = new HashMap<String, String>();

	private List<Statement> toList(StmtIterator si) {
		List<Statement> statements = new ArrayList<Statement>();
		while (si.hasNext()) {
			statements.add(si.nextStatement());
		}
		return statements;
	}
	
	private void replaceResource(Model prfs, Resource a, Resource b) {
		for (Statement s : toList(a.listProperties())) {
			prfs.add(b, s.getPredicate(), s.getObject());
			prfs.remove(s);
		}
		for (Statement s : toList(prfs.listStatements(null, null, a))) {
			prfs.add(s.getSubject(), s.getPredicate(), b);
			prfs.remove(s);
		}
	}

	Manufacturers(Model prfs) {
		// rewrite manufacturer URLs
		for (int i = 0; i < oldManufacturers.length; i++) {
			String oldManufacturer = ProcessUAProfMetadata.BASE + "manufacturers#"
					+ oldManufacturers[i];
			String newManufacturer = newManufacturers[i];
			Resource rOldManufacturer = prfs.getResource(oldManufacturer);
			Resource rNewManufacturer = prfs.createResource(newManufacturer);
			replaceResource(prfs, rOldManufacturer, rNewManufacturer);
		}

		ResIterator profilesIter = prfs
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			Resource manufacturer = resource.getProperty(DeliSchema.manufacturedBy)
					.getObject().asResource();
			String manufacturerURI = manufacturer.getURI();
			if (manufacturerURI.startsWith(ProcessUAProfMetadata.BASE)) {
				System.out.println("Error: using manufacturer URI " + manufacturerURI);
			} else if (manufacturer.hasProperty(RDFS.label)) {
				String manufacturerName = manufacturer.getProperty(RDFS.label)
						.getString();
				if (!manufacturers.containsKey(manufacturerName)) {
					manufacturers.put(manufacturerName, manufacturer.getURI());
				} else {
					String checkManufacturer = manufacturers.get(manufacturerName);
					if (!checkManufacturer.equals(manufacturerURI)) {
						System.out.println("Error: manufacturer " + manufacturerName
								+ " maps on to " + manufacturerURI + " and "
								+ checkManufacturer);
					}
				}
			}
		}
	}

	String get(String manufacturerName) {
		if (manufacturers.containsKey(manufacturerName)) {
			return manufacturers.get(manufacturerName);
		}
		return null;
	}
}
