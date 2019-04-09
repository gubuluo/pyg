package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.PageResult;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.SpecificationService")
@Transactional
public class specificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public void save(Specification specification) {
        try {
            specificationMapper.insertSelective(specification);
            specificationOptionMapper.save(specification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Specification specification) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            specificationOptionMapper.deleteAll(ids);
            specificationMapper.deleteAll(ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Specification findOne(Serializable id) {
       return null;
    }

    @Override
    public List<Specification> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Specification specification, int page, int rows) {
        PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                specificationMapper.findAll(specification);
            }
        });
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public List<SpecificationOption> findOneOption(Long id) {
        List<SpecificationOption> specificationOptions = specificationOptionMapper.findOne(id);
        return specificationOptions;
    }

    @Override
    public List<Map<String,Object>> findSpecList() {
        return specificationMapper.findSpecList();
    }
}
