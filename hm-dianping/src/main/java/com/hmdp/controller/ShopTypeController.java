package com.hmdp.controller;


import cn.hutool.crypto.symmetric.DES;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeRepository;
import com.hmdp.service.IShopTypeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {


    @Autowired
    IShopTypeService shopTypeService;

    @GetMapping("list")
    public Result queryTypeList() {
        return shopTypeService.queryTypeList();
    }
}
