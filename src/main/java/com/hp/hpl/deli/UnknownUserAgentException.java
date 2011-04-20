package com.hp.hpl.deli;

/**
 * Thrown if an unknown user agent is encountered.
 */
public class UnknownUserAgentException extends Exception {

	private static final long serialVersionUID = 2218327125900631712L;

	public UnknownUserAgentException(String string) {
		super(string);
	}
	
	public UnknownUserAgentException() {
		super();
	}

}
