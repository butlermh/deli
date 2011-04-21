package com.hp.hpl.deli.utils;

import java.util.HashMap;

class PropertyEntry implements Comparable<PropertyEntry> {
	private String name;

	private HashMap<String, Integer> map;

	PropertyEntry(String name, HashMap<String, Integer> map) {
		this.name = name;
		this.map = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(PropertyEntry other) {
		return other.name.compareTo(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the map
	 */
	public HashMap<String, Integer> getMap() {
		return map;
	}
}

