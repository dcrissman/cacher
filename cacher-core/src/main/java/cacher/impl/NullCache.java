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

package cacher.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cacher.Cache;

/**
 * Essentially a do nothing Cache, but will log messages for each method called. This is useful
 * when caching is disabled for some reason.
 * 
 * @author Dennis Crissman
 */
public class NullCache implements Cache{

	private static final Logger LOGGER = LoggerFactory.getLogger(NullCache.class);

	private void log(){
		LOGGER.warn("Caching currently not enabled.");
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		log();
		LOGGER.info("get: " + key);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		log();
		LOGGER.info("set: " + key + ", " + ((value == null) ? "null" : value.toString()));
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#clear()
	 */
	@Override
	public void clear() {
		log();
		LOGGER.debug("clear");
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		log();
		LOGGER.debug("getBulk: " + keys);
		return new HashMap<String, Object>();
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#remove(java.lang.String)
	 */
	@Override
	public void remove(String key) {
		log();
		LOGGER.info("remove: " + key);
	}

}
