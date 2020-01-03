package com.myth.db;

import com.google.inject.Inject;
import com.myth.MobileServerConfiguration;
import com.myth.core.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClient implements IRedisClient {

    private JedisPool jedisPool;
    private String environment;

    @Inject
    public RedisClient(JedisPool jedisPool, MobileServerConfiguration configuration) {
        this.jedisPool = jedisPool;
        this.environment = configuration.getEnvironment();
        if (this.environment != null) {
            this.environment = this.environment.toUpperCase();
        }
    }

    private String getFinalKey(String key) {
        return this.environment+"::"+key;
    }

    public <T>T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String data = jedis.get(this.getFinalKey(key));
            if (data != null) {
                return Json.fromJson(Json.parse(data), clazz);
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, Object value) {
        this.set(key, value, 0);
    }

    public void set(String key, Object value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String strValue = "";
            if (value instanceof String) {
                strValue = (String)value;
            } else {
                strValue = Json.stringify(Json.toJson(value));
            }
            if (seconds > 0) {
                jedis.setex(this.getFinalKey(key), seconds, strValue);
            } else {
                jedis.set(this.getFinalKey(key), strValue);
            }
        } catch (Exception ex) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long delete(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.del(this.getFinalKey(key));
        } catch (Exception ex) {
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean exist(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.exists(this.getFinalKey(key));
        } catch (Exception ex) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }
}
