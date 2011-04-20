package com.hp.hpl.deli.functional;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.Profile;
import com.hp.hpl.deli.ProfileProcessor;

abstract public class CCPPProfileTest {
	static Log log = LogFactory.getLog(CCPPProfileTest.class);
	
	static ProfileProcessor configuration;
	
	String correctNamespace;

	String profileFile;
	
	Profile profile;
	
	@BeforeClass
	public static void oneTimeSetup() throws IOException {
		configuration = new ProfileProcessor(Constants.CONFIG_FILE);
	}
	
	public void setUp(String correctNamespace, String profileFile) {
		this.correctNamespace = correctNamespace;
		this.profileFile = profileFile;
		profile = configuration.processProfile(getResource(profileFile), profileFile);
	}
	
	public static InputStream getResource(String name) {
		try {
			log.info("Getting resource: " + name);
			String fallback = "src/test/resources/" + name;
			File file = new File(name);
			File fallbackFile = new File(fallback);
			if (name.startsWith("http") || name.startsWith("file")
					|| name.startsWith("jndi")) {
				log.info("Via stream");
				return new URL(name).openStream();
			} else if (file.exists()) {
				log.info("Via file input stream" + file);
				return new FileInputStream(name);
			} else if (fallbackFile.exists()) {
				log.info("Via file input stream " + fallback);
				return new FileInputStream(fallbackFile);
			} else {
				log.info("Via classloader");
				InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
				if (in == null) {
					log.info("Trying " + fallback);
					in = ClassLoader.getSystemClassLoader().getResourceAsStream(fallback);
				}
				return in;
			}

		} catch (FileNotFoundException fnfe) {
			log.error("Could not load file: " + name, fnfe);
		} catch (Exception e) {
			log.error("Cannot retrieve resource: " + name, e);
		}

		return null;
	}

	protected void testAttribute(String correctAttributeName, String correctComponent, String correctResolution,
			String correctCollection, String correctType) throws Exception {
		if (profile.getAttribute(correctAttributeName) != null) {
			if (correctComponent != null) {
				String component = profile.getAttribute(correctAttributeName).getComponent().getLocalName();
				assertTrue("Attribute: " + correctAttributeName + ", Component name was " + component + " should be "
						+ correctComponent, component.equals(correctComponent));
			}

			if (correctResolution != null) {
				String resolution = profile.getAttribute(correctAttributeName).getResolution();
				assertTrue("Attribute: " + correctAttributeName + ", Resolution rule was " + resolution + " should be "
						+ correctResolution, resolution.equals(correctResolution));
			}

			if (correctCollection != null) {
				String collection = profile.getAttribute(correctAttributeName).getCollectionType();
				assertTrue("Attribute: " + correctAttributeName + ", Collection type was " + collection + " should be "
						+ correctCollection, collection.equals(correctCollection));
			}

			if (correctType != null) {
				String type = profile.getAttribute(correctAttributeName).getType();
				assertTrue("Attribute: " + correctAttributeName + ", attribute type was " + type + " should be "
						+ correctType, type.equals(correctType));
			}

			if (correctNamespace != null) {
				String namespace = profile.getAttribute(correctAttributeName).getName().getNameSpace();
				assertTrue("Attribute: " + correctAttributeName + ", namespace was " + namespace + " should be "
						+ correctNamespace, namespace.equals(correctNamespace));
			}
		}
	}
}
