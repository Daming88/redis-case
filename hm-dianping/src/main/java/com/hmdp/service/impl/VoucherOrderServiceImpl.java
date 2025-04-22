package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.SeckillVoucherRepository;
import com.hmdp.mapper.VoucherOrderRepository;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @Override
    public Result seckillVoucher(Long voucherId) {


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
