package com.hp.hpl.deli;

//REVIEWED 10/04/08

/**
 * This class contains constants used in DELI
 */
public class Constants {
		
	public final static String prefix = "ccpp";
	
	public final static String COMPONENT = prefix + "Component";
	
	public final static String ATTRIBUTE = prefix + "Attribute";
	
	public final static String type = prefix + "Type";
	
	public final static String COLLECTIONTYPE = prefix + "CollectionType";
	
	public final static String RESOLUTION = prefix + "Resolution";

	public final static String PROXY_PARAM = "http.proxyHost";

	public final static String PROXY_PORT_PARAM = "http.proxyPort";
	
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
	
	public final static String[][] DATATYPES = { { "Literal", "[A-Za-z0-9/.\\-;:_ ()=*+]+" }, 
			{ "Number", "[0-9]+" },
			{ "Boolean", "(Yes)|(No)" }, 
			{ "Dimension", "[0-9,.]+x[0-9,.]+" } };

	public final static String ALL_KNOWN_UAPROF_PROFILES = "config/profiles.n3";
	
	public final static String CONFIG_FILE = "config/deliConfig.n3";
	
	public final static String VALIDATOR_CONFIG_FILE = "config/deliValidatorConfig.n3";
	
	public final static String DATATYPE_CONFIG_FILE = "config/uaprofValidatorConfig.xml";
	
	public final static String PROPERTIES_OUTPUT_FILE = "build/properties.html";
	
	public final static String ALL_PROFILES_RDF = "build/uaprofData.rdf";
	
	public final static String PROFILE_LIST_OUTPUT = "build/uaprofData.rdf";
	
	public final static String DEFAULT_ATTRIBUTE_TYPE = "Any";
	
	// HP Labs specific

	public final static String PROXY_VALUE = "web-proxy.hpl.hp.com";

	public final static String PROXY_PORT_VALUE = "8088";
	
}
