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

package cacher.memcached;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.spy.memcached.MemcachedClient;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMemcachedCache {

	private final MemcachedClient client = EasyMock.createMock(MemcachedClient.class);

	@Before
	public void init(){
		reset(client);
		replay(client);
	}

	@After
	public void after(){
		verify(client);
	}

	private MemcachedCache createCache(){
		return createCache(1);
	}

	private MemcachedCache createCache(int timeout){
		return new MemcachedCache(client, timeout);
	}

	@Test
	public void testGet(){
		String key = "my&key";
		Object value = new Object();

		reset(client);
		expect(client.get(MemcachedCache.encode(key))).andReturn(value).once();
		replay(client);

		MemcachedCache cache = createCache();
		assertEquals(value, cache.get(key));
	}

	@Test
	public void testGetBulk(){
		String key1 = "my&key";
		String key2 = "my other key";
		Map<String, Object> value = new HashMap<String, Object>();

		reset(client);
		expect(client.getBulk(
				Arrays.asList(MemcachedCache.encode(key1), MemcachedCache.encode(key2)))
				).andReturn(value).once();
		replay(client);

		MemcachedCache cache = createCache();
		assertEquals(value, cache.getBulk(Arrays.asList(key1, key2)));
	}

	@Test
	public void testSet(){
		String key = "my&key";
		Object value = new Object();
		int timeout = 5;

		reset(client);
		expect(client.set(
				MemcachedCache.encode(key), timeout, value)
				).andReturn(null).once();
		replay(client);

		MemcachedCache cache = createCache(timeout);
		cache.set(key, value);
	}

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
