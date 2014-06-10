/*
 * Copyright 2013 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions.
 */

package cacher.aop.impl.simple;

import java.util.List;

import cacher.aop.KeyCleaner;

/**
 * <p>Uses the {@link CacheKey} annotation to identify the parameter to use as the key in the cache.</p>
 * <p>As only bulk fetches use {@link KeyCleaner}s, the annotated parameter needs to be a {@link java.util.Collection}.</p>
 * 
 * @author Dennis Crissman
 */
public class SimpleCacheKeyCleaner extends CacheKeyInterpreter implements KeyCleaner {

	/*
	 * (non-Javadoc)
	 * @see cacher.aop.KeyCleaner#clean(java.lang.Object[], java.util.List)
	 */
	@Override
	public void clean(final Object[] arguments, final List<String> uncachedKeys) {
		int keyPosition = findKeyPosition();
		arguments[keyPosition] = uncachedKeys;
	}

}
