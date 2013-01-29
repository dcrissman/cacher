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
 * Simple in-memory cacher, really just a wrapper around a {@link Map}.
 * 
 * @author Dennis Crissman
 */
public class InMemoryCache extends HashMap<String, Object> implements Cache {

	private static final long serialVersionUID = -295001316058726007L;

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCache.class);

	@Override
	public Object get(String key) {
		Object value = super.get(key);
		LOGGER.info("get: " + key + ", return value = " + ((value == null) ? "null" : value.toString()));
		return value;
	}

	@Override
	public void set(String key, Object value) {
		LOGGER.info("set: " + key + ", " + ((value == null) ? "null" : value.toString()));
		super.put(key, value);
	}

	@Override
	public void clear() {
		LOGGER.debug("clear");
		super.clear();
	}

	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		Map<String, Object> values = new HashMap<String, Object>();

		LOGGER.info("getBulk requested: " + keys);

		for(String key : keys){
			values.put(key, super.get(key));
		}

		LOGGER.info("getBulk: " + values);

		return values;
	}

}
