package com.hmdp.mapper;

import com.hmdp.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {

    Page<Shop> findAllByTypeId(Integer typeId, Pageable pageable);

    Page<Shop> findAllByName(String name, Pageable pageable);
}
