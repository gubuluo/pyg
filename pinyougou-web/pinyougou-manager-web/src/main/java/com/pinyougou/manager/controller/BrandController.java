package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.PageResult;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController  //相当于@Controller + @ResponseBody
@RequestMapping("/brand")
public class BrandController {

    @Reference(timeout = 1000)
    private BrandService brandService;

    @GetMapping("/findByPage")  //相当于@RequestMapping(method = RequestMethod.GET)
    public PageResult findByPage(Brand brand, Integer page, Integer rows) {
        if (brand != null && StringUtils.isNoneBlank(brand.getName())) {
            try {
                brand.setName(new String(brand.getName().getBytes("ISO8859-1"), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return brandService.findByPage(brand, page, rows);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand) {
        try {
            if (brand.getName() != null && brand.getFirstChar() != null) {
                brandService.save(brand);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {

            brandService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* 查询品牌下拉列表 */
    @GetMapping("/findBrandList")
    public List<Map<String,Object>> findBrandList() {
        List<Map<String,Object>> list = brandService.findAllByIdAndName();
        System.out.println(list);
        return list;
    }
}
