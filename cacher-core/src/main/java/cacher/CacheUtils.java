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

package cacher;

/**
 * Shared utility methods.
 * 
 * @author Dennis Crissman
 */
public final class CacheUtils {

	/**
	 * Returns the prefixed key value.
	 * @param prefix - String prefix
	 * @param key - String key
	 * @return prefixed key
	 */
	public static String prefixedKey(String prefix, String key){
		if(prefix == null){
			return key;
		}
		return prefix + key;
	}

	private CacheUtils(){}

}
