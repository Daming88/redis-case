package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.Voucher;
import com.hmdp.mapper.SeckillVoucherRepository;
import com.hmdp.mapper.VoucherRepository;
import com.hmdp.service.IVoucherService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements IVoucherService {

    @Resource
    private SeckillVoucherRepository seckillVoucherRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Override
    public Result queryVoucherOfShop(Long shopId) {
        // 查询优惠券信息
        List<Voucher> vouchers = voucherRepository.findAllByShopId(shopId);
        // 返回结果
        vouchers=vouchers.stream().map(item->{
            Optional<SeckillVoucher> seckillVoucher = seckillVoucherRepository.findById(item.getId());
            if (seckillVoucher.isPresent()) {
                item.setStock(seckillVoucher.get().getStock());
                item.setBeginTime(seckillVoucher.get().getBeginTime());
                item.setEndTime(seckillVoucher.get().getEndTime());
            }
            return item;
        }).collect(Collectors.toList());
        return Result.ok(vouchers);
    }

    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        voucher.setCreateTime(LocalDateTime.now() );
        voucher.setUpdateTime(LocalDateTime.now() );
        voucher.setStatus(1);
        voucherRepository.save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucher.setCreateTime(LocalDateTime.now() );
        seckillVoucher.setUpdateTime(LocalDateTime.now() );
        seckillVoucherRepository.save(seckillVoucher);
    }
}
