package cacher.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import testframework.util.GuiceRunner;
import testframework.util.GuiceRunner.UseModules;
import cacher.fetcher.FetchManager;
import cacher.impl.InMemoryCache;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

@RunWith(GuiceRunner.class)
@UseModules(TestCacheInterceptor_Multiple.class)
public class TestCacheInterceptor_Multiple implements Module{

	@Inject
	private InMemoryCache cache;

	@Inject
	private TestHelper helper;

	@Override
	public void configure(Binder binder) {
		InMemoryCache cnfConfig = new InMemoryCache();
		binder.bind(InMemoryCache.class).toInstance(cnfConfig);

		CacheInterceptor interceptor = new CacheInterceptor(new FetchManager(cnfConfig));
		binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(FetcherMethod.class), interceptor);

		binder.bind(TestHelper.class);
	}

	@Before
	public void init(){
		cache.clear();
	}

	@Test
	public void testMulti_ValuesCached(){
		assertTrue(cache.isEmpty());

		Map<String, Object> map = helper.getColors(TestHelper.KEY_BLUE, TestHelper.KEY_GREEN);
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_BLUE), map.get(TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), map.get(TestHelper.KEY_GREEN));
		assertEquals(2, map.size());

		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_BLUE), cache.get(TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), cache.get(TestHelper.KEY_GREEN));
		assertEquals(2, cache.size());
	}

	@Test
	public void testMulti_ValuesAlreadyCached(){
		assertTrue(cache.isEmpty());

		String alteredColor = "brown";
		cache.set(TestHelper.KEY_BLUE, alteredColor);

		Map<String, Object> map = helper.getColors(TestHelper.KEY_BLUE, TestHelper.KEY_GREEN);
		assertEquals(alteredColor, map.get(TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), map.get(TestHelper.KEY_GREEN));
		assertEquals(2, map.size());

		assertEquals(alteredColor, cache.get(TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), cache.get(TestHelper.KEY_GREEN));
		assertEquals(2, cache.size());
	}

	@Test
	public void testMulti_ValuesCached_WithPrefix(){
		assertTrue(cache.isEmpty());

		//Returned keys should NOT include the prefix
		Map<String, Object> map = helper.getColors2(TestHelper.KEY_BLUE, TestHelper.KEY_GREEN);
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_BLUE), map.get(TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), map.get(TestHelper.KEY_GREEN));
		assertEquals(2, map.size());

		//Cached keys should include the prefix
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_BLUE), cache.get(TestHelper.PREFIX + TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), cache.get(TestHelper.PREFIX + TestHelper.KEY_GREEN));
		assertEquals(2, cache.size());
	}

	@Test
	public void testMulti_ValuesAlreadyCached_WithPrefix(){
		assertTrue(cache.isEmpty());

		String alteredColor = "brown";
		cache.set(TestHelper.PREFIX + TestHelper.KEY_BLUE, alteredColor);

		//Returned keys should NOT include the prefix
		Map<String, Object> map = helper.getColors2(TestHelper.KEY_BLUE, TestHelper.KEY_GREEN);
		assertEquals(alteredColor, map.get(TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), map.get(TestHelper.KEY_GREEN));
		assertEquals(2, map.size());

		//Cached keys should include the prefix
		assertEquals(alteredColor, cache.get(TestHelper.PREFIX + TestHelper.KEY_BLUE));
		assertEquals(TestHelper.COLOR_MAP.get(TestHelper.KEY_GREEN), cache.get(TestHelper.PREFIX + TestHelper.KEY_GREEN));
		assertEquals(2, cache.size());
	}

	public static class TestHelper{

		public final static String KEY_BLUE = "color-blue";
		public final static String KEY_GREEN = "color-green";

		public final static String PREFIX = "myprefix";

		@SuppressWarnings("serial")
		public final static Map<String, String> COLOR_MAP = new HashMap<String, String>(){{
			put(KEY_BLUE, "blue");
			put(KEY_GREEN, "green");
		}};

		@FetcherMethod(keyGenerator=FakeKeyGenerator.class, fetchBulk=true, keyCleaner=FakeKeyCleaner.class)
		public Map<String, Object> getColors(String... keys){
			return convert(keys);
		}

		@FetcherMethod(keyGenerator=FakeKeyGenerator.class, prefix=PREFIX, fetchBulk=true, keyCleaner=FakeKeyCleaner.class)
		public Map<String, Object> getColors2(String... keys){
			return convert(keys);
		}

		private Map<String, Object> convert(String[] keys){
			Map<String, Object> map = new HashMap<String, Object>();
			for(String key : keys){
				map.put(key, COLOR_MAP.get(key));
			}
			return map;
		}

	}

	public static class FakeKeyGenerator implements KeyGenerator{

		@Override
		public String generateKey(final Object[] arguments) {
			throw new UnsupportedOperationException("This method should not be being called.");
		}

		@Override
		public List<String> generateKeys(final Object[] arguments) {
			List<String> list = new ArrayList<String>();
			Object[] args = (Object[]) arguments[0];
			for(Object arg : args){
				list.add(arg.toString());
			}
			return list;
		}

	}

	public static class FakeKeyCleaner implements KeyCleaner{

		@Override
		public void clean(final Object[] arguments, final List<String> uncachedKeys) {
			arguments[0] = uncachedKeys.toArray(new String[0]);
		}

	}

}
