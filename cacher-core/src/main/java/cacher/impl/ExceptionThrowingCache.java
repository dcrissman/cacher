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

	private static final String CACHING_IS_NOT_SUPPORTED = "Caching is not supported.";

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		throw new UnsupportedOperationException(CACHING_IS_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		throw new UnsupportedOperationException(CACHING_IS_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#clear()
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException(CACHING_IS_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		throw new UnsupportedOperationException(CACHING_IS_NOT_SUPPORTED);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#remove(java.lang.String)
	 */
	@Override
	public void remove(String key) {
		throw new UnsupportedOperationException(CACHING_IS_NOT_SUPPORTED);
	}

}
