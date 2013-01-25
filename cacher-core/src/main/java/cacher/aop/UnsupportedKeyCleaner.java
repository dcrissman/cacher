package cacher.aop;

import java.util.List;

/**
 * Default value used in {@link FetcherMethod#keyCleaner()} if no other value is provided.
 * 
 * @author Dennis Crissman
 *
 */
public class UnsupportedKeyCleaner implements KeyCleaner {

	@Override
	public void clean(Object[] arguments, List<String> uncachedKeys) {
		throw new UnsupportedOperationException("An implementation of KeyCleaner must be provided for Bulk fetches.");
	}

}
