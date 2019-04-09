package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.PageResult;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Brand brand, int page, int rows) {
        PageInfo<Brand> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                brandMapper.findAll(brand);
            }
        });
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public List<Map<String ,Object>> findAllByIdAndName() {
        return brandMapper.findAllByIdAndName();
    }

    @Override
    public void save(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            /*
            //使用for语句查询效率高，但是每次循环都要查询数据库，性能不高，所以在项目中不推荐使用for
            for (Serializable id : ids) {
                brandMapper.deleteByPrimaryKey(id);
            }
            */
            brandMapper.deleteAll(ids);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Brand findOne(Serializable id) {
        return null;
    }
}
