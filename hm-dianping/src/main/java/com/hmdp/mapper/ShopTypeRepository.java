package com.hmdp.mapper;

import com.hmdp.entity.ShopType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopTypeRepository extends JpaRepository<ShopType, Long>, JpaSpecificationExecutor<ShopType> {

}
