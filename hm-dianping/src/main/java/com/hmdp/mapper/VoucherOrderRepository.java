package com.hmdp.mapper;

import com.hmdp.entity.VoucherOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, Long>, JpaSpecificationExecutor<VoucherOrder> {

    List<VoucherOrder> findByuserIdAndVoucherId(@Param("userId") Long userId, @Param("voucherId") Long voucherId);
}
