package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.GoodsDesc;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;


    @Override
    public void save(Goods goods) {
        try {
            // 设置未申核状态
            goods.setAuditStatus("0");
            goodsMapper.insertSelective(goods);
            // 为商品描述对象设置主键id，这里注意顺序不能颠倒，否则取不到getId
            GoodsDesc goodsDesc = goods.getGoodsDesc();
            goodsDesc.setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goods.getGoodsDesc());

            //往tb_item表中插入数据（SKU）
            for (Item item : goods.getItems()) {
                /** 定义SKU商品的标题 */
                StringBuilder title = new StringBuilder();
                title.append(goods.getGoodsName());
                /** 把规格选项JSON字符串转化成Map集合 */
                Map<String, Object> spec = JSON.parseObject(item.getSpec());
                for (Object value : spec.values()) {
                    /** 拼接规格选项到SKU商品标题 */
                    title.append(" " + value);
                }
                /** 设置SKU商品的标题 */
                item.setTitle(title.toString());
                /** 设置SKU商品图片地址 */
                List<Map> imageList = JSON.parseArray(
                        goods.getGoodsDesc().getItemImages(), Map.class);
                if (imageList != null && imageList.size() > 0) {
                    /** 取第一张图片 */
                    item.setImage((String) imageList.get(0).get("url"));
                }
                /** 设置SKU商品的分类(三级分类) */
                item.setCategoryid(goods.getCategory3Id());
                /** 设置SKU商品的创建时间 */
                item.setCreateTime(new Date());
                /** 设置SKU商品的修改时间 */
                item.setUpdateTime(item.getCreateTime());
                /** 设置SPU商品的编号 */
                item.setGoodsId(goods.getId());
                /** 设置商家编号 */
                item.setSellerId(goods.getSellerId());
                /** 设置商品分类名称 */
                item.setCategory(itemCatMapper
                        .selectByPrimaryKey(goods.getCategory3Id()).getName());
                /** 设置品牌名称 */
                item.setBrand(brandMapper
                        .selectByPrimaryKey(goods.getBrandId()).getName());
                /** 设置商家店铺名称 */
                item.setSeller(sellerMapper.selectByPrimaryKey(
                        goods.getSellerId()).getNickName());

                itemMapper.insertSelective(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Goods goods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Goods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Goods> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Goods goods, int page, int rows) {
        try{
            /** 开始分页 */
            PageInfo<Map<String,Object>> pageInfo =
                    PageHelper.startPage(page, rows)
                            .doSelectPageInfo(new ISelect() {
                                @Override
                                public void doSelect() {
                                    goodsMapper.findAll(goods);
                                }
                            });
            /** 循环查询到的商品 */
            for (Map<String,Object> map : pageInfo.getList()){
                ItemCat itemCat1 =
                        itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");
                ItemCat itemCat2 =
                        itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");
                ItemCat itemCat3 =
                        itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateByStatus(Long[] ids, String status) {
        try {
            goodsMapper.updateByStatus(ids, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByStatus(Long[] ids, String status) {
        try {
            goodsMapper.deleteByStatus(ids, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateMarketable(Long[] ids, String status) {
        try {
            goodsMapper.updateMarketable(ids, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
