package com.hmdp.controller;


import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopRepository;
import com.hmdp.service.IShopService;
import com.hmdp.utils.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    IShopService shopService;

    @Autowired
    ShopRepository shopRepository;

    /**
     * 根据id查询商铺信息
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {
        return shopService.queryShopById(id);
    }

    /**
     * 新增商铺信息
     * @param shop 商铺数据
     * @return 商铺id
     */
    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {
        // 写入数据库
        shopRepository.save(shop);
        // 返回店铺id
        return Result.ok(shop.getId());
    }

    /**
     * 更新商铺信息
     * @param shop 商铺数据
     * @return 无
     */
    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        // 写入数据库
        return shopService.updateShop(shop);
    }

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId 商铺类型
     * @param current 页码
     * @return 商铺列表
     */
    @GetMapping("/of/type")
    public Result queryShopByType(@RequestParam("typeId") Integer typeId,
                                  @RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 根据类型分页查询
        Pageable pageable = PageRequest.of(current-1, 5);
        Page<Shop> page =shopRepository.findAllByTypeId(typeId,pageable);
        // 返回数据
        return Result.ok(page.get().collect(Collectors.toList()));
    }

    /**
     * 根据商铺名称关键字分页查询商铺信息
     * @param name 商铺名称关键字
     * @param current 页码
     * @return 商铺列表
     */
    @GetMapping("/of/name")
    public Result queryShopByName(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 根据类型分页查询

        Pageable pageable = PageRequest.of(current, SystemConstants.MAX_PAGE_SIZE);
        Page<Shop> page =shopRepository.findAllByName(name,pageable);
        // 返回数据
        return Result.ok(page.get().collect(Collectors.toList()));
    }
}
