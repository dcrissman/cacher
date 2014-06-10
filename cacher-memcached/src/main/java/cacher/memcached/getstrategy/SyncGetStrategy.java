/*
 * Copyright 2014 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions.
 */

package cacher.memcached.getstrategy;

import java.util.List;
import java.util.Map;

import net.spy.memcached.MemcachedClient;

/**
 * {@link GetStrategy} for querying Memcache synchronously.
 * 
 * @since 1.0.7
 * @author Dennis Crissman
 * @see AsyncGetStrategy
 */
public class SyncGetStrategy implements GetStrategy {

	/*
	 * (non-Javadoc)
	 * @see cacher.memcached.GetStrategy#get(java.lang.String)
	 */
	@Override
	public Object get(MemcachedClient client, String key) {
		if(key == null){
			return null;
		}

		return client.get(key);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.memcached.GetStrategy#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(MemcachedClient client, List<String> keys) {
		if(keys == null || keys.isEmpty()){
			return null;
		}

		return client.getBulk(keys);
	}

}
