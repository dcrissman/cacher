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

package cacher.fetcher.inject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import cacher.Cache;
import cacher.fetcher.FetchEventListener;
import cacher.fetcher.FetchManager;

/**
 * Eases integration with frameworks that supports the javax.inject api.
 * 
 * @author Dennis Crissman
 *
 * @param <T>
 */
public class FetchManagerProvider implements Provider<FetchManager>{

	private final List<FetchEventListener> fetchEventListeners;
	private final Cache cache;

	public FetchManagerProvider(Cache cache){
		this(cache, null);
	}

	@Inject
	public FetchManagerProvider(Cache cache, List<FetchEventListener> fetchEventListeners){
		this.cache = cache;
		this.fetchEventListeners = fetchEventListeners;
	}

	@Override
	public FetchManager get() {
		return new FetchManager(cache, fetchEventListeners);
	}

}
