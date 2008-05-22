package com.hp.hpl.deli;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides a way of caching profiles. When it retrieves profile,
 * it parses it and caches the parsed profile. It indexes these cached
 * profiles using the profile URL. This speeds up processing considerably.
 */
public class ProfileCache {
	private static Log log = LogFactory.getLog(ProfileCache.class);
	
	private Map<String, CachedProfile> profileCache;
	
	boolean cacheReferenceProfiles ;
	
	int maxCacheSize;

	/**
	 * The constructor creates the map used to resolve profile URLs onto
	 * cached profile objects.
	 * 
	 */
	public ProfileCache(boolean cacheReferenceProfiles, boolean refreshStaleProfiles,
			long maxCachedProfileLifetime, int maxCacheSize) {
		profileCache = new HashMap<String, CachedProfile>(maxCacheSize * 2);
		this.cacheReferenceProfiles = cacheReferenceProfiles;
		this.maxCacheSize = maxCacheSize;
	}

	/**
	 * This method retrieves a profile. It checks first to see if the
	 * profile is already in the cache. If it is then it returns the cached
	 * version. If not it checks to see if the cache is full. If it is it
	 * removes one profile from the cache. It then retrieves the profile and
	 * stores it in the cache.
	 * 
	 * @param profileURL
	 *            The URL of the profile to be retrieved.
	 * @return The resolved profile.
	 */
	public Profile get(String profileURL) {
		CachedProfile theCachedProfile;

		log.info("ProfileCache: Retrieving profile");
		if (cacheReferenceProfiles) {
			if (profileCache.containsKey(profileURL)) {
				log.info("ProfileCache: Retrieving cached profile");
				theCachedProfile = (CachedProfile) profileCache.get(profileURL);
			} else {
				if (profileCache.size() > maxCacheSize) {
					synchronized (this) {
						int profileToRemove = (new Random()).nextInt(maxCacheSize);
						Object[] theArray = profileCache.keySet().toArray();
						profileCache.remove(theArray[profileToRemove]);
					}
				}

				theCachedProfile = new CachedProfile();

				boolean result;
				result = theCachedProfile.set(profileURL);

				if (!result) {
					log.info("ProfileCache: Unable to retrieve the profile");

					return null;
				}

				synchronized (this) {
					profileCache.put(profileURL, theCachedProfile);
				}
			}

			return theCachedProfile.get();
		}
		return (new Profile(profileURL));
	}
}
