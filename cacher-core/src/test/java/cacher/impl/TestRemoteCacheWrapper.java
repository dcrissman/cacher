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

package cacher.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cacher.Cache;
import cacher.impl.RemoteCacheWrapper.CacheServiceInitializer;
import cacher.impl.RemoteCacheWrapper.ConnectionListener;

public class TestRemoteCacheWrapper {

	private static final String KEY = "FAKEKEY";
	private static final String VALUE = "FAKEVALUE";

	private final Cache mockedCache = EasyMock.createMock(Cache.class);
	private final CacheServiceInitializer mockedInitializer = EasyMock.createMock(CacheServiceInitializer.class);
	private final ConnectionListener mockedConnectionListener = EasyMock.createMock(ConnectionListener.class);

	@Before
	public void init(){
		reset(mockedCache);
		replay(mockedCache);

		reset(mockedInitializer);
		replay(mockedInitializer);

		reset(mockedConnectionListener);
		replay(mockedConnectionListener);
	}

	@After
	public void after(){
		verify(mockedCache);
		verify(mockedInitializer);
		verify(mockedConnectionListener);
	}

	private RemoteCacheWrapper createInstance(){
		RemoteCacheWrapper cache = new RemoteCacheWrapper(mockedInitializer);
		cache.addConnectionListener(mockedConnectionListener);
		return cache;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCacheServiceInitializer(){
		new RemoteCacheWrapper(null);
	}

	@Test
	public void testAlwaysValidConnection_Set() throws IOException{
		reset(mockedInitializer);
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		replay(mockedInitializer);

		reset(mockedCache);
		mockedCache.set(KEY, VALUE);
		expectLastCall().once();
		replay(mockedCache);

		RemoteCacheWrapper wrapper = createInstance();

		wrapper.set(KEY, VALUE);

		assertFalse(wrapper.getServiceCache() instanceof NullCache);
	}

	@Test
	public void testAlwaysValidConnection_Get() throws IOException{
		reset(mockedInitializer);
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		replay(mockedInitializer);

		reset(mockedCache);
		expect(mockedCache.get(KEY)).andReturn(VALUE).once();
		replay(mockedCache);

		RemoteCacheWrapper wrapper = createInstance();

		Object rtnValue = wrapper.get(KEY);

		assertFalse(wrapper.getServiceCache() instanceof NullCache);
		assertEquals(VALUE, rtnValue);
	}

	@Test
	public void testAlwaysValidConnection_GetBulk() throws IOException{
		List<String> keys = Arrays.asList(KEY);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(KEY, VALUE);

		reset(mockedInitializer);
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		replay(mockedInitializer);

		reset(mockedCache);
		expect(mockedCache.getBulk(keys)).andReturn(values).once();
		replay(mockedCache);

		RemoteCacheWrapper wrapper = createInstance();

		Object rtnValue = wrapper.getBulk(keys);

		assertFalse(wrapper.getServiceCache() instanceof NullCache);
		assertEquals(values, rtnValue);
	}

	@Test
	public void testAlwaysValidConnection_Clear() throws IOException{
		reset(mockedInitializer);
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		replay(mockedInitializer);

		reset(mockedCache);
		mockedCache.clear();
		expectLastCall().once();
		replay(mockedCache);

		RemoteCacheWrapper wrapper = createInstance();

		wrapper.clear();

		assertFalse(wrapper.getServiceCache() instanceof NullCache);
	}

	@Test
	public void testAlwaysBrokenConnection_Set() throws Exception{
		reset(mockedInitializer);
		reset(mockedConnectionListener);

		expect(mockedInitializer.createCacheInstance()).andThrow(new IOException("Fake Exception")).times(2);
		mockedConnectionListener.connectionBroken(EasyMock.anyObject(IOException.class));
		expectLastCall().once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);

		RemoteCacheWrapper wrapper = createInstance();

		wrapper.set(KEY, VALUE);

		assertTrue(wrapper.getServiceCache() instanceof NullCache);
	}

	@Test
	public void testAlwaysBrokenConnection_Get() throws IOException{
		reset(mockedInitializer);
		reset(mockedConnectionListener);

		expect(mockedInitializer.createCacheInstance()).andThrow(new IOException("Fake Exception")).times(2);
		mockedConnectionListener.connectionBroken(EasyMock.anyObject(IOException.class));
		expectLastCall().once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);

		RemoteCacheWrapper wrapper = createInstance();

		Object rtnValue = wrapper.get(KEY);

