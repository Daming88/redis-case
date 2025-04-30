package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.SeckillVoucherRepository;
import com.hmdp.mapper.VoucherOrderRepository;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.SimpleRedisLock;
import com.hmdp.utils.UserHolder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VoucherOrderServiceImpl implements IVoucherOrderService {

    private static final Object LOCK = new Object();

    @Resource
    SeckillVoucherRepository seckillVoucherRepository;

    @Resource
    VoucherOrderRepository voucherOrderRepository;

    @Resource
    RedisIdWorker redisIdWorker;

    @Autowired
    RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public Result seckillVoucher(Long voucherId) throws InterruptedException {

        return getResult5(voucherId);

    }

    // 悲观锁
    public Result getResult2(Long voucherId) {
        synchronized (LOCK) {
            // 根据id查询优惠券
            Optional<SeckillVoucher> seckillVoucherOptional = seckillVoucherRepository.findById(voucherId);
            if (!seckillVoucherOptional.isPresent()) {
                return Result.fail("优惠券不存在");
            }
            SeckillVoucher seckillVoucher = seckillVoucherOptional.get();
            // 判断时间是否开启
            if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
                return Result.fail("优惠券尚未开始");
            }
            // 判断时间是否结束
            if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
                return Result.fail("优惠券已经结束");
            }

            // 判断库存是否充足
            Optional<SeckillVoucher> seckillVoucher1 = seckillVoucherRepository.findById(voucherId);
            if (!seckillVoucher1.isPresent()) {
                return Result.fail("优惠券不存在");
            }
            SeckillVoucher freshSeckill = seckillVoucher1.get();
            if (freshSeckill.getStock() < 1) {
                return Result.fail("优惠券已经售完");
            }
            freshSeckill.setStock(freshSeckill.getStock() - 1);
            seckillVoucherRepository.save(freshSeckill);
            // 创建订单
            UserDTO user = UserHolder.getUser();
            VoucherOrder voucherOrder = new VoucherOrder();
            voucherOrder.setId(redisIdWorker.nextId("order"));
            voucherOrder.setUserId(user.getId());
            voucherOrder.setVoucherId(voucherId);
            voucherOrder.setPayType(1);
            voucherOrder.setStatus(1);
            voucherOrder.setCreateTime(LocalDateTime.now());
            voucherOrder.setPayTime(LocalDateTime.now());
            voucherOrder.setUpdateTime(LocalDateTime.now());
            voucherOrderRepository.save(voucherOrder);
            return Result.ok(voucherOrder.getId());
        }
    }

    // 乐观锁
    @Transactional
    public Result getResult1(Long voucherId) {

        UserDTO user = UserHolder.getUser();
        // 根据id查询优惠券
        Optional<SeckillVoucher> seckillVoucherOptional = seckillVoucherRepository.findById(voucherId);
        if (!seckillVoucherOptional.isPresent()) {
            return Result.fail("优惠券不存在");
        }
        SeckillVoucher seckillVoucher = seckillVoucherOptional.get();
        // 判断时间是否开启
        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("优惠券尚未开始");
        }
        // 判断时间是否结束
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("优惠券已经结束");
        }

        if (seckillVoucher.getStock() < 1) {
            return Result.fail("优惠券已经售完");
        }

        // 扣减库存
        int result = seckillVoucherRepository.reduceStock(voucherId, seckillVoucher.getStock());
        if (result == 0) {
            return Result.fail("优惠券已经售完");
        }

        // 创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(redisIdWorker.nextId("order"));
        voucherOrder.setUserId(user.getId());
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setPayType(1);
        voucherOrder.setStatus(1);
        voucherOrder.setCreateTime(LocalDateTime.now());
        voucherOrder.setPayTime(LocalDateTime.now());
        voucherOrder.setUpdateTime(LocalDateTime.now());
        voucherOrderRepository.save(voucherOrder);
        return Result.ok(voucherOrder.getId());
    }

    // 一人一单,悲观锁
    public Result getResult3(Long voucherId) {

        // 根据id查询优惠券
        Optional<SeckillVoucher> seckillVoucherOptional = seckillVoucherRepository.findById(voucherId);
        if (!seckillVoucherOptional.isPresent()) {
            return Result.fail("优惠券不存在");
        }
        SeckillVoucher seckillVoucher = seckillVoucherOptional.get();
        // 判断时间是否开启
        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("优惠券尚未开始");
        }
        // 判断时间是否结束
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("优惠券已经结束");
        }

        if (seckillVoucher.getStock() < 1) {
            return Result.fail("优惠券已经售完");
        }

        UserDTO user = UserHolder.getUser();
        synchronized (user.getId().toString().intern()) {
            // 获取代理对象(事务)
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId, seckillVoucher);
        }

    }

    // 一人一单,分布式锁
    public Result getResult4(Long voucherId) {

        // 根据id查询优惠券
        Optional<SeckillVoucher> seckillVoucherOptional = seckillVoucherRepository.findById(voucherId);
        if (!seckillVoucherOptional.isPresent()) {
            return Result.fail("优惠券不存在");
        }
        SeckillVoucher seckillVoucher = seckillVoucherOptional.get();
        // 判断时间是否开启
        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("优惠券尚未开始");
        }
        // 判断时间是否结束
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("优惠券已经结束");
        }

        if (seckillVoucher.getStock() < 1) {
            return Result.fail("优惠券已经售完");
        }

        UserDTO user = UserHolder.getUser();
        SimpleRedisLock redisLock = new SimpleRedisLock("order:" + user.getId(), redisTemplate);

        boolean isLock = redisLock.tryLock(10000);
        if (!isLock) {
            // 获取锁失败，返回错误或重试
            return Result.fail("不允许重复下单");
        }

        try {
            // 获取代理对象(事务)
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId, seckillVoucher);
        } finally {
            redisLock.unLock();
        }
    }

    // 一人一单,Redission分布式锁
    public Result getResult5(Long voucherId) throws InterruptedException {

        // 根据id查询优惠券
        Optional<SeckillVoucher> seckillVoucherOptional = seckillVoucherRepository.findById(voucherId);
        if (!seckillVoucherOptional.isPresent()) {
            return Result.fail("优惠券不存在");
        }
        SeckillVoucher seckillVoucher = seckillVoucherOptional.get();
        // 判断时间是否开启
        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("优惠券尚未开始");
        }
        // 判断时间是否结束
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("优惠券已经结束");
        }

        if (seckillVoucher.getStock() < 1) {
            return Result.fail("优惠券已经售完");
        }

        UserDTO user = UserHolder.getUser();
        RLock lock = redissonClient.getLock("lock:order:" + user.getId());

        boolean isLock = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!isLock) {
            // 获取锁失败，返回错误或重试
            return Result.fail("不允许重复下单");
        }

        try {
            // 获取代理对象(事务)
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId, seckillVoucher);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public Result createVoucherOrder(Long voucherId, SeckillVoucher seckillVoucher) {
        // 一人一单
        UserDTO user = UserHolder.getUser();

        List<VoucherOrder> count = voucherOrderRepository.findByuserIdAndVoucherId(user.getId(), voucherId);
        if (!count.isEmpty()) {
            return Result.fail("用户已经购买过一次了！");
        }

        // 扣减库存
        int result = seckillVoucherRepository.reduceStock(voucherId, seckillVoucher.getStock());
        if (result == 0) {
            return Result.fail("优惠券已经售完");
        }

        // 创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(redisIdWorker.nextId("order"));
        voucherOrder.setUserId(user.getId());
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setPayType(1);
        voucherOrder.setStatus(1);
        voucherOrder.setCreateTime(LocalDateTime.now());
        voucherOrder.setPayTime(LocalDateTime.now());
        voucherOrder.setUpdateTime(LocalDateTime.now());
        voucherOrderRepository.save(voucherOrder);
        return Result.ok(voucherOrder.getId());

    }


    // 添加事务模板注入
