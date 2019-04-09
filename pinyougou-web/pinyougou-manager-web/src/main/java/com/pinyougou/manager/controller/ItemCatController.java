package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.support.Parameter;
import com.pinyougou.common.PageResult;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference(timeout = 10000)
    private ItemCatService itemCatService;

    @GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        System.out.println("parentid = " + parentId);
        List<ItemCat> itemCatByParentId = itemCatService.findItemCatByParentId(parentId);
        return itemCatByParentId;
    }
}
