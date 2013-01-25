package cacher.fetcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A shortcut implementation for when multiple items are fetched together, but it needs to happen by
 * fetching each item one at a time.
 * 
 * @author Dennis Crissman
 *
 * @param <T>
 */
public abstract class FetchMultipleOneAtATime<T> implements FetchSingle<T>, FetchMultiple<T> {

	@Override
	public Map<String, T> fetch(List<String> keys) {
		Map<String, T> results = new HashMap<String, T>();
		for(String key : keys){
			results.put(key, fetch(key));
		}
		return results;
	}

}
