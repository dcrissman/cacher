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

package cacher.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.spy.memcached.MemcachedClient;
import cacher.Cache;

/**
 * Implementation of {@link Cache} that uses Memcache.
 * 
 * @author Andrew Edwards
 * @author Dennis Crissman
 */
public class MemcachedCache implements Cache {

	private final MemcachedClient client;
	private int defaultExpireSeconds;

	static final Character ESCAPE_CHAR = '&';
	/** Any Character in this Array will be escaped in the cached key. */
	static final Character[] ESCAPABLE_CHARS = new Character[]{' '};

	/**
	 * @param connections - list of memcached instances to connect too.
	 * @param expiration - the entry expire timeout in seconds
	 * @throws IOException
	 */
	public MemcachedCache(List<InetSocketAddress> connections, int expiration) throws IOException {
		this(new MemcachedClient(connections), expiration);
	}

	/**
	 * @param client - {@link MemcachedCache}
	 * @param expiration - the entry expire timeout in seconds
	 */
	public MemcachedCache(MemcachedClient client, int expiration){
		this.client = client;
		defaultExpireSeconds = expiration;
	}

	/**
	 * @return the underlying {@link MemcachedCache} that is used to communicate with the memcached pool.
	 */
	public MemcachedClient getClient(){
		return client;
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return client.get(encode(key));
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#getBulk(java.util.List)
	 */
	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		List<String> encodedKeys = new ArrayList<String>();
		for(String key : keys){
			encodedKeys.add(encode(key));
		}
		return client.getBulk(encodedKeys);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		set(key, value, getDefaultCacheExpireSeconds());
	}

	/**
	 * Sets a value in the cacher
	 * @param key - String key
	 * @param value - Object value
	 * @param expiration - Seconds to allow the cached key/value pair to live.
	 */
	public void set(String key, Object value, int expiration){
		client.set(encode(key), expiration, value);
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#remove(java.lang.String)
	 */
	@Override
	public void remove(String key) {
		client.delete(key);
	}

	/**
	 * Sets the default entry expire timeout in seconds. This value will be used if one is not otherwise specified.
	 * @param seconds - default seconds to expire entries
	 */
	public void setDefaultCacheExpireSeconds(int seconds) {
		defaultExpireSeconds = seconds;
	}

	/**
	 * @return default seconds each entry will expire in.
	 */
	public int getDefaultCacheExpireSeconds() {
		return defaultExpireSeconds;
	}

	/*
	 * (non-Javadoc)
	 * @see cacher.Cache#clear()
	 */
	@Override
	public void clear() {
		client.flush();
	}

	/**
	 * Certain characters are invalid for usage in the key, this method will scan a key for any such characters and escape them.
	 * @param key - raw key
	 * @return escaped key
	 */
	static String encode(String key){
		String encodedKey = key.replaceAll(ESCAPE_CHAR.toString(), ESCAPE_CHAR.toString() + Integer.valueOf(ESCAPE_CHAR));
		for(Character ch : ESCAPABLE_CHARS){
			encodedKey = encodedKey.replaceAll(ch.toString(), ESCAPE_CHAR.toString() + Integer.valueOf(ch));
		}
		return encodedKey;
	}

	/**
	 * Decodes escaped keys
	 * @param key - encoded key
	 * @return raw key
	 */
	static String decode(String key){
		String decodedKey = key;
		for(Character ch : ESCAPABLE_CHARS){
			decodedKey = decodedKey.replaceAll(ESCAPE_CHAR.toString() + Integer.valueOf(ch), ch.toString());
		}
		return decodedKey.replaceAll(ESCAPE_CHAR.toString() + Integer.valueOf(ESCAPE_CHAR), ESCAPE_CHAR.toString());
	}

}
