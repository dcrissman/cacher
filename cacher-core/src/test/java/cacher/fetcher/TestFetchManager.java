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

package cacher.fetcher;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cacher.Cache;
import cacher.CacheUtils;

public class TestFetchManager{

	private final static String PREFIX = "FAKE_PREFIX";

	private final FetchManager manager;
	private final Cache cache;
	private final FetchEventListener fetchEventListener;

	public TestFetchManager() {
		fetchEventListener = EasyMock.createMock(FetchEventListener.class);
		cache = EasyMock.createMock(Cache.class);

		manager = new FetchManager(cache, Arrays.asList(fetchEventListener));
	}

	@Before
	public void init(){
		reset(cache);
		replay(cache);

		reset(fetchEventListener);
		replay(fetchEventListener);
	}

	@After
	public void after(){
		verify(cache);
		verify(fetchEventListener);
	}

	protected Capture<Object> resetCacheForSingle(Object value){
		reset(cache);
		expect(cache.get(
				EasyMock.anyObject(String.class))
				).andReturn(value).once();

		Capture<Object> captureNewlyCachedObject = new Capture<Object>();
		cache.set(
				EasyMock.anyObject(String.class),
				EasyMock.capture(captureNewlyCachedObject)
				);
		expectLastCall().anyTimes();
		replay(cache);

		return captureNewlyCachedObject;
	}

	@SuppressWarnings("unchecked")
	protected Capture<Object> resetCacheForMultiple(Map<String, Object> value){
		reset(cache);
		expect(cache.getBulk(
				EasyMock.anyObject(List.class))
				).andReturn(value).once();

		Capture<Object> captureNewlyCachedObject = new Capture<Object>();
		cache.set(
				EasyMock.anyObject(String.class),
				EasyMock.capture(captureNewlyCachedObject)
				);
		expectLastCall().anyTimes();

		replay(cache);

		return captureNewlyCachedObject;
	}

	@Test
	public void testMultiple_NullListOfKeys(){
		Map<String, Apple> map = manager.fetchMultiple("", null, new FetchMultiple<Apple>() {

			@Override
			public Class<Apple> getType() {
				throw new UnsupportedOperationException("Test Exception that should NOT be thrown");
			}

			@Override
			public Map<String, Apple> fetch(List<String> keys) {
				throw new UnsupportedOperationException("Test Exception that should NOT be thrown");
			}
		});

		assertNotNull(map);
		assertTrue(map.isEmpty());
	}

	@Test
	public void testMultiple_EmptyListOfKeys(){
		Map<String, Apple> map = manager.fetchMultiple("", new ArrayList<String>(), new FetchMultiple<Apple>() {

			@Override
			public Class<Apple> getType() {
				throw new UnsupportedOperationException("Test Exception that should NOT be thrown");
			}

			@Override
			public Map<String, Apple> fetch(List<String> keys) {
				throw new UnsupportedOperationException("Test Exception that should NOT be thrown");
			}
		});

		assertNotNull(map);
		assertTrue(map.isEmpty());
	}

	@SuppressWarnings("serial")
	@Test
	public void testMultiple_AllCached(){
		final String key1 = "123";
		final String key2 = "234";
		final Apple a1 = new Apple();
		final Apple a2 = new Apple();

		reset(fetchEventListener);
		Capture<List<String>> captureFFC = new Capture<List<String>>();
		fetchEventListener.fetchedFromCache(EasyMock.capture(captureFFC));
		expectLastCall().once();
		replay(fetchEventListener);

		Capture<Object> captureNewlyCachedObject = resetCacheForMultiple(new HashMap<String, Object>(){{
			put(CacheUtils.prefixedKey(PREFIX, key1), a1);
			put(CacheUtils.prefixedKey(PREFIX, key2), a2);
		}});

		Map<String, Apple> apples = manager.fetchMultiple(
				PREFIX, Arrays.asList(key1, key2), new FetchMultiple<Apple>() {

					@Override
					public Class<Apple> getType() {
						return Apple.class;
					}

					@Override
					public Map<String, Apple> fetch(List<String> keys) {
						throw new UnsupportedOperationException("This method should not be being called.");
					}
				});

		assertNotNull(apples);
		assertEquals(2, apples.size());
		assertTrue(apples.containsKey(key1));
		assertSame(apples.get(key1), a1);
		assertTrue(apples.containsKey(key2));
		assertSame(apples.get(key2), a2);

		//Verify values fetcher events
		List<String> keysFetchedFromCache = captureFFC.getValue();
		assertNotNull(keysFetchedFromCache);
		assertEquals(2, keysFetchedFromCache.size());
		assertTrue(keysFetchedFromCache.contains(key1));
		assertTrue(keysFetchedFromCache.contains(key2));

		//Nothing needed to be cached
		assertFalse(captureNewlyCachedObject.hasCaptured());

		//no calls should be made to the fetcher.
	}

