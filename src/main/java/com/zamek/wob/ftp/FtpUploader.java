package com.zamek.wob.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import com.zamek.wob.Consts;
import com.zamek.wob.config.Config;
import com.zamek.wob.util.HasLogger;

public class FtpUploader implements HasLogger {

	private final static boolean DEBUG = true;
	
	private String fileName;
	private String hostUrl;
	
	public FtpUploader(String fileName) {
		this.fileName = fileName;
		this.hostUrl = Config.getInstance().getConfig().getString(Consts.FTP_CONFIG_KEY_HOST);
	}
	
	public void upload() throws Exception {
		Path path = Paths.get(this.fileName);
		if (Files.notExists(path)) 
			throw new IOException(String.format("%s file is not exists", this.fileName)); //$NON-NLS-1$
		
		FTPClient ftp = new FTPClient();
		
		FTPClientConfig config = setFtpConfig();
		ftp.configure(config );
		try {
		      int reply;
		      ftp.connect(this.hostUrl);
		      if (DEBUG) 
		    	  getLogger().debug(String.format("Connected to %s, reply:%s", this.hostUrl, ftp.getReplyString())); //$NON-NLS-1$

		      reply = ftp.getReplyCode();

		      if(!FTPReply.isPositiveCompletion(reply)) {
		    	  ftp.disconnect();
		    	  throw new IOException(String.format("Ftp Server at %s said %s", this.hostUrl, Integer.valueOf(reply))); //$NON-NLS-1$
		      }
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
