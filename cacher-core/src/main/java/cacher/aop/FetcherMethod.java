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

package cacher.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cacher.aop.impl.simple.SimpleCacheKeyCleaner;
import cacher.aop.impl.simple.SimpleCacheKeyGenerator;

/**
 * <p>The annotated method will be intercepted by the {@link CacheInterceptor} and the returned value(s) will be
 * cached. Or in the case that all values are already cached, the {@link FetcherMethod} will never actually be called.</p>
 * 
 * <p>This annotation can be used two ways. <b>In either case,
 * it is strongly recommended to keep the method signature as simple as possible.</b></p>
 * 
 * <p>1. In the first case, things are pretty straight forward, the returned value is cached exactly as it is.<br>
 * Only a {@link KeyGenerator} is required.</p>
 * 
 * <p>2. The second case (aka. Bulk Fetch) is more complicated. The idea is that a collection of keys is somehow passed
 * into the {@link FetcherMethod} and easy will be individually cached. It is then possible to have some key/values
 * already cached, and others not. <br>
 * {@link KeyGenerator}, {@link KeyCleaner} and fetchBulk = true are all required for this strategy.</p>
 * 
 * <p><b>NOTE:</b> Bulk Fetches are required to return a Map&#60;String, Object&#62;</p>
 * 
 * @author Dennis Crissman
 *
 * @see KeyCleaner
 * @see KeyGenerator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FetcherMethod {

	/**
	 * <p>Appends a prefix to each key.<br>
	 * This can be used for either case, but is most useful for bulk fetches. It allows a set of keys
	 * to be grouped together via a prefix without having to modify the key as it is used by the application.<br>
	 * This really comes down to a matter of preference.</p>
	 * <p>FOR EXAMPLE:<br>
	 * * For a given key '123', if a prefix is provided the key used in the cacher will be 'prefix123', but the value
	 * returned by the {@link cacher.fetcher.FetchManager} will be '123'.<br>
	 * * If a prefix is not included but rather build into the {@link KeyGenerator}, then the key 'prefix123' will
	 * be used for caching, but also returned from the {@link cacher.fetcher.FetchManager}.</p>
	 */
	String prefix() default "";

	/**
	 * {@link KeyGenerator} is used to generate the keys used by the {@link cacher.Cache}.
	 */
	Class<? extends KeyGenerator> keyGenerator() default SimpleCacheKeyGenerator.class;

	/**
	 * <p><code>true</code> indicates that bulk fetch should be performed,
	 * otherwise <code>false</code> indicates that just a single key/value is being fetched.</p>
	 * <p><b>NOTE:</b> Bulk fetches require that the annotated method returns a Map&#60;String, Object&#62;</p>
	 */
	boolean fetchBulk() default false;

	/**
	 * <p>An implementation of {@link KeyCleaner} is required only for bulk fetches, in order to
	 * remove keys that were already fetched from the {@link cacher.Cache}.</p>
	 * <p><b>NOTE:</b> Required and used only for bulk fetches.</p>
	 */
	Class<? extends KeyCleaner> keyCleaner() default SimpleCacheKeyCleaner.class;

}
