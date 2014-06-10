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

/**
 * Listener of {@link FetchManager} events.
 * 
 * @author Dennis Crissman
 *
 */
public interface FetchEventListener {

	/**
	 * Data has been fetched from the cacher
	 * @param keys - keys for which cached data was used.
	 */
	void fetchedFromCache(List<String> keys);

	/**
	 * Data has been fetched using a Fetcher.
	 * @param keys - keys for which Fetcher data was used.
	 */
	void fetchedFromFetcher(List<String> keys);

}
