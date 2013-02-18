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

package cacher.aop.impl.simple;

import java.util.Collection;
import java.util.List;

import cacher.aop.KeyCleaner;

/**
 * <p>Uses the {@link CacheKey} annotation to identify the parameter to use as the key in the cache.</p>
 * <p>As only bulk fetches use {@link KeyCleaner}s, the annotated parameter needs to be a {@link Collection}.</p>
 * 
 * @author Dennis Crissman
 */
public class SimpleCacheKeyCleaner extends CacheKeyInterpreter implements KeyCleaner {

	@Override
	public void clean(final Object[] arguments, final List<String> uncachedKeys) {
		int keyPosition = findKeyPosition();
		arguments[keyPosition] = uncachedKeys;
	}

}
