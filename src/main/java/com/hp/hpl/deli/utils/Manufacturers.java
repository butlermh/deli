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
	private final static String[] oldManufacturers = { "sonyericsson", "Nokia",
			"toshiba", "samsung", "SAMSUNG", "panasonic", "Research_In_Motion_Ltd.",
			"Motorola", "sharp", "LG", "siemens", "alcatel",
			"High_Tech_Computer_Corporation", "HTC Corporation", "amoi", "Kyocera",
			"Kyocera_Wireless_Corporation", "Kyocera_Wireless_Corp", "ZTE",
			"ZTE_Corporation", "Acer_Incorporated", "Acer", "Vodafone", "Pantech",
			"Zonda", "Ericsson", "FLY", "Huawei", "INQ", "Modelabs", "Openwave", "Palm",
			"Philips", "Haier", "ASUSTeK_COMPUTER_INC.", "NEC",
			"IAC_OKWAP_Mobile_Communications", "Audiovox", "AUDIOVOX",
			"BenQ_Corporation", "Ezze", "Mitsubishi", "MiTAC", "Samart", "OPPO",
			"Nexian", "Garmin-Asus", "Bellwave", "Uriver", "GHT", "UTStarcom",
			"Gigabyte_Communications_Corporation", "Global_High_Tech", "WellcoM",
			"IXI_Mobile_Inc.", "modu_LTD", "LAVA", "LGE", "Handspring", "PALM", "SHARP",
			"LG Electronics", "SANYO", "Sony Ericsson Mobile Communications", "MOTOROLA",
			"Nec", "Sony Ericsson Mobile Communications AB", "VODAFONE",
			"Ericsson Mobile Communications AB", "HTC", "HUAWEI", "SONYERICSSON",
			"ZONDA", "Kyocera Wireless Corp", "Sony Ericsson", "Global High Tech",
			"Gigabyte Communications Corporation", "PANTECH", "Vertu", "Philips",
			"Acer Incorporated", "MOT", "Hewlett-Packard", "IXI Mobile Inc.",
			"Sagem Wireless", "palm", "modu LTD", "ASUS", "ZTE Corporation", "MODELABS",
			"T-Mobile", "PHILIPS", "Vibo", "Samsung", "TOSHIBA", "Onda Communication S.P.A.", "Motorola Inc" };

	private final static String[] newManufacturers = { "http://www.sonyericsson.com/",
			"http://www.nokia.com/", "http://www.toshiba.com/",
			"http://www.samsungmobile.com/", "http://www.samsungmobile.com/",
			"http://www.panasonic.com/", "http://www.rim.com/",
			"http://www.motorola.com/", "http://www.sharp-mobile.com/",
			"http://www.lg.com", "http://www.siemens.com/",
			"http://www.alcatel-mobilephones.com/", "http://www.htc.com/",
			"http://www.htc.com/", "http://www.amoi.com/",
			"http://www.kyocera-wireless.com/", "http://www.kyocera-wireless.com/",
			"http://www.kyocera-wireless.com/", "http://www.zte.com.cn/",
			"http://www.zte.com.cn/", "http://www.acer.com/", "http://www.acer.com/",
			"http://www.vodafone.com/", "http://www.pantech.com/",
			"http://www.zondatelecom.com/", "http://www.ericsson.com/",
			"http://www.fly-phone.com/", "http://www.huawei.com/",
			"http://www.inqmobile.com/", "http://www.modelabs.com/",
			"http://www.openwave.com/", "http://www.palm.com/",
			"http://www.philips.com/", "http://www.haier.com/", "http://www.asus.com/",
			"http://www.nec.com/", "http://mobile.iac.com/", "http://www.audiovox.com/",
			"http://www.audiovox.com/", "http://www.benq.com/",
			"http://www.ezzemobile.com/", "http://www.mitsubishielectric.fr/",
			"http://www.mitac.com/", "http://www.samartcorp.com/",
			"http://www.oppothai.com/", "http://www.nexian.co.id/",
			"http://www.garminasus.com/", "http://www.bellwave.com/",
			"http://www.iriver.com/", "http://www.globalhightech.fr/",
			"http://www.utstar.com/", "http://www.gigabytecm.com/",
			"http://www.globalhightech.fr/", "http://www.wellcommobile.com/",
			"http://www.ixi.com/", "http://www.modumobile.com/",
			"http://www.lavamobiles.com/", "http://www.lg.com/",
			"http://www.handspring.com/", "http://www.palm.com/",
			"http://www.sharp-mobile.com/", "http://www.lg.com/",
			"http://www.sanyo-mobile.com/", "http://www.sonyericsson.com/",
			"http://www.motorola.com/", "http://www.nec.com/",
			"http://www.sonyericsson.com/", "http://www.vodafone.com/",
			"http://www.ericsson.com/", "http://www.htc.com/", "http://www.huawei.com/",
			"http://www.sonyericsson.com/", "http://www.zondatelecom.com/",
			"http://www.kyocera-wireless.com/", "http://www.sonyericsson.com/",
			"http://www.globalhightech.fr/", "http://www.gigabytecm.com/",
			"http://www.pantech.com/", "http://www.vertu.com/",
			"http://www.philips.com/", "http://www.acer.com/",
			"http://www.motorola.com/", "http://www.hp.com/", "http://www.ixi.com",
			"http://www.sagem.com/", "http://www.palm.com/",
			"http://www.modumobile.com/", "http://www.asus.com/",
			"http://www.zte.com.cn/", "http://asmobile.ehosting.com.tw/",
			"http://www.t-mobile.com", "http://www.mimiria.net/",
			"http://www.philips.com/", "http://www.samsungmobile.com/",
			"http://gphone.toshiba.co.jp/", "http://www.ondacommunication.com/", "http://www.motorola.com/" };

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
			String manufacturerURI = rNewManufacturer.getURI();
			if (!manufacturerURI.endsWith("/")) {
				manufacturerURI += "/";
			}
			manufacturers.put(oldManufacturers[i], manufacturerURI);
		}

		List<Resource> fixManufacturer = new ArrayList<Resource>();
		ResIterator profilesIter = prfs
				.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			Resource manufacturer = resource.getProperty(DeliSchema.manufacturedBy)
					.getObject().asResource();
			String manufacturerURI = manufacturer.getURI();
			if (!manufacturerURI.endsWith("/") && !manufacturerURI.contains("#")) {
				fixManufacturer.add(manufacturer);
			} else if (manufacturerURI.contains("#")) {
				System.out.println(manufacturerURI);
			}
		}
		for (Resource resource : fixManufacturer) {
			Resource newResource = prfs.createResource(resource.getURI() + "/");
			replaceResource(prfs, resource, newResource);
		}

		profilesIter = prfs.listSubjectsWithProperty(DeliSchema.manufacturedBy);
		while (profilesIter.hasNext()) {
			Resource resource = profilesIter.nextResource();
			Resource manufacturer = resource.getProperty(DeliSchema.manufacturedBy)
					.getObject().asResource();
			String manufacturerURI = manufacturer.getURI();
			if (manufacturerURI.startsWith(ProcessUAProfMetadata.BASE)) {
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
