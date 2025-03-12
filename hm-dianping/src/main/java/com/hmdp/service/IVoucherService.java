package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Voucher;

public interface IVoucherService{

    Result queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
