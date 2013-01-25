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

	private final static Logger logger = LoggerFactory.getLogger(InMemoryCache.class);

	@Override
	public Object get(String key) {
		Object value = super.get(key);
		logger.info("get: " + key + ", return value = " + ((value == null) ? "null" : value.toString()));
		return value;
	}

	@Override
	public void set(String key, Object value) {
		logger.info("set: " + key + ", " + ((value == null) ? "null" : value.toString()));
		super.put(key, value);
	}

	@Override
	public void clear() {
		logger.debug("clear");
		super.clear();
	}

	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		Map<String, Object> values = new HashMap<String, Object>();

		logger.info("getBulk requested: " + keys);

		for(String key : keys){
			values.put(key, super.get(key));
		}

		logger.info("getBulk: " + values);

		return values;
	}

}
