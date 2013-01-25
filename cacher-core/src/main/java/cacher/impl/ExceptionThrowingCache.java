package cacher.impl;

import java.util.List;
import java.util.Map;

import cacher.Cache;

/**
 * Implementation of {@link Cache} to ensure that caching is not being used. This
 * is only useful for testing code when caching is disabled.
 * 
 * @author Dennis Crissman
 */
public class ExceptionThrowingCache implements Cache {

	@Override
	public Object get(String key) {
		throw new RuntimeException("Caching not supported.");
	}

	@Override
	public void set(String key, Object value) {
		throw new RuntimeException("Caching not supported.");
	}

	@Override
	public void clear() {
		throw new RuntimeException("Caching not supported.");
	}

	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		throw new RuntimeException("Caching not supported.");
	}

}
