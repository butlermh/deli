package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.hp.hpl.deli.Constants;

class UrlUtils {

	static void savePage(String filename, StringBuffer data) throws IOException {
		String path = Constants.PROFILES_OUTPUT_FILE.substring(0,
				Constants.PROFILES_OUTPUT_FILE.lastIndexOf('/'));
		new File(path).mkdirs();
		OutputStream out = null;
		try {
			out = new FileOutputStream(Constants.PROFILES_OUTPUT_FILE);
			byte[] bytes = data.toString().getBytes();
			out.write(bytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
	
	static String downloadFileToLocalDir(String uri) throws IOException {
		InputStream streamToURL = null;
		FileOutputStream theFileStream = null;
		StringBuffer result = new StringBuffer();

		try {
			// open InputStream to URL
			URL theURL = new URL(uri);
			streamToURL = theURL.openStream();

			// open OutputStream to local file
			String filepath = "build/manufacturerProfiles/" + theURL.getHost()
					+ theURL.getFile();
			String path = filepath.substring(0, filepath.lastIndexOf('/'));
			new File(path).mkdirs();
			File localFile = new File(filepath);
			localFile.createNewFile();
			theFileStream = new FileOutputStream(localFile);

			int ch;
			while ((ch = streamToURL.read()) != -1) {
				result.append((char) ch);
				theFileStream.write(ch);
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
		
		StringBuffer input = new StringBuffer();
		InputStream s = null;
		try {
			URL u = new URL(pageUri);
			URLConnection conn = u.openConnection();
			// have to fake the user agent to get results back from Google
			conn.setRequestProperty(USER_AGENT, USER_AGENT_STRING);
			conn.connect();
			s = conn.getInputStream();
			int ch;
			while ((ch = s.read()) != -1) {
				input.append((char) ch);
			}
		} catch (IOException io) {
			throw new IOException(io.getMessage());
		} finally {
			if (s != null)
				s.close();
		}
		return input.toString();
	}
	
}
