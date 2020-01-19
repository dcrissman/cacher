package cacher.redis;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cacher.Cache;
import redis.clients.jedis.Jedis;

public class RedisCache implements Cache {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);
    
    private final Jedis redisClient;

    public RedisCache(Jedis redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public Object get(String key) {
        return redisClient.get(key);
    }

    @Override
    public Map<String, Object> getBulk(List<String> keys) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void set(String key, Object value) {
        if (value instanceof CharSequence) {
            LOGGER.warn("Passed in Object is not a CharSequence. This is fine, just know toString() will be called.");
        }
        redisClient.set(key, value.toString());
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(String key) {
        // TODO Auto-generated method stub

    }

}
