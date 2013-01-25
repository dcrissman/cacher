package cacher;

import java.util.List;
import java.util.Map;

/**
 * Interface representing a simple cacher.
 * 
 * @author Andrew Edwards
 * @author Dennis Crissman
 *
 */
public interface Cache {

	/**
	 * Gets a cached value
	 * @param key - String key
	 * @return Object value
	 */
	Object get(String key);

	/**
	 * Fetches multiple values at once.<br>
	 * <br>
	 * Depending on the implementation, getting multiple values at once may be more
	 * efficient than each one at time.
	 * @param keys - List of keys to fetch
	 * @return Map of fetched key/value pairs.
	 */
	Map<String, Object> getBulk(List<String> keys);

	/**
	 * Sets a value in the cacher
	 * @param key - String key
	 * @param value - Object value
	 */
	void set(String key, Object value);

	/**
	 * Clears all values in the cacher
	 */
	void clear();

}
