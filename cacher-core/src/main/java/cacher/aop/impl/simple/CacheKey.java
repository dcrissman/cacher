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

package cacher.aop.impl.simple;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Identifies the parameter to use as the key for the cache.</p>
 * <p>
 * For a single fetch, this parameter should be a <code>String</code> or an <code>Object</code>
 * that has had <code>#toString()</code> overridden.
 * </p>
 * <p>
 * For a bulk fetch, this parameter should be a {@link java.util.Collection} or an array. The values contained
 * within should be <code>String</code>s or <code>Object</code>s
 * that has had <code>#toString()</code> overridden.
 * </p>
 * 
 * @author Dennis Crissman
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface CacheKey {

}
