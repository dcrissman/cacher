package cacher.fetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cacher.Cache;

/**
 * A wrapper around {@link cacher.Cache} that will automate fetching from the
 * cacher if available, otherwise retrieving data from a strategy and then caching that value.<br>
 * <br>
 * <b>NOTE:</b> if an unexpected class type is returned from the {@link cacher.Cache}
 * a {@link ClassCastException} will be returned.
 * 
 * @author Dennis Crissman
 * 
 * @see FetchSingle
 * @see FetchMultiple
 */
public class FetchManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FetchManager.class);

	private final List<FetchEventListener> fetchEventListeners;

	private final Cache cache;

	/**
	 * Returns the prefixed key value.
	 * @param prefix - String prefix
	 * @param key - String key
	 * @return prefixed key
	 */
	public static String prefixedKey(String prefix, String key){
		if(prefix == null){
			return key;
		}
		return prefix + key;
	}

	public FetchManager(Cache cache){
		this(cache, null);
	}

	public FetchManager(Cache cache, List<FetchEventListener> fetchEventListeners){
		this.cache = cache;
		this.fetchEventListeners = (fetchEventListeners == null) ? new ArrayList<FetchEventListener>() : fetchEventListeners;
	}

	public Cache getCache(){
		return cache;
	}

	/**
	 * Searches the {@link Cache} for the provided keys. If cached, then the cached instance will be
	 * returned, otherwise the method will attempt to find and use a {@link FetchMultiple} to find the value
	 * in question and will then cacher that value for next time.
	 * @param keys - List of keys
	 * @param fetcher - Instance of {@link FetchMultiple}
	 * @return Map of keys to T instances
	 */
	public <T> Map<String, T> fetchMultiple(List<String> keys, FetchMultiple<T> fetcher){
		return fetchMultiple(null,  keys, fetcher);
	}

	/**
	 * <p>Searches the {@link Cache} for the provided keys. If cached, then the cached instance will be
	 * returned, otherwise the method will attempt to find and use a {@link FetchMultiple} to find the value
	 * in question and will then cacher that value for next time.</p>
	 * <p>This method is different from {@link #fetchMultiple(List, FetchMultiple)} in that it allows a group (or prefix)
	 * to be assigned to each key when being read or written. Useful for a set of related keys.</p>
	 * @param group - key prefix to use with the cacher
	 * @param keys - List of keys
	 * @param fetcher - Instance of {@link FetchMultiple}
	 * @return Map of keys to T instances
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> fetchMultiple(String group, List<String> keys, FetchMultiple<T> fetcher){
		Map<String, T> map = new HashMap<String, T>();
		if((keys == null) || keys.isEmpty()){
			return map;
		}

		List<String> uncachedObjects = new ArrayList<String>();
		try{
			Map<String, Object> cachedObjects = getBulkWithPrefix(group, keys);
			for(String key : keys) {
				Object obj = cachedObjects.get(prefixedKey(group, key));
				if(obj == null){
					uncachedObjects.add(key);
				}
				else {
					assertValidType(fetcher.getType(), obj);
					map.put(key, (T)obj);
				}
			}
			fireFetchedFromCacheEvent(new ArrayList<String>(map.keySet()));
		}
		catch(ClassCastException e){ //NOSONAR
			throw e;
		}
		catch(RuntimeException e){ //Likely a CacheResult issue.
			LOGGER.error("Unable to fetch from cacher - Group: '"
					+ (group == null ? "" : group)
					+ "' Keys: " + keys, e);
			map.clear();
			uncachedObjects.clear();
			uncachedObjects.addAll(keys);
		}

		if(!uncachedObjects.isEmpty()) {
			Map<String, T> missingObjects = fetcher.fetch(uncachedObjects);
			for(Entry<String, T> entry : missingObjects.entrySet()){
				map.put(entry.getKey(), entry.getValue());
				addToCache(prefixedKey(group, entry.getKey()), entry.getValue());
			}
			fireFetchedFromFetcherEvent(uncachedObjects);
		}

		return map;
	}

	private Map<String, Object> getBulkWithPrefix(String prefix, List<String> keys){
		ArrayList<String> adjustedKeys = new ArrayList<String>();
		for(String key : keys) {
			adjustedKeys.add(prefixedKey(prefix, key));
		}

		return cache.getBulk(adjustedKeys);
	}

	/**
	 * <p>Searches the {@link cacher.Cache} for the provided key. If cached, then the cached instance will be
	 * returned, otherwise the method will attempt to find and use a {@link FetchSingle} to find the value
	 * in question and will then cacher that value for next time.</p>
	 * @param key - key
	 * @param fetcher - Instance of {@link FetchSingle}
	 * @return T instance
	 */
	public <T> T fetchSingle(String key, FetchSingle<T> fetcher){
		return fetchSingle(null,  key, fetcher);
	}

	/**
	 * <p>Searches the {@link cacher.Cache} for the provided key. If cached, then the cached instance will be
	 * returned, otherwise the method will attempt to find and use a {@link FetchSingle} to find the value
	 * in question and will then cacher that value for next time.</p>
	 * <p>This method is different from {@link #fetchMultiple(List, FetchMultiple)} in that it allows a group (or prefix)
	 * to be assigned to each key when being read or written. Useful for a set of related keys.</p>
	 * @param group - key prefix to use with the cacher
	 * @param key - key
	 * @param fetcher - Instance of {@link FetchSingle}
	 * @return T instance
	 */
	@SuppressWarnings("unchecked")
	public <T> T fetchSingle(String group, String key, FetchSingle<T> fetcher){
		if(key == null){
			return null;
		}
		Object cachedObj = null;
		try{
			cachedObj = cache.get(prefixedKey(group, key));
		}
		catch(RuntimeException e){
			LOGGER.error("Unable to fetch from cacher: - Group: '"
					+ (group == null ? "" : group)
					+ "' Keys: " + key, e);
		}

		List<String> keys = new ArrayList<String>();
		keys.add(key);
		if(cachedObj == null){
			T obj = fetcher.fetch(key);
			addToCache(prefixedKey(group, key), obj);
			fireFetchedFromFetcherEvent(keys);
			return obj;
		}
		else {
			assertValidType(fetcher.getType(), cachedObj);
			fireFetchedFromCacheEvent(keys);
			return (T)cachedObj;
		}
	}

	/**
	 * Adds the provided key/value to the cacher.
	 */
	private void addToCache(String key, Object value) {
		if(value == null){
			return;
		}

		try{
			cache.set(key, value);
		}
		catch(RuntimeException e){
			//Log it! Otherwise, don't care.
			LOGGER.error("Unable to cacher key " + key + " with value " + value, e);
		}
	}

	/**
	 * Asserts that the passed in Object is of the correct type, otherwise throws a {@link ClassCastException}.
	 */
	private <T> void assertValidType(Class<T> type, Object obj) {
		if(!type.isInstance(obj)){
			throw new ClassCastException("Unable to cast type " + obj.getClass() + " to " + type);
		}
	}

	/**
	 * Fires the fetchedFromCache events.
	 * @param keys - Keys fetched from the cacher.
	 */
	private void fireFetchedFromCacheEvent(List<String> keys){
		if(keys == null || keys.isEmpty()){
			return;
		}
		for(FetchEventListener listener : fetchEventListeners){
			listener.fetchedFromCache(keys);
		}
	}

	/**
	 * Fires the fetchedFromFetcher events.
	 * @param keys - Keys fetched from the fetcher.
	 */
	private void fireFetchedFromFetcherEvent(List<String> keys){
		if(keys == null || keys.isEmpty()){
			return;
		}
		for(FetchEventListener listener : fetchEventListeners){
			listener.fetchedFromFetcher(keys);
		}
	}

}