	@SuppressWarnings("serial")
	@Test
	public void testMultiple_Mixed(){
		final String key1 = "123";
		final String key2 = "234";
		final Apple a1 = new Apple();
		final Apple a2 = new Apple();

		reset(fetchEventListener);
		Capture<List<String>> captureFFC = new Capture<List<String>>();
		fetchEventListener.fetchedFromCache(EasyMock.capture(captureFFC));
		expectLastCall().once();
		Capture<List<String>> captureFFF = new Capture<List<String>>();
		fetchEventListener.fetchedFromFetcher(EasyMock.capture(captureFFF));
		expectLastCall().once();
		replay(fetchEventListener);

		Capture<Object> captureNewlyCachedObjects = resetCacheForMultiple(new HashMap<String, Object>(){{
			put(CacheUtils.prefixedKey(PREFIX, key1), a1);
		}});

		Map<String, Apple> apples = manager.fetchMultiple(
				PREFIX, Arrays.asList(key1, key2), new FetchMultiple<Apple>() {

					@Override
					public Class<Apple> getType() {
						return Apple.class;
					}

					@Override
					public Map<String, Apple> fetch(List<String> keys) {
						return new HashMap<String, Apple>(){{
							put(key2, a2);
						}};
					}
				});

		assertNotNull(apples);
		assertEquals(2, apples.size());
		assertTrue(apples.containsKey(key1));
		assertSame(apples.get(key1), a1);
		assertTrue(apples.containsKey(key2));
		assertSame(apples.get(key2), a2);

		//a2 should have been cached
		assertSame(captureNewlyCachedObjects.getValue(), a2);

		//Verify values fetcher events
		List<String> keysFetchedFromCache = captureFFC.getValue();
		assertNotNull(keysFetchedFromCache);
		assertEquals(1, keysFetchedFromCache.size());
		assertTrue(keysFetchedFromCache.contains(key1));

		List<String> keysFetchedFromFetcher = captureFFF.getValue();
		assertNotNull(keysFetchedFromFetcher);
		assertEquals(1, keysFetchedFromFetcher.size());
		assertTrue(keysFetchedFromFetcher.contains(key2));
	}

	/**
	 * Null should be returned in Map, but should not be cached.
	 */
	@SuppressWarnings({ "serial", "unchecked" })
	@Test
	public void testMultiple_Null(){
		final String key1 = "123";
		final Apple a1 = null;

		reset(fetchEventListener);
		fetchEventListener.fetchedFromFetcher(EasyMock.anyObject(List.class));
		expectLastCall().once();
		replay(fetchEventListener);

		Capture<Object> captureNewlyCachedObjects = resetCacheForMultiple(new HashMap<String, Object>());

		Map<String, Apple> apples = manager.fetchMultiple(
				PREFIX, Arrays.asList(key1), new FetchMultiple<Apple>() {

					@Override
					public Class<Apple> getType() {
						return Apple.class;
					}

					@Override
					public Map<String, Apple> fetch(List<String> keys) {
						return new HashMap<String, TestFetchManager.Apple>(){{
							put(key1, a1);
						}};
					}
				});

		assertNotNull(apples);
		assertEquals(1, apples.size());
		assertTrue(apples.containsKey(key1));

		//a2 should have been cached
		assertFalse(captureNewlyCachedObjects.hasCaptured());
	}

