package com.hmdp.service;


import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;

public interface IShopService{

    Result queryShopById(Long id);

    Result updateShop(Shop shop);
}
