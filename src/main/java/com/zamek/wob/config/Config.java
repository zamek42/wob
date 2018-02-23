package com.zamek.wob.config;

import java.io.File;


import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.zamek.wob.util.HasLogger;


public class Config implements HasLogger {

	private final static String CONFIG_FILE_NAME = "application.properties"; //$NON-NLS-1$
	

	private static Config instance=null;
    
	private Configuration config;
    
	private Config() {
		Configurations configs = new Configurations();
		try {
		    this.config = configs.properties(new File(CONFIG_FILE_NAME));
		}
		catch (ConfigurationException cex) {
			getLogger().error("Configuration error:"+cex.getMessage()); //$NON-NLS-1$
		}	
	}
	
	public synchronized static Config getInstance() {
		if (instance==null)
			instance = new Config();
		return instance;
	}


	
}
