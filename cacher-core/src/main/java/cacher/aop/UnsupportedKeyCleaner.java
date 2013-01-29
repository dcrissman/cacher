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

package cacher.aop;

import java.util.List;

/**
 * Default value used in {@link FetcherMethod#keyCleaner()} if no other value is provided.
 * 
 * @author Dennis Crissman
 *
 */
public class UnsupportedKeyCleaner implements KeyCleaner {

	@Override
	public void clean(Object[] arguments, List<String> uncachedKeys) {
		throw new UnsupportedOperationException("An implementation of KeyCleaner must be provided for Bulk fetches.");
	}

}
