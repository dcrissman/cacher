/*
 * Copyright 2014 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions.
 */

package cacher.cleaner.inject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import cacher.Cache;
import cacher.cleaner.Cleaner;
import cacher.cleaner.CleanerEventListener;

public class CleanerProvider implements Provider<Cleaner>{

	private final List<CleanerEventListener> cleanerEventListeners;
	private final Cache cache;

	public CleanerProvider(Cache cache){
		this(cache, null);
	}

	@Inject
	public CleanerProvider(Cache cache, List<CleanerEventListener> cleanerEventListeners){
		this.cache = cache;
		this.cleanerEventListeners = cleanerEventListeners;
	}

	@Override
	public Cleaner get() {
		return new Cleaner(cache, cleanerEventListeners);
	}

}
