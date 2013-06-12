package com.hp.hpl.deli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.deli.ProcessedRequest;

@SuppressWarnings("javadoc")
class TempProfileDiff {
	/** Logging support. */
	private static Log log = LogFactory.getLog(TempProfileDiff.class);
	public String profileDiff;

	public String profileDiffDigest;

	TempProfileDiff(String filename, boolean noCarriageReturnsInProfileDiffs,
			boolean broken) {
		try {
			profileDiff = readFile(filename);

			if (broken) {
				profileDiffDigest = ProcessedRequest.calculateProfileDiffDigest(profileDiff.toString()
						+ "another random string to corrupt the digest value!", true);
			} else {
				profileDiffDigest = ProcessedRequest.calculateProfileDiffDigest(
						profileDiff.toString(), true);
			}

			if (noCarriageReturnsInProfileDiffs) {
				profileDiff = ProcessedRequest.removeWhitespaces(profileDiff.toString());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public String readFile(String filename) throws IOException {
		StringBuffer file = new StringBuffer();
		BufferedReader fileIn = new BufferedReader(new FileReader(filename));
		String str;

		while ((str = fileIn.readLine()) != null) {
			file.append(str);
		}

		return file.toString();
	}
}
