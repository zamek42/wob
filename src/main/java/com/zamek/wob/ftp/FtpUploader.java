package com.zamek.wob.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import com.zamek.wob.Consts;
import com.zamek.wob.config.Config;
import com.zamek.wob.util.HasLogger;

/**
 * Ftp uploader 
 * 
 * <p>This class can upload the given file to an ftp server. Ftp server can be configurable in the application.properties.</p> 
 * <p>Parameters are:</p>
 * <ul>
 * 		<li>ftp.user</li>
 * 		<li>ftp.password</li>
 * 		<li>ftp.port</li>
 * 		<li>ftp.language</li>
 * 		<li>ftp.timezone</li>
 * </ul>
 * Default values are int Consts 
 * @see com.zamek.wob.Consts
 *   
 * @author zamek
 *
 */
public class FtpUploader implements HasLogger {

	private final static boolean DEBUG = true;
	
	private String fileName;
	private String hostUrl;
	private int hostPort;
	private String user;
	private String password;
	
	/**
	 * Constructor for FtpUploader.
	 * 
	 * @param fileName name of the file to upload
	 */
	public FtpUploader(String fileName) {
		this.fileName = fileName;
		Configuration cfg = Config.getInstance().getConfig();
		this.hostUrl = cfg.getString(Consts.FTP_CONFIG_KEY_HOST);
		this.hostPort = cfg.getInt(Consts.FTP_CONFIG_KEY_PORT, Consts.FTP_CONFIG_DEF_PORT);
		this.user = cfg.getString(Consts.FTP_CONFIG_DEF_USER, Consts.FTP_CONFIG_DEF_USER);
		this.password = cfg.getString(Consts.FTP_CONFIG_KEY_PASSWORD, Consts.FTP_CONFIG_DEF_PASSWORD);
	}
	
	/**
	 * Start uploading.
	 * 
	 * 
	 * @throws Exception if connection error or login error or any other file handling error happend.
	 * 
	 */
	public void upload() throws Exception {
		Path path = Paths.get(this.fileName);
		if (Files.notExists(path)) 
			throw new IOException(String.format("%s file is not exists", this.fileName)); //$NON-NLS-1$
		
		FTPClient ftp = new FTPClient();
		
		FTPClientConfig config = setFtpConfig();
		ftp.configure(config );
		try {
		      int reply;
		      ftp.connect(this.hostUrl, this.hostPort);
		      if (DEBUG) 
		    	  getLogger().debug(String.format("Connected to %s, reply:%s", this.hostUrl, ftp.getReplyString())); //$NON-NLS-1$

		      reply = ftp.getReplyCode();

		      if(!FTPReply.isPositiveCompletion(reply)) {
		    	  ftp.disconnect();
		    	  throw new IOException(String.format("Ftp Server at %s said %s", this.hostUrl, Integer.valueOf(reply))); //$NON-NLS-1$
		      }
		      
		      if (!StringUtils.isBlank(this.user) &&  !ftp.login(this.user, this.password)) {
		    	  ftp.disconnect();
		    	  throw new IOException(String.format("FTP Authentication error with %s user", this.user)); //$NON-NLS-1$
		      }
		    
		      ftp.setFileType(FTP.ASCII_FILE_TYPE);
		      
		      try (InputStream in = Files.newInputStream(path)) {
		    	  ftp.storeFile(this.fileName, in);
		      }
		 }
		finally {
			ftp.logout();
		}
	}
	
	private static FTPClientConfig setFtpConfig() {
		Configuration cfg = Config.getInstance().getConfig();
		FTPClientConfig ftpCfg = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		String lang = cfg.getString(Consts.FTP_CONFIG_KEY_LANGUAGE, Consts.FTP_CONFIG_DEF_LANGUAGE);
		ftpCfg.setServerLanguageCode(lang);
		
		String timeZone = cfg.getString(Consts.FTP_CONFIG_KEY_TIME_ZONE, Consts.FTP_CONFIG_DEF_TIME_ZONE);
		ftpCfg.setServerTimeZoneId(timeZone);

		return ftpCfg;
	}
}
