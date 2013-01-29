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

package cacher.fetcher;

/**
 * Implementations of this interface know how to fetch a single data-point
 * from the primary data store.
 * 
 * @author Dennis Crissman
 *
 * @param <T>
 */
public interface FetchSingle<T> {

	/**
	 * @return the type class
	 */
	Class<T> getType();

	/**
	 * Fetches a single data-point from the primary data store.
	 * @param keys - key that need to be fetched.
	 * @return fetched data point.
	 */
	T fetch(String key);

}
