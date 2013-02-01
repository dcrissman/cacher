/*
 * Copyright 2013 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions and must accompany binary
 * distributions with such author attributions.
 */

package cacher.aop;

import java.util.List;

/**
 * <p>For Bulk Fetches, the {@link FetcherMethod} only needs to be called for keys that are not yet cached. Because
 * it would be difficult to know how the full key set was passed into the method, it
 * is equally difficult to remove the previously cached keys from the key set actually passed into the method from
 * the {@link CacheInterceptor}.</p>
 * 
 * <p>This interface tells the {@link CacheInterceptor} how to strip out the already cached keys.</p>
 * 
 * @author Dennis Crissman
 *
 */
public interface KeyCleaner {

	/**
	 * Tells the {@link CacheInterceptor} how to remove keys that were already cached so that the fetching method
	 * can only retrieve values that are yet to be cached.
	 * @param arguments - Arguments as they were passed into the {@link FetcherMethod} before interception.
	 * @param uncachedKeys - List of keys that need to be fetched
	 */
	void clean(Object[] arguments, List<String> uncachedKeys);

}