	@SuppressWarnings("serial")
	@Test(expected = ClassCastException.class)
	public void testMultiple_ClassCastException(){
		final String key1 = "123";
		final String prefixedKey1 = CacheUtils.prefixedKey(PREFIX, key1);

		resetCacheForMultiple(new HashMap<String, Object>(){{
			put(prefixedKey1, new Long(0));
		}});

		manager.fetchMultiple(
				PREFIX, Arrays.asList(key1), new FetchMultiple<Apple>() {

					@Override
					public Class<Apple> getType() {
						return Apple.class;
					}

					@Override
					public Map<String, Apple> fetch(List<String> keys) {
						throw new UnsupportedOperationException("This method should not be being called.");
					}
				});

		//no calls should be made to the fetcher.
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMultiple_CacheThrowsException(){
		final String key1 = "123";
		final String key2 = "234";
		final Apple a1 = new Apple();
		final Apple a2 = new Apple();

		reset(fetchEventListener);
		fetchEventListener.fetchedFromFetcher(EasyMock.anyObject(List.class));
		expectLastCall().once();
		replay(fetchEventListener);

		reset(cache);
		expect(cache.getBulk(
				EasyMock.anyObject(List.class))
				).andThrow(new RuntimeException("Fake Exception")).once();

		Capture<Object> captureNewlyCachedObject = new Capture<Object>();
		cache.set(
				EasyMock.anyObject(String.class),
				EasyMock.capture(captureNewlyCachedObject)
				);
		expectLastCall().andThrow(new RuntimeException("Fake Exception")).times(2);

		replay(cache);

		Map<String, Apple> apples = manager.fetchMultiple(
				PREFIX, Arrays.asList(key1, key2), new FetchMultiple<Apple>() {

					@Override
					public Class<Apple> getType() {
						return Apple.class;
					}

					@SuppressWarnings("serial")
					@Override
					public Map<String, Apple> fetch(List<String> keys) {
						return new HashMap<String, Apple>(){{
							put(key1, a1);
							put(key2, a2);
						}};
					}
				});

		assertNotNull(apples);
		assertEquals(2, apples.size());
		assertTrue(apples.containsKey(key1));
		assertSame(apples.get(key1), a1);
		assertTrue(apples.containsKey(key2));
		assertSame(apples.get(key2), a2);
	}

	@Test
	public void testSingle_NullKey(){
		Apple apple = manager.fetchSingle(null, new FetchSingle<Apple>() {

			@Override
			public Class<Apple> getType() {
				throw new UnsupportedOperationException("Test Exception that should NOT be thrown");
			}

			@Override
			public Apple fetch(String keys) {
				throw new UnsupportedOperationException("Test Exception that should NOT be thrown");
			}
		});

		assertNull(apple);
	}

	@Test
	public void testSingle_Cached(){
		final String key1 = "123";
		final Apple a1 = new Apple();

		reset(fetchEventListener);
		Capture<List<String>> captureFFC = new Capture<List<String>>();
		fetchEventListener.fetchedFromCache(EasyMock.capture(captureFFC));
		expectLastCall().once();
		replay(fetchEventListener);

		Capture<Object> newlyCachedObject = resetCacheForSingle(a1);

		Apple apple = manager.fetchSingle(key1, new FetchSingle<Apple>() {

			@Override
			public Class<Apple> getType() {
				return Apple.class;
			}

			@Override
			public Apple fetch(String key) {
				throw new UnsupportedOperationException("This method should not be being called.");
			}
		});

		assertNotNull(apple);
		assertSame(apple, a1);

		//Nothing needed to be cached
		assertFalse(newlyCachedObject.hasCaptured());

		//no calls should be made to the fetcher.

		//Verify values fetcher events
		List<String> keysFetchedFromCache = captureFFC.getValue();
		assertNotNull(keysFetchedFromCache);
		assertEquals(1, keysFetchedFromCache.size());
		assertTrue(keysFetchedFromCache.contains(key1));
	}

	@Test
	public void testSingle_NotCached(){
		final String key1 = "123";
		final Apple a1 = new Apple();

		reset(fetchEventListener);
		Capture<List<String>> captureFFF = new Capture<List<String>>();
		fetchEventListener.fetchedFromFetcher(EasyMock.capture(captureFFF));
		expectLastCall().once();
		replay(fetchEventListener);

		Capture<Object> newlyCachedObject = resetCacheForSingle(null);

		Apple apple = manager.fetchSingle(key1, new FetchSingle<Apple>() {

			@Override
			public Class<Apple> getType() {
				return Apple.class;
			}

			@Override
			public Apple fetch(String key) {
				return a1;
			}
		});

		assertNotNull(apple);
		assertSame(apple, a1);

		//a1 should have been cached
		assertSame(newlyCachedObject.getValue(), a1);

		//Verify values fetcher events
		List<String> keysFetchedFromFetcher = captureFFF.getValue();
		assertNotNull(keysFetchedFromFetcher);
		assertEquals(1, keysFetchedFromFetcher.size());
		assertTrue(keysFetchedFromFetcher.contains(key1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSingle_Null(){
		final String key1 = "123";
		final Apple a1 = null;

		reset(fetchEventListener);
		fetchEventListener.fetchedFromFetcher(EasyMock.anyObject(List.class));
		expectLastCall().once();
		replay(fetchEventListener);

		Capture<Object> newlyCachedObject = resetCacheForSingle(null);

		Apple apple = manager.fetchSingle(key1, new FetchSingle<Apple>() {

			@Override
			public Class<Apple> getType() {
				return Apple.class;
			}

			@Override
			public Apple fetch(String key) {
				return a1;
			}
		});

		assertNull(apple);

		//a1 should have been cached
		assertFalse(newlyCachedObject.hasCaptured());
	}

	@Test(expected = ClassCastException.class)
	public void testSingle_ClassCastException(){
		final String key1 = "123";

		resetCacheForSingle(new Long(0));

		manager.fetchSingle(key1, new FetchSingle<Apple>() {

			@Override
			public Class<Apple> getType() {
				return Apple.class;
			}

			@Override
			public Apple fetch(String key) {
				throw new UnsupportedOperationException("This method should not be being called.");
			}
		});

		//no calls should be made to the fetcher.
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSingle_CacheThrowsException(){
		final String key1 = "123";
		final Apple a1 = new Apple();

		reset(fetchEventListener);
		fetchEventListener.fetchedFromFetcher(EasyMock.anyObject(List.class));
		expectLastCall().once();
		replay(fetchEventListener);

		reset(cache);
		expect(cache.get(
				EasyMock.anyObject(String.class))
				).andThrow(new RuntimeException("Fake Exception")).once();
		cache.set(
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(Apple.class)
				);
		expectLastCall().andThrow(new RuntimeException("Fake Exception"));
		replay(cache);

		Apple apple = manager.fetchSingle(key1, new FetchSingle<Apple>() {

			@Override
			public Class<Apple> getType() {
				return Apple.class;
			}

			@Override
			public Apple fetch(String key) {
				return a1;
			}
		});

		assertNotNull(apple);
		assertSame(apple, a1);
	}

	private class Apple{}

}
