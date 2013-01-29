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

	private static final String ERROR = "Caching not supported.";

	@Override
	public Object get(String key) {
		throw new RuntimeException(ERROR);
	}

	@Override
	public void set(String key, Object value) {
		throw new RuntimeException(ERROR);
	}

	@Override
	public void clear() {
		throw new RuntimeException(ERROR);
	}

	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		throw new RuntimeException(ERROR);
	}

}
