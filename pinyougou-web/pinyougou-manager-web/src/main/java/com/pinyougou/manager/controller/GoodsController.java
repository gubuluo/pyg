package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        /** 添加查询条件 */
        goods.setAuditStatus("0");
        try {
            if (StringUtils.isNoneBlank(goods.getGoodsName())) {
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISo8859-1"), "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goodsService.findByPage(goods, page, rows);
    }

    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateByStatus(ids, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids, String status) {
        try {
            goodsService.deleteByStatus(ids, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
