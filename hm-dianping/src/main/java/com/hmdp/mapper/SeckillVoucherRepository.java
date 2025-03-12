package com.hmdp.mapper;

import com.hmdp.entity.SeckillVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SeckillVoucherRepository extends JpaRepository<SeckillVoucher, Long>, JpaSpecificationExecutor<SeckillVoucher> {

}
