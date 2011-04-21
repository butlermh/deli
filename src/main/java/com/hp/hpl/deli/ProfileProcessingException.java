package com.hp.hpl.deli;

/**
 * Thrown if there is a problem processing a mal-formed profile.
 */
class ProfileProcessingException extends Exception {

	private static final long serialVersionUID = -8950598616897666177L;

	public ProfileProcessingException(String string) {
		super(string);
	}

	public ProfileProcessingException() {
		super();
	}
	
}
