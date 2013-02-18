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

import org.aopalliance.intercept.MethodInvocation;

/**
 * Applicable to {@link KeyGenerator} or {@link KeyCleaner} implementations that need to know about
 * the {@link MethodInvocation}.
 * 
 * @author Dennis Crissman
 */
public interface MethodInvocationAware {

	/**
	 * Sets the {@link MethodInvocation}
	 * @param invocation - {@link MethodInvocation}
	 */
	void setMethodInvocation(MethodInvocation invocation);

}
