package com.hmdp;

import com.hmdp.utils.RedisIdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
class HmDianPingApplicationTests {


    @Resource
    RedisIdWorker redisIdWorker;

    private ExecutorService es= Executors.newFixedThreadPool(500);

    @Test
    public void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);

        Runnable task=()->{
            for (int i = 0; i < 100; i++) {
                long order = redisIdWorker.nextId("order");
                System.out.println("id="+order);
            }
            latch.countDown();
        };

        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time="+(end-begin));
    }

}
