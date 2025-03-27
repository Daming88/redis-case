package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeRepository;
import com.hmdp.service.IShopTypeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;


@Service
public class ShopTypeServiceImpl implements IShopTypeService {

    @Resource
    private ShopTypeRepository shopTypeRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Result queryTypeList() {
        List<ShopType> shopType = redisTemplate.opsForList().range(CACHE_SHOP_TYPE_KEY, 0, -1);

        if (!shopType.isEmpty()){
            return Result.ok(shopType);
        }

        List<ShopType> typeList = shopTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "sort"));

        if (typeList.size() == 0){
            return Result.fail("查询失败");
        }

        redisTemplate.opsForList().leftPushAll(CACHE_SHOP_TYPE_KEY, typeList);

        return Result.ok(typeList);
    }
}
