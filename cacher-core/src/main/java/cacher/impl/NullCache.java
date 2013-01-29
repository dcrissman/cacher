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

package cacher.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cacher.Cache;

/**
 * Essentially a do nothing CacheResult, but will log error messages for each method called. This is useful
 * when caching is disabled for some reason.
 * 
 * @author Dennis Crissman
 */
public class NullCache implements Cache{

	private static final Logger LOGGER = LoggerFactory.getLogger(NullCache.class);

	private void log(){
		LOGGER.error("Caching currently not enabled, please see earlier error for details.");
	}

	@Override
	public Object get(String key) {
		log();
		LOGGER.info("get: " + key);
		return null;
	}

	@Override
	public void set(String key, Object value) {
		log();
		LOGGER.info("set: " + key + ", " + ((value == null) ? "null" : value.toString()));
	}

	@Override
	public void clear() {
		log();
		LOGGER.debug("clear");
	}

	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		log();
		LOGGER.debug("getBulk: " + keys);
		return new HashMap<String, Object>();
	}

}