		assertTrue(wrapper.getServiceCache() instanceof NullCache);
		assertNull(rtnValue);
	}

	@Test
	public void testAlwaysBrokenConnection_GetBulk() throws IOException{
		reset(mockedInitializer);
		reset(mockedConnectionListener);

		expect(mockedInitializer.createCacheInstance()).andThrow(new IOException("Fake Exception")).times(2);
		mockedConnectionListener.connectionBroken(EasyMock.anyObject(IOException.class));
		expectLastCall().once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);

		RemoteCacheWrapper wrapper = createInstance();

		Map<String, Object> rtnValue = wrapper.getBulk(Arrays.asList(KEY));

		assertTrue(wrapper.getServiceCache() instanceof NullCache);
		assertNotNull(rtnValue);
		assertTrue(rtnValue.isEmpty());
	}

	@Test
	public void testAlwaysBrokenConnection_Clear() throws IOException{
		reset(mockedInitializer);
		reset(mockedConnectionListener);

		expect(mockedInitializer.createCacheInstance()).andThrow(new IOException("Fake Exception")).times(2);
		mockedConnectionListener.connectionBroken(EasyMock.anyObject(IOException.class));
		expectLastCall().once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);

		RemoteCacheWrapper wrapper = createInstance();

		wrapper.clear();

		assertTrue(wrapper.getServiceCache() instanceof NullCache);
	}

	@Test
	public void testConnection_Made_Broken_ThenReestablished_Set() throws IOException{
		//Mocking is in order of execution for ease of readability
		reset(mockedInitializer);
		reset(mockedConnectionListener);
		reset(mockedCache);

		//Setup Connection Made
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		mockedConnectionListener.connectionEstablished();
		expectLastCall().once();

		mockedCache.set(KEY, VALUE);
		expectLastCall().once();

		//Setup Connection Broken
		mockedCache.set(KEY, VALUE);
		expectLastCall().andThrow(new RuntimeException("Fake Exception")).once();

		mockedConnectionListener.connectionBroken(EasyMock.anyObject(Exception.class));
		expectLastCall().once();

		//Setup Connection Reestablished
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();

		mockedCache.set(KEY, VALUE);
		expectLastCall().once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);
		replay(mockedCache);

		//Execute Test
		RemoteCacheWrapper wrapper = createInstance();

		wrapper.set(KEY, VALUE);
		assertFalse(wrapper.getServiceCache() instanceof NullCache);

		wrapper.set(KEY, VALUE);
		assertTrue(wrapper.getServiceCache() instanceof NullCache);

		wrapper.set(KEY, VALUE);
		assertFalse(wrapper.getServiceCache() instanceof NullCache);
	}

	@Test
	public void testConnection_Made_Broken_ThenReestablished_Get() throws IOException{
		//Mocking is in order of execution for ease of readability
		reset(mockedInitializer);
		reset(mockedConnectionListener);
		reset(mockedCache);

		//Setup Connection Made
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		mockedConnectionListener.connectionEstablished();
		expectLastCall().once();

		expect(mockedCache.get(KEY)).andReturn(VALUE).once();

		//Setup Connection Broken
		expect(mockedCache.get(KEY)).andThrow(new RuntimeException("Fake Exception")).once();

		mockedConnectionListener.connectionBroken(EasyMock.anyObject(Exception.class));
		expectLastCall().once();

		//Setup Connection Reestablished
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();

		expect(mockedCache.get(KEY)).andReturn(VALUE).once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);
		replay(mockedCache);

		//Execute Test
		RemoteCacheWrapper wrapper = createInstance();

		assertEquals(VALUE, wrapper.get(KEY));
		assertFalse(wrapper.getServiceCache() instanceof NullCache);

		assertNull(wrapper.get(KEY));
		assertTrue(wrapper.getServiceCache() instanceof NullCache);

		assertEquals(VALUE, wrapper.get(KEY));
		assertFalse(wrapper.getServiceCache() instanceof NullCache);
	}

	@Test
	public void testConnection_Made_Broken_ThenReestablished_GetBulk() throws IOException{
		List<String> keys = Arrays.asList(KEY);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(KEY, VALUE);

		//Mocking is in order of execution for ease of readability
		reset(mockedInitializer);
		reset(mockedConnectionListener);
		reset(mockedCache);

		//Setup Connection Made
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		mockedConnectionListener.connectionEstablished();
		expectLastCall().once();

		expect(mockedCache.getBulk(keys)).andReturn(values).once();

		//Setup Connection Broken
		expect(mockedCache.getBulk(keys)).andThrow(new RuntimeException("Fake Exception")).once();

		mockedConnectionListener.connectionBroken(EasyMock.anyObject(Exception.class));
		expectLastCall().once();

		//Setup Connection Reestablished
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();

		expect(mockedCache.getBulk(keys)).andReturn(values).once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);
		replay(mockedCache);

		//Execute Test
		RemoteCacheWrapper wrapper = createInstance();

		assertEquals(values, wrapper.getBulk(keys));
		assertFalse(wrapper.getServiceCache() instanceof NullCache);

		Map<String, Object> rtnValue = wrapper.getBulk(keys);
		assertTrue(wrapper.getServiceCache() instanceof NullCache);
		assertNotNull(rtnValue);
		assertTrue(rtnValue.isEmpty());

		assertEquals(values, wrapper.getBulk(keys));
		assertFalse(wrapper.getServiceCache() instanceof NullCache);
	}

	@Test
	public void testConnection_Made_Broken_ThenReestablished_Clear() throws IOException{
		//Mocking is in order of execution for ease of readability
		reset(mockedInitializer);
		reset(mockedConnectionListener);
		reset(mockedCache);

		//Setup Connection Made
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();
		mockedConnectionListener.connectionEstablished();
		expectLastCall().once();

		mockedCache.clear();
		expectLastCall().once();

		//Setup Connection Broken
		mockedCache.clear();
		expectLastCall().andThrow(new RuntimeException("Fake Exception")).once();

		mockedConnectionListener.connectionBroken(EasyMock.anyObject(Exception.class));
		expectLastCall().once();

		//Setup Connection Reestablished
		expect(mockedInitializer.createCacheInstance()).andReturn(mockedCache).once();

		mockedCache.clear();
		expectLastCall().once();

		replay(mockedConnectionListener);
		replay(mockedInitializer);
		replay(mockedCache);

		//Execute Test
		RemoteCacheWrapper wrapper = createInstance();

		wrapper.clear();
		assertFalse(wrapper.getServiceCache() instanceof NullCache);

		wrapper.clear();
		assertTrue(wrapper.getServiceCache() instanceof NullCache);

		wrapper.clear();
		assertFalse(wrapper.getServiceCache() instanceof NullCache);
	}

}
