package com.hmdp.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {

    /**
     * 开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1640995200L;
    // 序列号的位数
    private static final int COUNT_BIT = 32;


    private RedisTemplate redisTemplate;

    public RedisIdWorker(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long nextId(String keyPrefix) {
        // 1、生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;
        // 2、生成序列号
        long count = redisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        // 3、拼接返回
        return timestamp << COUNT_BIT | count;
    }


}
