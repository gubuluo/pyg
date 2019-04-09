package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * TypeTemplateMapper 数据访问接口
 * @date 2019-03-28 18:44:02
 * @version 1.0
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate>{


    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

    void deleteAll(Serializable[] ids);
}