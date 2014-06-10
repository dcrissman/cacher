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

package cacher.memcached.getstrategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.internal.GetFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link GetStrategy} for querying Memcache asynchronously.
 * 
 * @since 1.0.7
 * @author Dennis Crissman
 * @see SyncGetStrategy
 */
public class AsyncGetStrategy implements GetStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGetStrategy.class);

	private static final int DEFAULT_TIMEOUT_SECONDS = 5;

	private int secondsToTimeout;

	/**
	 * Sets the seconds to wait before timing out.
	 * @param secondsToTimeout - seconds to wait before timing out.
	 */
	public void setSecondsToTimeout(int secondsToTimeout){
		this.secondsToTimeout = secondsToTimeout;
	}

	/**
	 * @return seconds to wait before timing out.
	 */
	public int getSecondsToTimeout(){
		return secondsToTimeout;
	}

	/**
	 * Defaults to waiting 5 seconds before timing out.
	 */
	public AsyncGetStrategy(){
		this.secondsToTimeout = DEFAULT_TIMEOUT_SECONDS;
	}

	/**
	 * @param secondsToTimeout - seconds to wait before timing out.
	 */
	public AsyncGetStrategy(int secondsToTimeout){
		this.secondsToTimeout = secondsToTimeout;
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.memcached.GetStrategy#get(java.lang.String)
	 */
	@Override
	public Object get(MemcachedClient client, String key) {
		if(key == null){
			return null;
		}

		GetFuture<Object> future = client.asyncGet(key);
		try{
			return future.get(getSecondsToTimeout(), TimeUnit.SECONDS);
		}
		catch(TimeoutException e){
			LOGGER.warn("Future timed out while waiting for key: " + key, e);
			future.cancel(false);
		}
		catch (ExecutionException e) {
			LOGGER.error("Unexpected error while retrieving key: " + key, e);
		}
		catch (InterruptedException e) {
			//Thread has been interrupted, allow it to exit.
			future.cancel(false);
			Thread.currentThread().interrupt();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.memcached.GetStrategy#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(MemcachedClient client, List<String> keys) {
		if(keys == null || keys.isEmpty()){
			return null;
		}

		BulkFuture<Map<String,Object>> future = client.asyncGetBulk(keys);
		try{
			return future.get(getSecondsToTimeout(), TimeUnit.SECONDS);
		}
		catch(TimeoutException e){
			LOGGER.warn("BulkFuture timed out while waiting for keys: " + keys, e);
			future.cancel(false);
		}
		catch (ExecutionException e) {
			LOGGER.error("Unexpected error while retrieving keys: " + keys, e);
		}
		catch (InterruptedException e) {
			//Thread has been interrupted, allow it to exit.
			future.cancel(false);
			Thread.currentThread().interrupt();
		}

		return null;
	}

}
