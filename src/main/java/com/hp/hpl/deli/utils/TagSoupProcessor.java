package com.hp.hpl.deli.utils;

public class TagSoupProcessor {
	/**
	 * Tag soup approach to get data out of profile
	 * 
	 * @param profile The profile as ASCII text
	 * @param startTag the start tag to look for
	 * @param endTag the end tag to look for
	 * @return the value
	 */
	static String getTagSoup(String profile, String startTag, String endTag) {
		int begin = profile.indexOf(startTag);
		int end = profile.indexOf(endTag, begin + startTag.length());
		if (begin >= 0 && end >= 0 && begin < end) {
			return profile.substring(begin + startTag.length(), end).trim();
		}
		return null;
	}
	
	static String getBackwardTagSoup(String profile, String startTag, String endTag) {
		int end = profile.indexOf(endTag);
		int begin = profile.lastIndexOf(startTag, end);
		if (begin >= 0 && end >= 0 && begin < end) {
			return profile.substring(begin + startTag.length(), end).trim();
		}
		return null;
	}
}
