/* CVS $Id: $ */
package com.hp.hpl.deli; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from config/deliSchema.n3 
 * @author Auto-generated by schemagen on 20 Apr 2011 12:23 
 */
public class DeliSchema {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://delicon.sourceforge.net/schema#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>the alias URI of a namespace</p> */
    public static final Property aliasUri = m_model.createProperty( "http://delicon.sourceforge.net/schema#aliasUri" );
    
    /** <p>cache reference profiles</p> */
    public static final Property cacheReferenceProfiles = m_model.createProperty( "http://delicon.sourceforge.net/schema#cacheReferenceProfiles" );
    
    /** <p>the property to use for components</p> */
    public static final Property componentProperty = m_model.createProperty( "http://delicon.sourceforge.net/schema#componentProperty" );
    
    /** <p>file containing datatype configuration</p> */
    public static final Property datatypeConfigFile = m_model.createProperty( "http://delicon.sourceforge.net/schema#datatypeConfigFile" );
    
    /** <p>the file containing the datatype file</p> */
    public static final Property datatypeFile = m_model.createProperty( "http://delicon.sourceforge.net/schema#datatypeFile" );
    
    /** <p>the datatype uri</p> */
    public static final Property datatypeUri = m_model.createProperty( "http://delicon.sourceforge.net/schema#datatypeUri" );
    
    /** <p>whether to use datatype validation or not</p> */
    public static final Property datatypeValidationOn = m_model.createProperty( "http://delicon.sourceforge.net/schema#datatypeValidationOn" );
    
    /** <p>debug request headers</p> */
    public static final Property debugRequestHeaders = m_model.createProperty( "http://delicon.sourceforge.net/schema#debugRequestHeaders" );
    
    /** <p>The name of the device</p> */
    public static final Property deviceName = m_model.createProperty( "http://delicon.sourceforge.net/schema#deviceName" );
    
    /** <p>A file containing the profile</p> */
    public static final Property file = m_model.createProperty( "http://delicon.sourceforge.net/schema#file" );
    
    /** <p>the file containing details of local profiles used to override devices with 
     *  no profiles</p>
     */
    public static final Property localProfilesFile = m_model.createProperty( "http://delicon.sourceforge.net/schema#localProfilesFile" );
    
    /** <p>the path to the local profiles</p> */
    public static final Property localProfilesPath = m_model.createProperty( "http://delicon.sourceforge.net/schema#localProfilesPath" );
    
    /** <p>The manufacturer who created the device described by the profile</p> */
    public static final Property manufacturedBy = m_model.createProperty( "http://delicon.sourceforge.net/schema#manufacturedBy" );
    
    /** <p>the maximum cache size</p> */
    public static final Property maxCacheSize = m_model.createProperty( "http://delicon.sourceforge.net/schema#maxCacheSize" );
    
    /** <p>the maximum lifetime for a profile in the cache</p> */
    public static final Property maxCachedProfileLifetime = m_model.createProperty( "http://delicon.sourceforge.net/schema#maxCachedProfileLifetime" );
    
    /** <p>normalize whitespaces in the profile diff</p> */
    public static final Property normaliseWhitespaceInProfileDiff = m_model.createProperty( "http://delicon.sourceforge.net/schema#normaliseWhitespaceInProfileDiff" );
    
    /** <p>Prefer local profiles over remote profiles</p> */
    public static final Property preferLocalOverRemoteProfiles = m_model.createProperty( "http://delicon.sourceforge.net/schema#preferLocalOverRemoteProfiles" );
    
    /** <p>process unconfigured namespaces</p> */
    public static final Property processUnconfiguredNamespaces = m_model.createProperty( "http://delicon.sourceforge.net/schema#processUnconfiguredNamespaces" );
    
    /** <p>whether to process properties that have not been defined in a schema</p> */
    public static final Property processUndefinedAttributes = m_model.createProperty( "http://delicon.sourceforge.net/schema#processUndefinedAttributes" );
    
    /** <p>Profile Diff Digest Verification</p> */
    public static final Property profileDiffDigestVerification = m_model.createProperty( "http://delicon.sourceforge.net/schema#profileDiffDigestVerification" );
    
    /** <p>Profile supplied by vendor</p> */
    public static final Property profileSuppliedByVendor = m_model.createProperty( "http://delicon.sourceforge.net/schema#profileSuppliedByVendor" );
    
    /** <p>The organization who provided the profile</p> */
    public static final Property provider = m_model.createProperty( "http://delicon.sourceforge.net/schema#provider" );
    
    /** <p>the RDF schema URI</p> */
    public static final Property rdfsUri = m_model.createProperty( "http://delicon.sourceforge.net/schema#rdfsUri" );
    
    /** <p>Refresh stale profiles</p> */
    public static final Property refreshStaleProfiles = m_model.createProperty( "http://delicon.sourceforge.net/schema#refreshStaleProfiles" );
    
    /** <p>The release of this profile</p> */
    public static final Property release = m_model.createProperty( "http://delicon.sourceforge.net/schema#release" );
    
    /** <p>the schema vocabulary file</p> */
    public static final Property schemaVocabularyFile = m_model.createProperty( "http://delicon.sourceforge.net/schema#schemaVocabularyFile" );
    
    /** <p>Does the device support UAProf?</p> */
    public static final Property supportsUAProf = m_model.createProperty( "http://delicon.sourceforge.net/schema#supportsUAProf" );
    
    /** <p>The URI of the UAProf profile</p> */
    public static final Property uaprofUri = m_model.createProperty( "http://delicon.sourceforge.net/schema#uaprofUri" );
    
    /** <p>the URI of a namespace</p> */
    public static final Property uri = m_model.createProperty( "http://delicon.sourceforge.net/schema#uri" );
    
    /** <p>use local profiles if no CC/PP information in request</p> */
    public static final Property useLocalProfilesIfNoCCPP = m_model.createProperty( "http://delicon.sourceforge.net/schema#useLocalProfilesIfNoCCPP" );
    
    /** <p>The useragent of the device</p> */
    public static final Property useragent = m_model.createProperty( "http://delicon.sourceforge.net/schema#useragent" );
    
    public static final Resource Manufacturer = m_model.createResource( "http://delicon.sourceforge.net/schema#Manufacturer" );
    
    /** <p>A namespace definition</p> */
    public static final Resource NamespaceDefinition = m_model.createResource( "http://delicon.sourceforge.net/schema#NamespaceDefinition" );
    
    public static final Resource Profile = m_model.createResource( "http://delicon.sourceforge.net/schema#Profile" );
    
}