//    @Resource
//    private PlatformTransactionManager transactionManager;
//
//    private TransactionTemplate transactionTemplate;
//
//    @PostConstruct
//    public void init() {
//        transactionTemplate = new TransactionTemplate(transactionManager);
//    }
//
//    @Override
//    public Result seckillVoucher(Long voucherId) {
//
//        synchronized (LOCK){
//            return transactionTemplate.execute(status -> {
//                try{
//                    // 根据id查询优惠券
//                    Optional<SeckillVoucher> seckillVoucherOptional = seckillVoucherRepository.findById(voucherId);
//                    if (!seckillVoucherOptional.isPresent()) {
//                        return Result.fail("优惠券不存在");
//                    }
//                    SeckillVoucher seckillVoucher = seckillVoucherOptional.get();
//                    // 判断时间是否开启
//                    if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
//                        return Result.fail("优惠券尚未开始");
//                    }
//                    // 判断时间是否结束
//                    if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
//                        return Result.fail("优惠券已经结束");
//                    }
//
////        synchronized (LOCK){
////            // 判断库存是否充足
////            SeckillVoucher freshSeckill = seckillVoucherRepository.findById(voucherId).get();
////            if (freshSeckill.getStock()<1){
////                return Result.fail("优惠券已经售完");
////            }
////            freshSeckill.setStock(freshSeckill.getStock()-1);
////            seckillVoucherRepository.save(freshSeckill);
////        }
//
//                    if (seckillVoucher.getStock() < 1) {
//                        return Result.fail("优惠券已经售完");
//                    }
//                    seckillVoucher.setStock(seckillVoucher.getStock() - 1);
//                    seckillVoucherRepository.save(seckillVoucher);
//
//                    // 创建订单
//                    UserDTO user = UserHolder.getUser();
//                    VoucherOrder voucherOrder = new VoucherOrder();
//                    voucherOrder.setId(redisIdWorker.nextId("order"));
//                    voucherOrder.setUserId(user.getId());
//                    voucherOrder.setVoucherId(voucherId);
//                    voucherOrder.setPayType(1);
//                    voucherOrder.setStatus(1);
//                    voucherOrder.setCreateTime(LocalDateTime.now());
//                    voucherOrder.setPayTime(LocalDateTime.now());
//                    voucherOrder.setUpdateTime(LocalDateTime.now());
//                    voucherOrderRepository.save(voucherOrder);
//                    return Result.ok(voucherOrder.getId());
//                }catch (Exception e){
//                    return Result.fail("下单失败");
//                }
//
//            });
//        }
//    }
}
