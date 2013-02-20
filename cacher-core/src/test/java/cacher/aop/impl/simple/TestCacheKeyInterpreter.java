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

package cacher.aop.impl.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import testframework.util.GuiceRunner;
import testframework.util.GuiceRunner.UseModules;
import cacher.aop.impl.simple.CacheKeyInterpreter.ConversionException;
import cacher.aop.impl.simple.CacheKeyInterpreter.MultipleKeyException;
import cacher.aop.impl.simple.CacheKeyInterpreter.NoKeyException;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

@RunWith(GuiceRunner.class)
@UseModules(TestCacheKeyInterpreter.class)
public class TestCacheKeyInterpreter implements Module{

	@Inject
	private CacheKeyInterpreter interpreter;

	@Inject
	private TestHelper helper;

	@Override
	public void configure(Binder binder) {
		binder.bind(CacheKeyInterpreter.class).asEagerSingleton();

		FakeMethodInterceptor interceptor = new FakeMethodInterceptor();
		binder.requestInjection(interceptor);
		binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(TestMethod.class), interceptor);

		binder.bind(TestHelper.class);
	}

	@After
	public void after(){
		interpreter.setMethodInvocation(null);
	}

	@Test(expected = NoKeyException.class)
	public void testFindKeyPosition_NoKeyException(){
		helper.noCacheKey();
		interpreter.findKeyPosition();
	}

	@Test(expected = MultipleKeyException.class)
	public void testFindKeyPosition_MultipleKeyException(){
		helper.multipleCacheKeys("whatever", "anything");
		interpreter.findKeyPosition();
	}

	@Test
	public void testFindKeyPosition(){
		helper.singleCacheKey("whatever", "key", "anything");
		assertEquals(1, interpreter.findKeyPosition());
	}

	@Test(expected = ConversionException.class)
	public void testConvertToList_ConversionException(){
		interpreter.convertToList("not a collection");
	}

	@Test
	public void testConvertToList_ObjectAsCollection(){
		List<String> values = interpreter.convertToList(Arrays.asList("value1", "value2"));
		assertFalse(values.isEmpty());
		assertTrue(values.containsAll(Arrays.asList("value1", "value2")));
	}

	@Test
	public void testConvertToList_ObjectAsArray(){
		List<String> values = interpreter.convertToList(new String[]{"value1", "value2"});
		assertFalse(values.isEmpty());
		assertTrue(values.containsAll(Arrays.asList("value1", "value2")));
	}

	public static class TestHelper{

		@TestMethod
		public void noCacheKey(){
			//Do Nothing!
		}

		@TestMethod
		public void multipleCacheKeys(@CacheKey String key1, @CacheKey String key2){
			//Do Nothing!
		}

		@TestMethod
		public void singleCacheKey(String someValue, @CacheKey String key, String someOtherValue){
			//Do Nothing!
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface TestMethod{}

	private class FakeMethodInterceptor implements MethodInterceptor{

		@Inject
		private CacheKeyInterpreter interpreter;

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			interpreter.setMethodInvocation(invocation);
			return null;
		}

	}

}
