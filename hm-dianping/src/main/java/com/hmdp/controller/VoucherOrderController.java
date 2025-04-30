package com.hmdp.controller;


import com.hmdp.dto.Result;
import com.hmdp.service.IVoucherOrderService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    IVoucherOrderService voucherOrderService;

    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) throws InterruptedException {

        return voucherOrderService.seckillVoucher(voucherId);
    }

//    @Resource
//    RedissonClient  redissonClient;
//
//    @GetMapping("/testLock1")
//    public String testLock(){
//        String city = "huaqiao";
//        String type = "patient";
//
//        // 获取一个名为"myLock"的分布式锁
//        RLock lock = redissonClient.getLock("myLock");
//        try {
//            System.out.println("当前锁状态"+Thread.currentThread().getName()+lock.isLocked());
//            if (!lock.isLocked()){
//                // 尝试获取锁，这里可以设置等待时间和锁的过期时间
//                System.out.println("尝试获取到锁");
//                boolean isSuccess = lock.tryLock(0, 60, java.util.concurrent.TimeUnit.SECONDS);
//                System.out.println("获取锁: " + isSuccess);
//                // 获取到锁，执行临界区代码
//                System.out.println(Thread.currentThread().getName()+"成功获取到锁，执行临界区代码");
//                // 模拟业务逻辑处理
//                Thread.sleep(10000);
//            } else {
//                // 未获取到锁
//                System.out.println(Thread.currentThread().getName()+"未能获取到锁");
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            // 释放锁
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//                System.out.println("锁已释放");
//            }
//        }
//        System.out.println(lock);
//        return "lock";
//    }

}
