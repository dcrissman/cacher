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

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		throw new CachingNotSupported();
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		throw new CachingNotSupported();
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#clear()
	 */
	@Override
	public void clear() {
		throw new CachingNotSupported();
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		throw new CachingNotSupported();
	}

	/**
	 * Exception is thrown by {@link ExceptionThrowingCache} to indicate that caching is not actually supported.
	 * 
	 * @author Dennis Crissman
	 */
	public static class CachingNotSupported extends RuntimeException{

		private static final long serialVersionUID = 7805563875629720920L;

		public CachingNotSupported(){
			super("Caching is not supported.");
		}

	}

}
