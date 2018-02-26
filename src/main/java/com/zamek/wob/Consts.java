package com.zamek.wob;

/**
 * Commonly used constants for the application
 * 
 * @author zamek
 *
 */
public interface Consts {
	/**
	 * Persistence Unit name
	 */
	final static String PERSISTENCE_NAME ="Persistence";	 //$NON-NLS-1$
	
	/**
	 * CSV field delimiter
	 */
	final static char FIELD_DELIMITER = ';';
	
	/**
	 * CSV Record separator
	 */
	final static String RECORD_SEPARATOR = "\n"; //$NON-NLS-1$
	
	final static String FTP_KEY = "ftp."; 		 //$NON-NLS-1$
	
	/**
	 * Ftp host key in the config file
	 */
	final static String FTP_CONFIG_KEY_HOST = FTP_KEY + "host"; //$NON-NLS-1$
	
	/**
	 * Ftp language parameter in the config file
	 */
	final static String FTP_CONFIG_KEY_LANGUAGE = FTP_KEY+"language"; //$NON-NLS-1$
	
	/**
	 * Default value of ftp language
	 */
	final static String FTP_CONFIG_DEF_LANGUAGE = "en"; //$NON-NLS-1$

	/**
	 * Ftp timezone parameter in the config file
	 */
	final static String FTP_CONFIG_KEY_TIME_ZONE = FTP_KEY+"timezone"; //$NON-NLS-1$
	
	/**
	 * Default ftp timezone  
	 */
	final static String FTP_CONFIG_DEF_TIME_ZONE = "Europe/Budapest"; //$NON-NLS-1$
		
}
