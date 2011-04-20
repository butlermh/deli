package com.hp.hpl.deli;

/**
 * Constants used in DELI
 */
public class Constants {
		
	private final static String PREFIX = "ccpp";
	
	public final static String COMPONENT = PREFIX + "Component";
	
	final static String ATTRIBUTE = PREFIX + "Attribute";
	
	public final static String TYPE = PREFIX + "Type";
	
	public final static String COLLECTIONTYPE = PREFIX + "CollectionType";
	
	final static String RESOLUTION = PREFIX + "Resolution";

	final static String PROXY_PARAM = "http.proxyHost";

	final static String PROXY_PORT_PARAM = "http.proxyPort";
	
	public final static String LOCKED = "Locked";
	
	public final static String OVERRIDE = "Override";
	
	public final static String APPEND = "Append";
	
	public final static String SIMPLE = "Simple";
	
	public final static String LITERAL = "Literal";
	
	public final static String DIMENSION = "Dimension";
	
	public final static String BOOLEAN = "Boolean";
	
	public final static String BAG = "Bag";
	
	public final static String SEQ = "Seq";
	
	public final static String NUMBER = "Number";
	
	final static String[][] DATATYPES = { { "Literal", "[A-Za-z0-9/.\\-;:_ ()=*+]+" }, 
			{ "Number", "[0-9]+" },
			{ "Boolean", "(Yes)|(No)" }, 
			{ "Dimension", "[0-9,.]+x[0-9,.]+" } };
	
	final static String FS = System.getProperty("file.separator"); 
	
	final static String CONFIG_DIR = "config"; 
	
	final static String CONFIG_PATH = CONFIG_DIR + FS;

	public final static String CONFIG_FILE = CONFIG_PATH + "deliConfig.n3";

	final static String DATATYPE_CONFIG_FILE = CONFIG_PATH + "uaprofValidatorConfig.xml";
	
	final static String BUILD_DIR ="target";
	
	final static String BUILD_PATH = BUILD_DIR + FS;
	
	final static String DEFAULT_ATTRIBUTE_TYPE = "Any";
	
	public final static String ALL_KNOWN_UAPROF_PROFILES = CONFIG_PATH + "profiles.n3";
	
	public final static String VALIDATOR_CONFIG_FILE = CONFIG_PATH + "deliValidatorConfig.n3";
	
    public final static String PROPERTIES_OUTPUT_FILE = BUILD_PATH + "properties.html";
	
	public final static String ALL_PROFILES_RDF = BUILD_PATH + "uaprofData.rdf";
	
	public final static String PROFILE_LIST_OUTPUT = BUILD_PATH + "uaprofData.rdf";
	
	public final static String LOCAL_PROFILES_FILE = BUILD_PATH + "profiles.n3";
}
