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
 * Simple in-memory cacher, really just a wrapper around a {@link Map}.
 * 
 * @author Dennis Crissman
 */
public class InMemoryCache extends HashMap<String, Object> implements Cache {

	private static final long serialVersionUID = -295001316058726007L;

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCache.class);

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		Object value = super.get(key);
		LOGGER.debug("get: " + key + ", return value = " + ((value == null) ? "null" : value.toString()));
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		LOGGER.debug("set: " + key + ", " + ((value == null) ? "null" : value.toString()));
		super.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.HashMap#clear()
	 */
	@Override
	public void clear() {
		LOGGER.debug("clear");
		super.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		Map<String, Object> values = new HashMap<String, Object>();

		LOGGER.debug("getBulk requested: " + keys);

		for(String key : keys){
			values.put(key, super.get(key));
		}

		LOGGER.debug("getBulk: " + values);

		return values;
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#remove(java.lang.String)
	 */
	@Override
	public void remove(String key) {
		LOGGER.debug("remove: " + key);
		super.remove(key);
	}

}
