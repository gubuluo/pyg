package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        try {
            /** 获取检索关键字 */
            String keywords = (String) params.get("keywords");
            /** 获取当前页码 */
            Integer page = (Integer) params.get("page");
            if(page == null){
                //如果没有当前页码，就设置当前页码为1
                page = 1;
            }
            /** 获取页大小 */
            Integer rows = (Integer) params.get("rows");
            if (rows == null) {
                //设置默认页大小
                rows = 10;
            }
            /** 判断检索关键字 */
            if (StringUtils.isNoneBlank(keywords)) {
                /** 创建高亮查询对象*/
                HighlightQuery highlightQuery = new SimpleHighlightQuery();

                /** 创建高亮选项对象（封装高亮显示所需要的设置信息） */
                HighlightOptions highlightOptions = new HighlightOptions();
                /** 设置高亮域 */
                highlightOptions.addField("title");
                /** 设置高亮前缀 */
                highlightOptions.setSimplePrefix("<font color = 'red'>");
                /** 设置高亮后缀 */
                highlightOptions.setSimplePostfix("</font>");

                /** 设置高亮选项 */
                highlightQuery.setHighlightOptions(highlightOptions);

                /** 创建查询条件 */
                Criteria criteria = new Criteria("keywords").is(keywords);//is()可实现分词
                /** 查询对象添加查询条件 */
                highlightQuery.addCriteria(criteria);

                /** 按商品分类过滤 */
                String category = (String) params.get("category");
                if(StringUtils.isNoneBlank(category)){
                    //创建条件对象，在这里的is()不会做分词
                    Criteria criteria1 = new Criteria("category").is(category);
                    //添加过滤查询,实现过滤查询
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                /** 按品牌过滤 */
                String brand = (String) params.get("brand");
                if(StringUtils.isNoneBlank(brand)){
                    //创建查询对象
                    Criteria criteria1 = new Criteria("brand").is(brand);
                    //添加过滤查询
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                /** 按规格过滤 */
                Map<String,String> specMap = (Map<String, String>) params.get("spec");
                if(specMap != null){
                    for(String key : specMap.keySet()){
                        //创建查询对象
                        Criteria criteria1 = new Criteria("spec_" + key).is(specMap.get(key));
                        //添加过滤查询
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                }
                /** 按价格过滤 */
                String prices = (String) params.get("price");
                if(StringUtils.isNoneBlank(prices)){
                    String[] price = prices.split("-");
                    if(!"0".equals(price[0])){
                        //创建查询对象
                        Criteria criteria1 = new Criteria("price").greaterThanEqual(price[0]);
                        //添加过滤查询
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                    if(!"*".equals(price[1])){
                        //创建查询对象
                        Criteria criteria1 = new Criteria("price").lessThanEqual(price[1]);
                        //添加过滤查询
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                }

                /** 设置起始记录查询数 */
                highlightQuery.setOffset((page - 1) * rows);
                /** 设置页大小 */
                highlightQuery.setRows(rows);

                /** 高亮分页查询 */
                HighlightPage<SolrItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, SolrItem.class);

                /** 获取高亮内容集合 */
                List<HighlightEntry<SolrItem>> highlighted = highlightPage.getHighlighted();
                //循环迭代
                for (HighlightEntry<SolrItem> highlightEntry : highlighted) {
                    //获得一个solrItem
                    SolrItem solrItem = highlightEntry.getEntity();
                    List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                    if(highlights != null && highlights.size() > 0){
                        //设置
                        solrItem.setTitle(highlights.get(0).getSnipplets().get(0));
                    }
                }
                /** 创建Map集合封装返回数据 **/
                Map<String, Object> data = new HashMap<>();

                data.put("rows", highlightPage.getContent());
                data.put("totalPages", highlightPage.getTotalPages());
                data.put("total", highlightPage.getTotalElements());

                System.out.println("totalPages=" + highlightPage.getTotalPages());

                return data;

            } else {
                /** 创建Map集合封装返回数据 **/
                Map<String, Object> data = new HashMap<>();
                /** 创建查询对象 */
                Query query = new SimpleQuery("*:*");
                /** 设置起始记录查询数 */
                query.setOffset((page - 1) * rows);
                /** 设置页大小 */
                query.setRows(rows);
                /** 得到分页分数对象 */
                ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
                /** 获取搜索结果getContent() */
                data.put("rows", scoredPage.getContent());
                data.put("totalPages", scoredPage.getTotalPages());
                data.put("total", scoredPage.getTotalElements());
                return data;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
