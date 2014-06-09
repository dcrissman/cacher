/*
 * Copyright 2014 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions and must accompany binary
 * distributions with such author attributions.
 */

package cacher.memcached;

import java.util.List;
import java.util.Map;

import net.spy.memcached.MemcachedClient;

/**
 * Abstracts the strategy for how cached values are retrieved from Memcache.
 * 
 * @author Dennis Crissman
 */
public interface GetStrategy {

	/**
	 * Gets a cached value
	 * @param client - {@link MemcachedClient}
	 * @param key - String key
	 * @return Object value
	 */
	Object get(MemcachedClient client, String key);

	/**
	 * Retrieves multiple cached values at once.
	 * @param client - {@link MemcachedClient}
	 * @param keys - List of keys to fetch
	 * @return Map of fetched key/value pairs.
	 */
	Map<String, Object> getBulk(MemcachedClient client, List<String> keys);

}
