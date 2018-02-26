package com.zamek.wob.domain;


/**
 * Convert exception class. 
 * It contains the reason of error 
 * 
 * @author zamek
 *
 */
public class ConvertException extends Exception {

	/**
	 * Constructor for the exception
	 * 
	 * @param message error message
	 */
	public ConvertException(String message) {
		super(message);
	}
	
}
