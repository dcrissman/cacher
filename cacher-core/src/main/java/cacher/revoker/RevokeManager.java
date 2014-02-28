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

package cacher.revoker;

import cacher.Cache;
import cacher.CacheUtils;

/**
 * 
 * 
 * @author Dennis Crissman
 */
public class RevokeManager {

	private final Cache cache;

	public RevokeManager(Cache cache){
		this.cache = cache;
	}

	public void revoke(String key){
		revoke(null, key);
	}

	public void revoke(String group, String key){
		cache.remove(CacheUtils.prefixedKey(group, key));
	}

}
