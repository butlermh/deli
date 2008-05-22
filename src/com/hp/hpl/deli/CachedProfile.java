package com.hp.hpl.deli;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class wraps the profile class so it can be cached. It does this by
 * including information about the time and date the profile was retrieved from
 * the repository, along with the URL of the profile.
 */
class CachedProfile {
	private static Log log = LogFactory.getLog(CachedProfile.class);

	/** The cached profile. */
	private Profile theProfile;

	/** The date and time the profile was cached. */
	private Date dateAccquired;

	/** The resource associated with the profile. */
	private String profileResource;
	
	/**
	 * Retrieve a profile from a resource such as a URL or a file, convert it
	 * from RDF to the profile data structure and cache it making a note of the
	 * time and date.
	 * 
	 * @param profileResource The resource of the profile.
	 * @return Whether the retrieval was successful or not.
	 */
	protected boolean set(String profileResource) {
		this.profileResource = profileResource;
		this.theProfile = new Profile(profileResource);
		log.info("CachedProfile: Putting profile " + profileResource + " in cache");
		dateAccquired = new Date();
		return true;
	}

	/**
	 * Retrieve the cached profile object.
	 * 
	 * return The cached profile.
	 */
	protected Profile get() {
		Date currentTime = new Date();
		long diffTime = currentTime.getTime() - dateAccquired.getTime();
		if ((Workspace.getInstance().isRefreshStaleProfiles())
				&& (diffTime >= Workspace.getInstance().getMaxCachedProfileLifetime())) {
			Profile keepOldProfile = theProfile;
			log.info("CachedProfile: Updating profile");
			if (!set(profileResource)) {
				// Retrieval failed, keep old profile and reset time
				log.warn("CachedProfile: Profile update failed");
				theProfile = keepOldProfile;
				dateAccquired = currentTime;
			}
		}
		return theProfile;
	}
}
