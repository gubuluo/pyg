package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Goods;

import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 * @date 2019-03-28 18:44:02
 * @version 1.0
 */
public interface GoodsMapper extends Mapper<Goods>{


    List<Map<String,Object>> findAll(Goods goods);

    /** 传两个参数的时候需要用@Param注解指定映射的参数名 */
    void updateByStatus(@Param("ids")Long[] ids, @Param("status")String status);

    void deleteByStatus(@Param("ids") Long[] ids, @Param("status") String status);

    void updateMarketable(@Param("ids") Long[] ids, @Param("status") String status);
}