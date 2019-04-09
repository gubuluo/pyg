package com.pinyougou.service;

import com.pinyougou.common.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;

import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * SpecificationService 服务接口
 * @date 2019-03-28 18:44:56
 * @version 1.0
 */
public interface SpecificationService {

	/** 添加方法 */
	void save(Specification specification);

	/** 修改方法 */
	void update(Specification specification);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Specification findOne(Serializable id);

	/** 查询全部 */
	List<Specification> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(Specification specification, int page, int rows);

	List<SpecificationOption> findOneOption(Long id);

    List<Map<String,Object>> findSpecList();
}