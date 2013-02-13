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

public interface MethodInvocationAware {

	void setMethodInvocation(MethodInvocation invocation);

}
