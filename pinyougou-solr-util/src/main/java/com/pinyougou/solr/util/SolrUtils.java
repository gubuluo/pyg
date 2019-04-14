package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemMapper itemMapper;

    public void save(){
        Item item = new Item();
        item.setStatus("1");
        List<Item> itemList = itemMapper.selectAll();
        List<SolrItem> solrItemList = new ArrayList<>();
        for (Item item1 : itemList) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setTitle(item1.getTitle());
            solrItem.setPrice(item1.getPrice());
            solrItem.setImage(item1.getImage());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setCategory(item1.getCategory());
            solrItem.setBrand(item1.getBrand());
            solrItem.setSeller(item1.getSeller());
            solrItem.setUpdateTime(item1.getUpdateTime());
            /** 将spec字段的json字符串转换成map */
            Map specMap = JSON.parseObject(item1.getSpec(), Map.class);
            /** 设置动态域 */
            solrItem.setSpecMap(specMap);

            solrItemList.add(solrItem);
        }

        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItemList);
        if(updateResponse.getStatus() == 0){
            //提交事务
            solrTemplate.commit();
        }else{
            //回滚事务
            solrTemplate.rollback();
        }
    }

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrUtils solrUtils = ac.getBean(SolrUtils.class);
        solrUtils.save();
    }
}
