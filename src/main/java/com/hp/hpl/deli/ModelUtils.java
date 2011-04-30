package com.hp.hpl.deli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;

public class ModelUtils {
	private static Log log = LogFactory.getLog(ModelUtils.class);

	/**
	 * Convenience method to get a String property attached to a resource.
	 * 
	 * @param resource
	 * @param property
	 * @return
	 */
	public static String getPropertyString(Resource resource, Property property) {
		return resource.hasProperty(property) ? resource.getProperty(property)
				.getLiteral().getLexicalForm() : null;
	}

	/**
	 * This method returns a local or global resource. If it is a local
	 * resource, DELI uses the appropriate method depending on whether DELI is
	 * running within a servlet or not.
	 * 
	 * @param name The name of the resource. If it is a URL then it is a global
	 *            resource.
	 * @return A InputStream for the resource.
	 */
	public static InputStream getResource(String name) throws IOException {
		try {
			log.info("Getting resource: " + name);
			String fallback = "src/main/resources/" + name;
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
				InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(
						name);
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

		throw new IOException("Could not load" + name);
	}

	static InputSource getInputSource(String name) throws IOException {
		InputStream in = getResource(name);
		Reader resource = new InputStreamReader(in);
		InputSource inputSource = new InputSource(resource);

		return inputSource;
	}

	public static Model loadModel(String filename) throws IOException {
		log.debug("Loading " + filename);
		Model newModel = ModelFactory.createDefaultModel();
		newModel.read(getResource(filename), "", "N3");
		return newModel;
	}

	public static void writeModel(Model model, String filename, String language) {
		String path = filename.substring(0, filename.lastIndexOf('/'));
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		RDFWriter writer = model.getWriter(language);
		writer.setProperty("allowBadURIs", "true");
		OutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			writer.write(model, out, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
