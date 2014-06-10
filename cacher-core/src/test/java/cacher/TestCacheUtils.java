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

package cacher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestCacheUtils {

	@Test
	public void testPrefixedKey(){
		String prefix = "this";
		String key = "that";

		String prefixedKey = CacheUtils.prefixedKey(prefix, key);

		assertNotNull(prefixedKey);
		assertEquals(prefix + key, prefixedKey);
	}

	@Test
	public void testPrefixedKey_NullPrefix(){
		String prefix = null;
		String key = "that";

		String prefixedKey = CacheUtils.prefixedKey(prefix, key);

		assertNotNull(prefixedKey);
		assertEquals(key, prefixedKey);
	}

}
