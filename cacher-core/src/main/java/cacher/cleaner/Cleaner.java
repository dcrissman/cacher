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

package cacher.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cacher.Cache;
import cacher.fetcher.FetchManager;

public class Cleaner {

	private static final Logger LOGGER = LoggerFactory.getLogger(FetchManager.class);

	private final List<CleanerEventListener> cleanerEventListeners;

	private final Cache cache;

	public Cleaner(Cache cache){
		this(cache, null);
	}

	public Cleaner(Cache cache, List<CleanerEventListener> cleanerEventListeners){
		this.cache = cache;
		this.cleanerEventListeners = (cleanerEventListeners == null) ? new ArrayList<CleanerEventListener>() : cleanerEventListeners;
	}

	public Cache getCache(){
		return cache;
	}

	public void clean(String key){
		clean(Arrays.asList(key));
	}

	public void clean(List<String> keys){
		LOGGER.info("Purging cached keys: " + keys.toString());

		List<String> clearedKeys = new ArrayList<>();
		List<String> erroredKeys = new ArrayList<>();

		for(String key : keys){
			try{
				cache.remove(key);
				clearedKeys.add(key);
			}
			catch(RuntimeException e){
				LOGGER.warn("Unable to clear key: " + key, e);
				erroredKeys.add(key);
			}
		}

		if(!clearedKeys.isEmpty()){
			fireClearedFromCacheEvent(clearedKeys);
		}

		if(!erroredKeys.isEmpty()){
			fireErroredEvent(erroredKeys);
		}
	}

	/**
	 * Fires the clearedFromCache events.
	 * @param keys - Keys cleared from the cacher.
	 */
	private void fireClearedFromCacheEvent(List<String> keys){
		if((keys == null) || keys.isEmpty() || (cleanerEventListeners == null)){
			return;
		}

		for(CleanerEventListener listener : cleanerEventListeners){
			try{
				listener.clearedFromCache(keys);
			}
			catch(Exception e){
				LOGGER.error("Exception occurred while handling a 'clearedFromCache' event", e);
			}
		}
	}

	/**
	 * Fires the errored events.
	 * @param keys - Keys errored while clearing from cache.
	 */
	private void fireErroredEvent(List<String> keys){
		if((keys == null) || keys.isEmpty() || (cleanerEventListeners == null)){
			return;
		}

		for(CleanerEventListener listener : cleanerEventListeners){
			try{
				listener.erroredKeys(keys);
			}
			catch(Exception e){
				LOGGER.error("Exception occurred while handling a 'errored' event", e);
			}
		}
	}

}
