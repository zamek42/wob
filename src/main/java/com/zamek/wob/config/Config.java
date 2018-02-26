package com.zamek.wob.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.zamek.wob.Consts;
import com.zamek.wob.util.HasLogger;

/**
 * Configuration for the application
 * 
 * Default name is application.properties
 * 
 * It contains configurable parameters for the application like jdbc paramerters, ftp parameters and csv input and response file names.
 * 
 * This class is a Singleton class.
 * 
 * Usage:
 * <pre>
 * Config.getInstance().getXXX
 * </pre>
 * 
 * The application creates a default config if the application.properties doesn't exists in the current directory. 
 * Default config is:
 * <pre> 
javax.persistence.jdbc.driver=org.hsqldb.jdbcDriver
javax.persistence.jdbc.url=jdbc:hsqldb:mem:testdb
javax.persistence.jdbc.user=sa
javax.persistence.jdbc.password=
eclipselink.ddl-generation=create-tables
eclipselink.logging.level=OFF
input.csvfile=/tmp/input.csv
response.csvfile=/tmp/response.csv

 * </pre> 
 * @author zamek
 *
 */
public class Config implements HasLogger {

	
	private final static String CONFIG_FILE_NAME = "application.properties"; //$NON-NLS-1$
	private final static String KEY_JDBC_DRIVER="javax.persistence.jdbc.driver"; //$NON-NLS-1$
	private final static String DEF_JDBC_DRIVER="org.hsqldb.jdbcDriver"; //$NON-NLS-1$
	
	private final static String KEY_JDBC_URL = "javax.persistence.jdbc.url"; //$NON-NLS-1$
	private final static String DEF_JDBC_URL = "jdbc:hsqldb:mem:testdb"; //$NON-NLS-1$
	
	private final static String KEY_JDBC_USER = "javax.persistence.jdbc.user"; //$NON-NLS-1$
	private final static String DEF_JDBC_USER = "sa"; //$NON-NLS-1$
	
	private final static String KEY_JDBC_PASSWD = "javax.persistence.jdbc.password"; //$NON-NLS-1$ 
	private final static String DEF_JDBC_PASSWORD = ""; //$NON-NLS-1$

	private final static String KEY_JDBC_DDL_GENERATION = "eclipselink.ddl-generation"; //$NON-NLS-1$
	private final static String DEF_JDBC_DDL_GENERATION = "create-tables"; //$NON-NLS-1$
	
	private final static String KEY_JDBC_LOG_LEVEL = "eclipselink.logging.level"; //$NON-NLS-1$
	private final static String DEF_JDBC_LOG_LEVEL = "OFF"; //$NON-NLS-1$
	
	public final static String KEY_INPUT_CSV = "input.csvfile"; //$NON-NLS-1$ 
	public final static String DEF_INPUT_CSV = "/tmp/input.csv"; //$NON-NLS-1$

	public final static String KEY_RESPONSE_CSV = "response.csvfile"; //$NON-NLS-1$ 
	public final static String DEF_RESPONSE_CSV = "/tmp/response.csv"; //$NON-NLS-1$

	private static Config instance=null;
    
	private Configuration config;
    private Map<String, Object> jdbcConfig=null;
    private EntityManager entityManager;
    
	private Config() {
		Configurations configs = new Configurations();
		try {
			Path currentRelativePath = Paths.get("",CONFIG_FILE_NAME); //$NON-NLS-1$
			String s = currentRelativePath.toAbsolutePath().toString();
		    this.config = configs.properties(new File(s));
		}
		catch (ConfigurationException cex) {
			getLogger().error("Configuration error:"+cex.getMessage()); //$NON-NLS-1$
		}	
	}
	
	/**
	 * Get a config instance
	 * 
	 * @return Config
	 */
	public synchronized static Config getInstance() {
		if (instance==null)
			instance = new Config();
		return instance;
	}

	/**
	 * Return the reference with Config (Commons Configuration)
	 * 
	 * @return Configuration
	 */
	public Configuration getConfig() {
		return this.config;
	}

