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

package cacher.fetcher;

import java.util.List;
import java.util.Map;

/**
 * Implementations of this interface (aka. Bulk Fetch) know how to fetch multiple data-points at once
 * from the primary data store.
 * 
 * @author Dennis Crissman
 *
 * @param <T>
 */
public interface FetchMultiple<T> {

	/**
	 * @return the type class
	 */
	Class<T> getType();

	/**
	 * Fetches multiple data-points from the primary data store.
	 * @param keys - keys that need to be fetched.
	 * @return fetched data points.
	 */
	Map<String, T> fetch(List<String> keys);

}
