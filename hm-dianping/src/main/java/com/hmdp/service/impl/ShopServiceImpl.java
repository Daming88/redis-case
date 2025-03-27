package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopRepository;
import com.hmdp.service.IShopService;
import com.hmdp.utils.ToolUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.hmdp.utils.RedisConstants.*;
import static java.util.concurrent.TimeUnit.MINUTES;


@Service
public class ShopServiceImpl implements IShopService {

    @Autowired
    ShopRepository shopRepository;

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public Result queryShopById(Long id) {
        Map<Object, Object> shopMap = redisTemplate.opsForHash().entries(CACHE_SHOP_KEY + id);
        if (!shopMap.isEmpty()) {
            if (shopMap.get("id")!=null){
                Shop shop = BeanUtil.fillBeanWithMap(shopMap, new Shop(), false);
                return Result.ok(shop);
            }else {
                return Result.fail("店铺不存在");
            }
        }

        // 不存在
        Shop shop = shopRepository.findById(id).orElse(null);
        if (shop == null) {
            redisTemplate.opsForHash().put(CACHE_SHOP_KEY + id, "id",null);
            redisTemplate.expire(CACHE_SHOP_KEY + id, CACHE_NULL_TTL, MINUTES);
            return Result.fail("店铺不存在");
        }
        // 存在,写入Redis
        Map<String, Object> targetShopMap = ToolUtil.beanToMap(shop);
        redisTemplate.opsForHash().putAll(CACHE_SHOP_KEY + id, targetShopMap);
        redisTemplate.expire(CACHE_SHOP_KEY + id, CACHE_SHOP_TTL, MINUTES);
        return Result.ok(shop);
    }

    @Transactional
    @Override
    public Result updateShop(Shop shop) {
        Long id = shop.getId();
        if (id == null){
            return Result.fail("店铺id不能为空");
        }
        // 跟新数据库，删除缓存
        Shop shop1 = shopRepository.findById(id).get();
        BeanUtil.copyProperties(shop, shop1, CopyOptions.create().setIgnoreNullValue(true));
        shopRepository.save(shop1);
        redisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return Result.ok();
    }
}