	/**
	 * Returns a Database default configuration. 
	 * 
	 * The following properties coming from application.properties:
	 * <ul>
	 * 	<li>javax.persistence.jdbc.driver</li>
		<li>javax.persistence.jdbc.url</li>
		<li>javax.persistence.jdbc.user</li>
		<li>javax.persistence.jdbc.password</li>
		<li>eclipselink.ddl-generation</li>
		<li>eclipselink.logging.level</li>
	 * </ul>
	 * @return DbConfig Map
	 */
	public Map<String, Object> getDbConfig() {
		if (this.jdbcConfig!=null)
			return this.jdbcConfig;
		
		this.jdbcConfig =  new HashMap<>();
		this.jdbcConfig.put(KEY_JDBC_DRIVER, this.config.getString(KEY_JDBC_DRIVER, DEF_JDBC_DRIVER));
		this.jdbcConfig.put(KEY_JDBC_URL, this.config.getString(KEY_JDBC_URL));
		this.jdbcConfig.put(KEY_JDBC_USER, this.config.getString(KEY_JDBC_USER, DEF_JDBC_USER));
		this.jdbcConfig.put(KEY_JDBC_PASSWD, this.config.getString(KEY_JDBC_PASSWD, DEF_JDBC_PASSWORD));
		this.jdbcConfig.put(KEY_JDBC_DDL_GENERATION, this.config.getString(KEY_JDBC_DDL_GENERATION, DEF_JDBC_DDL_GENERATION));
		this.jdbcConfig.put(KEY_JDBC_LOG_LEVEL, this.config.getString(KEY_JDBC_LOG_LEVEL, DEF_JDBC_LOG_LEVEL));
		
		return this.jdbcConfig;
	}

	/**
	 * Checks exists of application.properties
	 * 
	 * @return true if exists or false if doesn't
	 */
	public static boolean isConfigExists() {
		Path currentRelativePath = Paths.get("", CONFIG_FILE_NAME); //$NON-NLS-1$
		return Files.exists(currentRelativePath.toAbsolutePath()); 
	}
	
	/**
	 * Creates a deault application.properties file
	 * 
	 * @throws ConfigurationException if something went wrong
	 * @throws IOException if something went wrong
	 */
	public static void createDefaultConfigFile() throws ConfigurationException, IOException {
		Parameters params = new Parameters();
		Path currentRelativePath = Paths.get("", CONFIG_FILE_NAME); //$NON-NLS-1$
		String absFile = currentRelativePath.toAbsolutePath().toString();
		File nf = new File(absFile);
		nf.createNewFile();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
			    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
			    .configure(params.fileBased()
			        .setFile(new File(absFile)));
			builder.setAutoSave(true);

		Configuration cfg = builder.getConfiguration();
		cfg.setProperty(KEY_JDBC_DRIVER, DEF_JDBC_DRIVER);
		cfg.setProperty(KEY_JDBC_URL, DEF_JDBC_URL); 
		cfg.setProperty(KEY_JDBC_USER, DEF_JDBC_USER);		
		cfg.setProperty(KEY_JDBC_PASSWD, DEF_JDBC_PASSWORD);
		cfg.setProperty(KEY_JDBC_DDL_GENERATION, DEF_JDBC_DDL_GENERATION);
		cfg.setProperty(KEY_JDBC_LOG_LEVEL, DEF_JDBC_LOG_LEVEL);
		cfg.setProperty(KEY_INPUT_CSV, DEF_INPUT_CSV);
		cfg.setProperty(KEY_RESPONSE_CSV, DEF_RESPONSE_CSV);
		cfg.setProperty(Consts.FTP_CONFIG_KEY_HOST, "???"); //$NON-NLS-1$
		cfg.setProperty(Consts.FTP_CONFIG_KEY_LANGUAGE,Consts.FTP_CONFIG_DEF_LANGUAGE);
		cfg.setProperty(Consts.FTP_CONFIG_KEY_TIME_ZONE, Consts.FTP_CONFIG_DEF_TIME_ZONE);
	}
	
	/**
	 * Get the name of the input file from application.properties 
	 * 
	 * Key is input.csvfile 
	 * 
	 * @return the name of the input file or default value if it doesn't exists. Default value is /tmp/input.csv
	 */
	public String getInputFileName() {
		return this.config.getString(KEY_INPUT_CSV, DEF_INPUT_CSV);
	}
	
	/**
	 * Get the name of the response file from application.properties 
	 * 
	 * Key is response.csvfile 
	 * 
	 * @return the name of the response file or default value if it doesn't exists. Default value is /tmp/response.csv
	 */
	public String getResponseFileName() {
		return this.config.getString(KEY_RESPONSE_CSV, DEF_RESPONSE_CSV);
	}
	
	/**
	 * Get the Entity Manager
	 * 
	 * @return the preinitialized Entity manager
	 */
	public EntityManager getEntityManager() {
		if (this.entityManager == null) {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(Consts.PERSISTENCE_NAME, getDbConfig());
			this.entityManager = emf.createEntityManager();
		}
		return this.entityManager;
	}
}
