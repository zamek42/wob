package com.zamek.wob;

import org.apache.commons.lang3.StringUtils;

import com.zamek.wob.config.Config;
import com.zamek.wob.csv.CSVImporter;
import com.zamek.wob.ftp.FtpUploader;

public class WobApplication {
	
	private static void configNotExists() {
		try {
			Config.createDefaultConfigFile();
			System.out.println("Generating default application.properties with default configuration. Please modify it, and restart application"); //$NON-NLS-1$
		}
		catch (Exception e) {
			System.out.println("Cannot generate application.properties:( " + e.getMessage()); //$NON-NLS-1$
		}
		System.exit(-1);
	}
	
	private static void fileError(String file) {
		System.out.println(String.format("Please set the correct %s file name in the application.properties", file)); //$NON-NLS-1$
		System.exit(-1);
	}
	
	private static void startConversion(String inputFile, String responseFile) {
		try {
			CSVImporter csvImporter = new CSVImporter(inputFile, responseFile, Config.getInstance().getEntityManager());
			if (csvImporter.process()) {
				FtpUploader ftpUploader= new FtpUploader(responseFile);
				ftpUploader.upload();
			}
			System.out.println("Conversion finished successfully"); //$NON-NLS-1$
		}
		catch (Exception e) {
			System.out.println("Fatal error:"+e.getMessage()); //$NON-NLS-1$
		}
		
	}
	/**
	 * Entry point of the application. 
	 * 
	 * App try to process the given csv file as an input. The valid rows will be inserted to the database in the application
	 * 
	 * Usage: app inputfile 
	 * 
	 * @param args if inputfile is empty, the default filename will be used from the application.properties
	 * 
	 */
	public static void main(String[] args) {
		if (!Config.isConfigExists()) {
			configNotExists();
		}
		String inputFile = Config.getInstance().getInputFileName();
		if (StringUtils.isEmpty(inputFile)) 
			fileError("input"); //$NON-NLS-1$
		
		String responseFile = Config.getInstance().getResponseFileName();
		if (StringUtils.isEmpty(responseFile)) 
			fileError("response"); //$NON-NLS-1$
			
		startConversion(inputFile, responseFile);
	}
}
