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
	private int cacheExpireSeconds;

	static final Character ESCAPE_CHAR = '&';
	/** Any Character in this Array will be escaped in the cached key. */
	static final Character[] ESCAPABLE_CHARS = new Character[]{' '};

	/**
	 * @param connections - list of memcached instances to connect too.
	 * @param timeout - the entry expire timeout in seconds
	 * @throws IOException
	 */
	public MemcachedCache(List<InetSocketAddress> connections, int timeout) throws IOException {
		this(new MemcachedClient(connections), timeout);
	}

	/**
	 * @param client - {@link MemcachedCache}
	 * @param timeout - the entry expire timeout in seconds
	 */
	public MemcachedCache(MemcachedClient client, int timeout){
		this.client = client;
		cacheExpireSeconds = timeout;
	}

	public MemcachedClient getClient(){
		return client;
	}

	@Override
	public Object get(String key) {
		return client.get(encode(key));
	}

	@Override
	public Map<String, Object> getBulk(List<String> keys) {
		List<String> encodedKeys = new ArrayList<String>();
		for(String key : keys){
			encodedKeys.add(encode(key));
		}
		return client.getBulk(encodedKeys);
	}

	@Override
	public void set(String key, Object value) {
		client.set(encode(key), getCacheExpireSeconds(), value);
	}

	/**
	 * Sets the entry expire timeout in seconds.
	 * @param seconds - int
	 */
	public void setCacheExpireSeconds(int seconds) {
		cacheExpireSeconds = seconds;
	}

	/**
	 * @return seconds each entry will expire in.
	 */
	public int getCacheExpireSeconds() {
		return cacheExpireSeconds;
	}

	@Override
	public void clear() {
		client.flush();
	}

	static String encode(String key){
		String encodedKey = key.replaceAll(ESCAPE_CHAR.toString(), ESCAPE_CHAR.toString() + Integer.valueOf(ESCAPE_CHAR));
		for(Character ch : ESCAPABLE_CHARS){
			encodedKey = encodedKey.replaceAll(ch.toString(), ESCAPE_CHAR.toString() + Integer.valueOf(ch));
		}
		return encodedKey;
	}

	static String decode(String key){
		String decodedKey = key;
		for(Character ch : ESCAPABLE_CHARS){
			decodedKey = decodedKey.replaceAll(ESCAPE_CHAR.toString() + Integer.valueOf(ch), ch.toString());
		}
		return decodedKey.replaceAll(ESCAPE_CHAR.toString() + Integer.valueOf(ESCAPE_CHAR), ESCAPE_CHAR.toString());
	}

}
