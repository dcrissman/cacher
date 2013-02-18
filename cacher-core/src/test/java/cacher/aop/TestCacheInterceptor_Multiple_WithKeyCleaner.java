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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
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
@UseModules(TestCacheInterceptor_Multiple_WithKeyCleaner.class)
public class TestCacheInterceptor_Multiple_WithKeyCleaner implements Module{

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

	/**
	 * Asserts that the key cleaner can in fact change method argument values
	 */
	@Test
	public void testValueChangingKeyCleaner(){
		assertEquals(ValueChangingKeyCleaner.NEW_VALUE, helper.changeValue("whatever").get(TestHelper.HELPER_KEY1));
	}

	/**
	 * Asserts that if the key cleaner changes a argument value, but then an exception is thrown, that there is at least
	 * some level of ability to get the unaltered method arguments back.
	 */
	@Test
	public void testTroubleMakingKeyCleaner(){
		String value = "whatever";
		assertEquals(value, helper.makeTrouble(value).get(TestHelper.HELPER_KEY1));
	}

	public static class TestHelper{

		public final static String HELPER_KEY1 = "helperkey1";

		@SuppressWarnings("serial")
		@FetcherMethod(keyGenerator=FakeKeyGenerator.class, fetchBulk=true, keyCleaner=ValueChangingKeyCleaner.class)
		public Map<String, Object> changeValue(final String value){
			return new HashMap<String, Object>(){{put(HELPER_KEY1, value);}};
		}

		@SuppressWarnings("serial")
		@FetcherMethod(keyGenerator=FakeKeyGenerator.class, fetchBulk=true, keyCleaner=TroubleMakingKeyCleaner.class)
		public Map<String, Object> makeTrouble(final String value){
			return new HashMap<String, Object>(){{put(HELPER_KEY1, value);}};
		}

	}

	public static class FakeKeyGenerator implements KeyGenerator{

		public final static String KEY1 = "key1";
		public final static String KEY2 = "key2";
		public final static String KEY3 = "key3";

		@Override
		public String generateKey(final Object[] arguments) {
			throw new UnsupportedOperationException("This method should not be being called.");
		}

		@Override
		public List<String> generateKeys(final Object[] arguments) {
			return Arrays.asList(KEY1, KEY2, KEY3);
		}

	}

	public static class ValueChangingKeyCleaner implements KeyCleaner{

		public static final String NEW_VALUE = "value changed";

		@Override
		public void clean(Object[] arguments, List<String> uncachedKeys) {
			arguments[0] = NEW_VALUE;
		}

	}

	public static class TroubleMakingKeyCleaner extends ValueChangingKeyCleaner{

		@Override
		public void clean(Object[] arguments, List<String> uncachedKeys) {
			super.clean(arguments, uncachedKeys);
			throw new RuntimeException("FAKE EXCEPTION");
		}

	}

}
