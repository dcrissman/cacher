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
 * <p>Generates the key(s) that should be used by the {@link cacher.fetcher.FetchManager}.</p>
 * 
 * <p>As it is difficult to determine how the key(s) is passed into the {@link cacher.fetcher.FetchManager}, implementations
 * of this interface tell the {@link CacheInterceptor} how to retrieve the key(s) and ultimately what the cached
 * key should be.</p>
 * 
 * @author Dennis Crissman
 */
public interface KeyGenerator {

	/**
	 * @param arguments - Method arguments passed into called method.
	 * @return single key
	 */
	String generateKey(Object[] arguments);

	/**
	 * @param arguments - Method arguments passed into called method.
	 * @return list of keys
	 */
	List<String> generateKeys(Object[] arguments);

}
