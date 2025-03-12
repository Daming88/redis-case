package com.hmdp.mapper;

import com.hmdp.entity.VoucherOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, Long>, JpaSpecificationExecutor<VoucherOrder> {

}
