package com.hp.hpl.deli.utils;

class PropertyValueEntry implements Comparable<PropertyValueEntry> {
	private String name;

	private int frequency;

	PropertyValueEntry(String name, int frequency) {
		this.name = name;
		this.frequency = frequency;
	}

	public int compareTo(PropertyValueEntry other) {
		int r = other.frequency - frequency;
		if (r == 0)
			return other.name.compareTo(name);
		return r;
	}
	
	/**
	 * @return the name
	 */
	String getName() {
		return name;
	}

	/**
	 * @return the frequency
	 */
	int getFrequency() {
		return frequency;
	}
}