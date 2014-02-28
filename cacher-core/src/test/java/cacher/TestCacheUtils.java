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
