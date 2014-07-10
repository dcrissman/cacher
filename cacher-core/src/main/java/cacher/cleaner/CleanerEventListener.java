/*
 * Copyright 2014 Red Hat, Inc.
 * Author: Dennis Crissman
 *
 * Licensed under the GNU Lesser General Public License, version 3 or
 * any later version.
 *
 * In addition to the conditions of LGPLv3, you must preserve author
 * attributions in source code distributions.
 */

package cacher.cleaner;

import java.util.List;

public interface CleanerEventListener {

	void clearedFromCache(List<String> keys);

	void erroredKeys(List<String> keys);

}
