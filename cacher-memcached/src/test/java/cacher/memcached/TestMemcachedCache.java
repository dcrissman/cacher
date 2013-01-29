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

package cacher.memcached;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMemcachedCache {

	@Test
	public void testEncode(){
		String fakeKey = "He llo &Wo&rld";
		String encodedKey = MemcachedCache.encode(fakeKey);
		assertEquals("He&32llo&32&38Wo&38rld", encodedKey);
	}

	@Test
	public void testDecode(){
		String fakeEncodedKey = "He&32llo&32&38Wo&38rld";
		String decodedKey = MemcachedCache.decode(fakeEncodedKey);
		assertEquals("He llo &Wo&rld", decodedKey);
	}

}
