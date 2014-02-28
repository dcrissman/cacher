/*
 * Copyright 2014 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions and must accompany binary
 * distributions with such author attributions.
 */

package cacher.revoker.inject;

import javax.inject.Inject;
import javax.inject.Provider;

import cacher.Cache;
import cacher.revoker.RevokeManager;

/**
 * <p>Eases integration with frameworks that supports the javax.inject api.</p>
 * <p>Provides an instance of {@link RevokeManager}.</p>
 * 
 * @author Dennis Crissman
 */
public class RevokeManagerProvider implements Provider<RevokeManager> {

	private final Cache cache;

	@Inject
	public RevokeManagerProvider(Cache cache){
		this.cache = cache;
	}

	@Override
	public RevokeManager get() {
		return new RevokeManager(cache);
	}

}
