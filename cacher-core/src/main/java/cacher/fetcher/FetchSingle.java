package cacher.fetcher;

/**
 * Implementations of this interface know how to fetch a single data-point
 * from the primary data store.
 * 
 * @author Dennis Crissman
 *
 * @param <T>
 */
public interface FetchSingle<T> {

	/**
	 * @return the type class
	 */
	Class<T> getType();

	/**
	 * Fetches a single data-point from the primary data store.
	 * @param keys - key that need to be fetched.
	 * @return fetched data point.
	 */
	T fetch(String key);

}
