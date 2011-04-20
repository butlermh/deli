package com.hp.hpl.deli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class provides a way of caching profiles. When it retrieves profile, it
 * parses it and caches the parsed profile. It indexes these cached profiles
 * using the profile URL. This speeds up processing considerably.
 */
class ProfileCache {
	/** Constant for converting milliseconds in to hours */
	private static final long HOURINMILLISECONDS = 1000 * 60 * 60;

	private static Log log = LogFactory.getLog(ProfileCache.class);

	private Map<String, CachedProfile> profileCache;

	/** Turn reference profile caching on or off. */
	private boolean cacheReferenceProfiles = true;

	/** The maximum number of profiles in the profile cache. */
	private int maxCacheSize = 100;

	/** Whether to refresh cached profiles after maximum lifetime has expired. */
	private boolean refreshStaleProfiles = false;

	/** Maximum lifetime of a cached profile in hours. */
	private long maxCachedProfileLifetime = 24 * HOURINMILLISECONDS;

	private ProfileProcessor configuration;
	/**
	 * The constructor creates the map used to resolve profile URLs onto cached
	 * profile objects.
	 * 
	 */
	ProfileCache(ProfileProcessor configuration) {
		this.configuration = configuration;
		DeliConfiguration workspace = configuration.getWorkspace();
		profileCache = new HashMap<String, CachedProfile>(maxCacheSize * 2);
		cacheReferenceProfiles = workspace.get(DeliSchema.cacheReferenceProfiles,
				cacheReferenceProfiles);
		maxCacheSize = workspace.get(DeliSchema.maxCacheSize, maxCacheSize);
		refreshStaleProfiles = workspace.get(DeliSchema.refreshStaleProfiles,
				refreshStaleProfiles);
		maxCachedProfileLifetime = workspace.get(DeliSchema.maxCachedProfileLifetime,
				maxCachedProfileLifetime);
	}

	/**
	 * This method retrieves a profile. It checks first to see if the profile is
	 * already in the cache. If it is then it returns the cached version. If not
	 * it checks to see if the cache is full. If it is it removes one profile
	 * from the cache. It then retrieves the profile and stores it in the cache.
	 * 
	 * @param profileURL The URL of the profile to be retrieved.
	 * @return The resolved profile.
	 */
	Profile get(HttpProfileProcessor profileProcessor, String profileURL) throws IOException {
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

				theCachedProfile = new CachedProfile(configuration);

				boolean result;
				result = theCachedProfile.set(profileURL);

				if (!result) {
					log.info("ProfileCache: Unable to retrieve the profile");
				}

				synchronized (this) {
					profileCache.put(profileURL, theCachedProfile);
				}
			}

			return theCachedProfile.get(refreshStaleProfiles, maxCachedProfileLifetime);
		}
		return new ProcessProfile(configuration, false, profileURL).getProfile();
	}
}
