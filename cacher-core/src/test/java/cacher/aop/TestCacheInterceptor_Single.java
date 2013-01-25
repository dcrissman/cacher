package cacher.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import testframework.util.GuiceRunner;
import testframework.util.GuiceRunner.UseModules;
import cacher.Cache;
import cacher.fetcher.FetchManager;
import cacher.impl.InMemoryCache;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

@RunWith(GuiceRunner.class)
@UseModules(TestCacheInterceptor_Single.class)
public class TestCacheInterceptor_Single implements Module{

	@Inject
	private Cache cache;

	@Inject
	private TestHelper helper;

	@Override
	public void configure(Binder binder) {
		Cache cnfConfig = new InMemoryCache();
		binder.bind(Cache.class).toInstance(cnfConfig);

		CacheInterceptor interceptor = new CacheInterceptor(new FetchManager(cnfConfig));
		binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(FetcherMethod.class), interceptor);

		binder.bind(TestHelper.class);
	}

	@Before
	public void init(){
		cache.clear();
	}

	@Test
	public void testSingle_ValueCached(){
		assertNull(cache.get(FakeKeyGenerator.SINGLE_KEY));

		assertEquals(TestHelper.COLOR, helper.getColor());

		assertEquals(TestHelper.COLOR, cache.get(FakeKeyGenerator.SINGLE_KEY));
	}

	@Test
	public void testSingle_ValueAlreadyCached(){
		String cachedColor = "red";

		assertNull(cache.get(FakeKeyGenerator.SINGLE_KEY));

		cache.set(FakeKeyGenerator.SINGLE_KEY, cachedColor);

		assertEquals(cachedColor, helper.getColor());

		assertEquals(cachedColor, cache.get(FakeKeyGenerator.SINGLE_KEY));
	}

	@Test
	public void testSingle_ValueCached_WithPrefix(){
		assertNull(cache.get(FakeKeyGenerator.SINGLE_KEY));

		assertEquals(TestHelper.COLOR, helper.getColor2());

		//Cached key should contain the prefix
		assertEquals(TestHelper.COLOR, cache.get(TestHelper.PREFIX + FakeKeyGenerator.SINGLE_KEY));
	}

	@Test
	public void testSingle_ValueAlreadyCached_WithPrefix(){
		String cachedColor = "red";

		assertNull(cache.get(FakeKeyGenerator.SINGLE_KEY));

		cache.set(TestHelper.PREFIX + FakeKeyGenerator.SINGLE_KEY, cachedColor);

		assertEquals(cachedColor, helper.getColor2());

		//Cached key should contain the prefix
		assertEquals(cachedColor, cache.get(TestHelper.PREFIX + FakeKeyGenerator.SINGLE_KEY));
	}

	public static class TestHelper{

		public final static String COLOR = "purple";
		public final static String PREFIX = "myprefix";

		@FetcherMethod(keyGenerator=FakeKeyGenerator.class)
		public String getColor(){
			return COLOR;
		}

		@FetcherMethod(keyGenerator=FakeKeyGenerator.class, prefix=PREFIX)
		public String getColor2(){
			return COLOR;
		}

	}

	public static class FakeKeyGenerator implements KeyGenerator{

		public static String SINGLE_KEY = "fruit-color";

		@Override
		public String generateKey(Object[] arguments) {
			return SINGLE_KEY;
		}

		@Override
		public List<String> generateKeys(Object[] arguments) {
			throw new UnsupportedOperationException("This method should not be being called.");
		}

	}

}
