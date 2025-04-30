package com.hmdp.listen;

import com.hmdp.entity.SeckillVoucher;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Component
@CanalTable("tb_seckill_voucher")
public class SeckillVoucherHandler implements EntryHandler<SeckillVoucher> {

    @Override
    public void insert(SeckillVoucher seckillVoucher) {
        System.out.println("insert");
        EntryHandler.super.insert(seckillVoucher);
    }

    @Override
    public void update(SeckillVoucher before, SeckillVoucher after) {
        System.out.println("update");
        EntryHandler.super.update(before, after);
    }

    @Override
    public void delete(SeckillVoucher seckillVoucher) {
        System.out.println("delete");
        EntryHandler.super.delete(seckillVoucher);
    }
}
