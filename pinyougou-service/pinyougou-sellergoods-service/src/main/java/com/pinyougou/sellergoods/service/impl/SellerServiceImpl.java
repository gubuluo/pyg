package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.PageResult;
import com.pinyougou.mapper.SellerMapper;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SellerService")
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public void save(Seller seller) {
        try {
            //设置状态码，初始状态为0
            seller.setStatus("0");
            seller.setCreateTime(new Date());
            sellerMapper.insertSelective(seller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Seller seller) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Seller findOne(Serializable id) {
        return sellerMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Seller> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Seller seller, int page, int rows) {
            PageInfo<Seller> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    sellerMapper.findByPage(seller);
                }
            });
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        try {
            Seller seller = new Seller();
            seller.setStatus(status);
            seller.setSellerId(sellerId);
            sellerMapper.updateStatus(seller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
