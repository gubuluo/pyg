package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @PostMapping("/save")
    public boolean save(@RequestBody Goods goods) {
        try {
            String sellerId = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            /** 设置商家ID */
            goods.setSellerId(sellerId);
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);
        if(StringUtils.isNoneBlank(goods.getGoodsName())){
            try {
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return goodsService.findByPage(goods, page, rows);
    }

    @GetMapping("/updateMarketable")
    public boolean updateMarketable(Long[] ids, String status) {
        try {
            goodsService.updateMarketable(ids, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
