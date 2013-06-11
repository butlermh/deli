package com.hp.hpl.deli;

/**
 * Thrown if an unknown vocabulary attribute is encountered.
 */
public class VocabularyException extends Exception {

	public VocabularyException(String string) {
		super(string);
	}

	public VocabularyException() {
		super();
	}
	
	private static final long serialVersionUID = 5247920128864035392L;

}
