package com.nhn.util;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisCache {
    private final RedisTemplate redisTemplate;

    public <T> void setCacheObject(final String key, final T value) {
        this.redisTemplate.opsForValue().set(key, value);
    }


    public <T> void setCacheObject(final String key, final T value,
                                   final Integer timeout,
                                   final TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public boolean expire(final String key, final long timeout) {
        return this.expire(key, timeout, TimeUnit.SECONDS);
    }


    public boolean expire(final String key, final long timeout,
                          final TimeUnit unit) {
        return this.redisTemplate.expire(key, timeout, unit);
    }

    public <T> T getCacheObject(final String key) {
        final ValueOperations<String, T> operation = this.redisTemplate.opsForValue();
        return operation.get(key);
    }

    public boolean deleteObject(final String key) {
        return this.redisTemplate.delete(key);
    }

    public long deleteObject(final Collection collection) {
        return this.redisTemplate.delete(collection);
    }

    public <T> long setCacheList(final String key, final List<T> dataList) {
        final Long count = this.redisTemplate.opsForList()
                .rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    public <T> List<T> getCacheList(final String key) {
        return this.redisTemplate.opsForList().range(key, 0, -1);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(final String key,
                                                         final Set<T> dataSet) {
        final BoundSetOperations<String, T> setOperation = this.redisTemplate.boundSetOps(key);
        final Iterator<T> it           = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    public <T> BoundSetOperations<String, T> setCacheSet(final String key,
                                                         final Set<T> dataSet,
                                                         final Integer timeout,
                                                         final TimeUnit timeUnit) {
        final BoundSetOperations<String, T> setOperation = this.redisTemplate.boundSetOps(key);
        final Iterator<T>                   it           = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        this.expire(key, timeout, timeUnit);
        return setOperation;
    }

    public <T> Set<T> getCacheSet(final String key) {
        return this.redisTemplate.opsForSet().members(key);
    }

    public <T> void setCacheMap(final String key,
                                final Map<String, T> dataMap) {
        if (dataMap != null) {
            this.redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    public <T> Map<String, T> getCacheMap(final String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    public <T> void setCacheMapValue(final String key, final String hKey,
                                     final T value) {
        this.redisTemplate.opsForHash().put(key, hKey, value);
    }

    public <T> T getCacheMapValue(final String key, final String hKey) {
        final HashOperations<String, String, T> opsForHash = this.redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    public <T> List<T> getMultiCacheMapValue(final String key,
                                             final Collection<Object> hKeys) {
        return this.redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public Collection<String> keys(final String pattern) {
        return this.redisTemplate.keys(pattern);
    }

    public boolean hasKey(final Object key) {
        return this.redisTemplate.hasKey(key);
    }

    public Long getExpire(final String pattern) {
        return this.redisTemplate.getExpire(pattern);
    }
}