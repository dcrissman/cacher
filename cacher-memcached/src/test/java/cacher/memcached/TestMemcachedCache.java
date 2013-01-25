package cacher.memcached;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cacher.memcached.MemcachedCache;

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
