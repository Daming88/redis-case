package com.hmdp.controller;


import cn.hutool.crypto.symmetric.DES;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeRepository;
import com.hmdp.service.IShopTypeService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private ShopTypeRepository shopTypeRepository;

    @GetMapping("list")
    public Result queryTypeList() {

        List<ShopType> typeList = shopTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "sort"));
        return Result.ok(typeList);
    }
}
