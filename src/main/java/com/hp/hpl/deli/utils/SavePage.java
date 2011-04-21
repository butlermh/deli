package com.hp.hpl.deli.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.hp.hpl.deli.Constants;

public class SavePage {

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
	
}
