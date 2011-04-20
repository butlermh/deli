package com.hp.hpl.deli;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A cached profile, including the date acquired and the URL of the profile. 
 */
class CachedProfile {
	private static Log log = LogFactory.getLog(CachedProfile.class);

	/** The cached profile. */
	private Profile cachedProfile;

	/** The date and time the profile was cached. */
	private Date dateTimeAcquired;

	/** The resource associated with the profile. */
	private String profileURI;
	
	private ProfileProcessor configuration;
	
	CachedProfile(ProfileProcessor configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * Retrieve a profile from a resource such as a URL or a file, convert it
	 * from RDF to the profile data structure and cache it making a note of the
	 * time and date.
	 * 
	 * @param profileUri The resource of the profile.
	 * @return Whether the retrieval was successful or not.
	 */
	boolean set(final String profileUri) throws IOException {
		this.profileURI = profileUri;
		this.cachedProfile = configuration.processProfile(profileUri);
		this.dateTimeAcquired = new Date();
		log.info("CachedProfile: Putting profile " + profileUri + " in cache");
		return true;
	}

	/**
	 * Retrieve the cached profile object.
	 * 
	 * return The cached profile.
	 */
	Profile get(boolean refreshStaleProfiles, long maxCachedProfileLifetime) throws IOException {
		Date currentTime = new Date();
		long diffTime = currentTime.getTime() - dateTimeAcquired.getTime();
		if ((refreshStaleProfiles)
				&& (diffTime >= maxCachedProfileLifetime)) {
			Profile oldProfile = cachedProfile;
			log.info("CachedProfile: Updating profile");
			if (!set(profileURI)) {
				// Retrieval failed, keep old profile and reset time
				log.warn("CachedProfile: Profile update failed");
				cachedProfile = oldProfile;
				dateTimeAcquired = currentTime;
			}
		}
		return cachedProfile;
	}
}
