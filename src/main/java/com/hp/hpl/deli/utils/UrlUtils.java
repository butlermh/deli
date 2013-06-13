package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Utilities for dealing with URLs //TODO not sure how accurate this is.
 */
class UrlUtils {

	/**
	 * @param filename The name of the file.
	 * @param data The data.
	 * @throws IOException Thrown if there is a problem writing the file.
	 */
	static void savePage(String filename, StringBuffer data) throws IOException {
		String path = filename.substring(0, filename.lastIndexOf('/'));
		new File(path).mkdirs();
		FileUtils.writeStringToFile(new File(filename), data.toString());
	}

	/**
	 * Load a local file.
	 * 
	 * @param filepath The path to the file.
	 * @return The file as a String.
	 * @throws IOException Thrown if there is a problem reading the file.
	 */
	static String loadLocalFile(String filepath) throws IOException {
		return FileUtils.readFileToString(new File(filepath));
	}

	/**
	 * Get the contents of a particular URL as a String.
	 * 
	 * @param pageUri the URL.
	 * @return the contents as a String.
	 * @throws IOException Thrown if there is a problem writing the output.
	 */
	static String getURL(String pageUri) throws IOException {
		final String USER_AGENT = "User-Agent";
		final String USER_AGENT_STRING = "Mozilla/5.0 (X11; U; Linux i686; rv:1.7.3) Gecko/20041020 Firefox/0.10.1";

		InputStream streamToURL = null;
		FileOutputStream theFileStream = null;
		String result = null;

		try {
			URL theURL = new URL(pageUri);
			// open OutputStream to local file
			String filepath = "target/manufacturerProfiles/" + theURL.getHost()
					+ theURL.getFile();
			String path = filepath.substring(0, filepath.lastIndexOf('/'));
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			File localFile = new File(filepath);
			if (localFile.exists()) {
				result = loadLocalFile(filepath);
			} else {

				// open InputStream to URL

				URLConnection conn = theURL.openConnection();
				conn.setReadTimeout(2000);
				// have to fake the user agent to get results back from Google
				conn.setRequestProperty(USER_AGENT, USER_AGENT_STRING);
				conn.connect();
				streamToURL = conn.getInputStream();
				result = IOUtils.toString(streamToURL);
				FileUtils.writeStringToFile(localFile, result);
			}
		} catch (IOException ioe) {
			throw new IOException(ioe.getMessage());
		} finally {
			if (streamToURL != null)
				streamToURL.close();
			if (theFileStream != null)
				theFileStream.close();
		}
		return result.toString();
	}
}
