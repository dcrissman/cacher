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

import java.util.List;

import cacher.aop.KeyGenerator;

/**
 * Uses the {@link CacheKey} annotation to identify the parameter to use as the key in the cache.
 * 
 * @author Dennis Crissman
 */
public class SimpleCacheKeyGenerator extends CacheKeyInterpreter implements KeyGenerator {

	@Override
	public String generateKey(final Object[] arguments) {
		return arguments[findKeyPosition()].toString();
	}

	@Override
	public List<String> generateKeys(final Object[] arguments) {
		return convertToList(arguments[findKeyPosition()]);
	}

}
