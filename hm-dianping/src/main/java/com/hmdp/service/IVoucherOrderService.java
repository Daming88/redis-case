package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;

public interface IVoucherOrderService {

    Result seckillVoucher(Long voucherId) throws InterruptedException;

    Result createVoucherOrder(Long voucherId, SeckillVoucher seckillVoucher);
}
