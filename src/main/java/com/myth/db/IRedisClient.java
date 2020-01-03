package com.myth.db;

import com.google.inject.ImplementedBy;

@ImplementedBy(RedisClient.class)
public interface IRedisClient {
    <T>T get(String key, Class<T> clazz);
    void set(String key, Object value);
    void set(String key, Object value, int seconds);
    long delete(String key);
    boolean exist(String key);
}
