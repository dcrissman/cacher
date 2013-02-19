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

package cacher.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cacher.Cache;

/**
 * Enables this system to remain stable if the remove cacher is for whatever reason unavailable.
 * 
 * TODO: Decide if this implementation is truly useful. Possibly refactor.
 * 
 * @author Dennis Crissman
 */
public class RemoteCacheWrapper implements Cache {

	/**
	 * Listens for connections being established and broken.
	 * @author dcrissman
	 */
	public interface ConnectionListener{

		/**
		 * Connection was established to the remote cacher.
		 */
		void connectionEstablished();

		/**
		 * Remote cacher connection was broken.
		 * @param e - {@link Exception} that was given.
		 */
		void connectionBroken(Exception e);
	}

	/**
	 * Represents a class that knows how to instantiate the connection.
	 * @author dcrissman
	 */
	public interface CacheServiceInitializer{

		/**
		 * Instantiated and connected {@link Cache}.
		 * @return instance of {@link Cache}.
		 * @throws IOException
		 */
		Cache createCacheInstance() throws IOException;

	}

	private Cache cache;
	/** Indicates if the cacher is currently in null cacher mode. */
	private boolean isNullCacheMode = true;
	private final NullCache nullCache = new NullCache();

	private final CacheServiceInitializer initializer;
	private final List<ConnectionListener> connectionListeners = new ArrayList<RemoteCacheWrapper.ConnectionListener>();

	public RemoteCacheWrapper(CacheServiceInitializer initializer){
		if(initializer == null){
			throw new IllegalArgumentException("CacheServiceInitializer cannot be null!");
		}

		this.initializer = initializer;
		establishConnection();
	}

	/**
	 * Returns the wrapped {@link Cache} instance.
	 * @return {@link Cache}.
	 */
	public Cache getServiceCache(){
		return cache;
	}

	/**
	 * Adds a {@link ConnectionListener}.
	 * @param listener - {@link ConnectionListener} to add.
	 */
	public void addConnectionListener(ConnectionListener listener){
		connectionListeners.add(listener);
	}

	/**
	 * Removes a {@link ConnectionListener}.
	 * @param listener - {@link ConnectionListener} to remove.
	 */
	public void removeConnectionListener(ConnectionListener listener){
		connectionListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		establishConnection();
		try{
			return cache.get(key);
		}
		catch(Exception e){
			brokenConnection(e);
			return nullCache.get(key);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		establishConnection();
		try{
			return cache.getBulk(keys);
		}
		catch(Exception e){
			brokenConnection(e);
			return nullCache.getBulk(keys);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		establishConnection();
		try{
			cache.set(key, value);
		}
		catch(Exception e){
			brokenConnection(e);
			nullCache.set(key, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#clear()
	 */
	@Override
	public void clear() {
		establishConnection();
		try{
			cache.clear();
		}
		catch(Exception e){
			brokenConnection(e);
			nullCache.clear();
		}
	}

	/**
	 * Ensures that a valid connection has been established, and continues to monitor the
	 * availability of the remote cacher.
	 */
	private void establishConnection(){
		if(isNullCacheMode){
			try{
				//TODO Disconnect?
				cache = initializer.createCacheInstance();
				isNullCacheMode = false;
				for(ConnectionListener listener : connectionListeners){
					listener.connectionEstablished();
				}
			}
			catch(Exception e){
				brokenConnection(e);
			}
		}
	}

	/**
	 * Performs the duty of changing the cacher state to null mode.
	 * @param e - {@link Exception} that fired.
	 */
	private void brokenConnection(Exception e) {
		isNullCacheMode = true;
		cache = nullCache;
		for(ConnectionListener listener : connectionListeners){
			listener.connectionBroken(e);
		}
	}

}
