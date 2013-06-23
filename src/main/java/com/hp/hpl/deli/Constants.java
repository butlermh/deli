package com.hp.hpl.deli;

/**
 * Constants used in DELI
 */
public class Constants {
		
	private static final String PREFIX = "ccpp";
	
	public static final String COMPONENT = PREFIX + "Component";
	
	public static final String ATTRIBUTE = PREFIX + "Attribute";
	
	public static final String TYPE = PREFIX + "Type";
	
	public static final String COLLECTIONTYPE = PREFIX + "CollectionType";
	
	public static final String RESOLUTION = PREFIX + "Resolution";

	public static final String PROXY_PARAM = "http.proxyHost";

	public static final String PROXY_PORT_PARAM = "http.proxyPort";
	
	public static final String LOCKED = "Locked";
	
	public static final String OVERRIDE = "Override";
	
	public static final String APPEND = "Append";
	
	public static final String SIMPLE = "Simple";
	
	public static final String LITERAL = "Literal";
	
	public static final String DIMENSION = "Dimension";
	
	public static final String BOOLEAN = "Boolean";
	
	public static final String BAG = "Bag";
	
	public static final String SEQ = "Seq";
	
	public static final String NUMBER = "Number";
	
	public static final String[][] DATATYPES = { { "Literal", "[A-Za-z0-9/.\\-;:_ ()=*+]+" }, 
			{ "Number", "[0-9]+" },
			{ "Boolean", "(Yes)|(No)" }, 
			{ "Dimension", "[0-9,.]+x[0-9,.]+" } };
	
	public static final String FS = System.getProperty("file.separator"); 
	
	public static final String CONFIG_DIR = "config"; 
	
	public static final String CONFIG_PATH = CONFIG_DIR + FS;

	public static final String CONFIG_FILE = CONFIG_PATH + "deliConfig.n3";

	public static final String DATATYPE_CONFIG_FILE = CONFIG_PATH + "uaprofValidatorConfig.xml";
	
	public static final String BUILD_DIR ="target";
	
	public static final String BUILD_PATH = BUILD_DIR + FS;
	
	public static final String DEFAULT_ATTRIBUTE_TYPE = "Any";
	
	public static final String ALL_KNOWN_UAPROF_PROFILES_OUTPUT = BUILD_PATH + "profiles.n3";
	
	public static final String VALIDATOR_CONFIG_FILE = CONFIG_PATH + "deliValidatorConfig.n3";
	
    public static final String PROFILES_OUTPUT_FILE = BUILD_PATH + "profiles.html";
    
    public static final String PROPERTIES_OUTPUT_FILE = BUILD_PATH + "properties.html";
	
	public static final String ALL_PROFILES_RDF = BUILD_PATH + "uaprofData.rdf";
	
	public static final String PROFILE_LIST_OUTPUT = BUILD_PATH + "uaprofData.rdf";
	
	public static final String LOCAL_PROFILES_FILE = BUILD_PATH + "profiles.n3";
}
