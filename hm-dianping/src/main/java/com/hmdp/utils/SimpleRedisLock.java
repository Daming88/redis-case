package com.hmdp.utils;

import cn.hutool.core.lang.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{

    private static final String LOCK_PREFIX = "lock:";
    private static final String ID_PREFIX = UUID.randomUUID().toString(true)+"-";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    private String name;
    private RedisTemplate redisTemplate;

    public SimpleRedisLock(String name, RedisTemplate redisTemplate) {
        this.name = name;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        // 获取线程的标示
        String threadId = ID_PREFIX+Thread.currentThread().getId();
        // 获取锁
        Boolean success = redisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unLock() {
        // 执行lua脚本
        redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(LOCK_PREFIX + name), ID_PREFIX+Thread.currentThread().getId());
    }

//    @Override
//    public void unLock() {
//        // 获取线程标示
//        String threadId = ID_PREFIX+Thread.currentThread().getId();
//        // 获取所锁中的标示
//        String id = (String) redisTemplate.opsForValue().get(LOCK_PREFIX + name);
//        if (threadId.equals(id)) {
//            redisTemplate.delete(LOCK_PREFIX + name);
//        }
//    }
}
