package com.hmdp.mapper;

import com.hmdp.entity.SeckillVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeckillVoucherRepository extends JpaRepository<SeckillVoucher, Long>, JpaSpecificationExecutor<SeckillVoucher> {

    // 减库存
    @Modifying // 标记这是修改操作
    @Query(value = "update tb_seckill_voucher set stock = stock - 1 where voucher_id = ?1 and stock >0", nativeQuery = true)
    int reduceStock(@Param("voucherId") Long voucherId, @Param("stock") Integer stock);
}
