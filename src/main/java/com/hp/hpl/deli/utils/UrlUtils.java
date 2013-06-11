package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

class UrlUtils {

	static void savePage(String filename, StringBuffer data) throws IOException {
		String path = filename.substring(0,
				filename.lastIndexOf('/'));
		new File(path).mkdirs();
		OutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			byte[] bytes = data.toString().getBytes();
			out.write(bytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
	
	static StringBuffer loadLocalFile(String filepath) throws IOException {
		InputStream streamToURL = null;
		StringBuffer result = new StringBuffer();

		try {
			File localFile = new File(filepath);
			if (localFile.exists()) {
				streamToURL = new FileInputStream(filepath);
				int ch;
				while ((ch = streamToURL.read()) != -1) {
					result.append((char) ch);
				}
			}
		} catch (IOException ioe) {
			throw new IOException(ioe.getMessage());
		} finally {
			if (streamToURL != null)
				streamToURL.close();
		}
		return result;
	}
	
	/**
	 * Get the contents of a particular URL as a String.
	 * 
	 * @param pageUri the URL.
	 * @return the contents as a String.
	 * @throws Exception
	 */
	static String getURL(String pageUri) throws IOException {
		final String USER_AGENT = "User-Agent";
		final String USER_AGENT_STRING = "Mozilla/5.0 (X11; U; Linux i686; rv:1.7.3) Gecko/20041020 Firefox/0.10.1";
				
		InputStream streamToURL = null;
		FileOutputStream theFileStream = null;
		StringBuffer result = new StringBuffer();

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
				// have to fake the user agent to get results back from Google
				conn.setRequestProperty(USER_AGENT, USER_AGENT_STRING);
				conn.connect();
				streamToURL = conn.getInputStream();

				localFile.createNewFile();
				theFileStream = new FileOutputStream(localFile);

				int ch;
				while ((ch = streamToURL.read()) != -1) {
					result.append((char) ch);
					theFileStream.write(ch);
				}
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
