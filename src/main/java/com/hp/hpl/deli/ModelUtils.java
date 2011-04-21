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

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;

public class ModelUtils {
	private static Log log = LogFactory.getLog(ModelUtils.class);

	/**
	 * Convenience method to get a URI property attached to a resource.
	 * 
	 * @param resource
	 * @param property
	 * @return
	 */
	public static String getPropertyUri(Resource resource, Property property) {
		return resource.hasProperty(property) ? resource.getProperty(property)
				.getObject().asResource().getURI() : null;
	}

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
				log.debug("Via stream");
				return new URL(name).openStream();
			} else if (file.exists()) {
				log.debug("Via file input stream" + file);
				return new FileInputStream(name);
			} else if (fallbackFile.exists()) {
				log.debug("Via file input stream " + fallback);
				return new FileInputStream(fallbackFile);
			} else {
				log.debug("Via classloader");
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

	public static JenaReader configureArp() {
		JenaReader arpReader = new JenaReader();
		arpReader.setErrorHandler(new RDFErrorHandler() {
			// ARP parser error handling routines
			public void warning(Exception e) {//
			}

			public void error(Exception e) {//
			}

			public void fatalError(Exception e) {//
			}
		});
		arpReader.setProperty("WARN_RESOLVING_URI_AGAINST_EMPTY_BASE", "EM_IGNORE");
		return arpReader;
	}

	public static void writeModel(Model model, String filename, String language) {
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
