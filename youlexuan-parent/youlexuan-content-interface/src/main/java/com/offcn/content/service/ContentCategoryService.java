package com.offcn.content.service;
import java.util.List;
import com.offcn.pojo.TbContentCategory;

import com.offcn.entity.PageResult;
/**
 * 内容分类服务层接口
 * @author Administrator
 *
 */
public interface ContentCategoryService {

	/**
	 * 返回全部列表
	 * @return
	 */
	List<TbContentCategory> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	void add(TbContentCategory content_category);
	
	
	/**
	 * 修改
	 */
	void update(TbContentCategory content_category);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	TbContentCategory findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(TbContentCategory content_category, int pageNum,int pageSize);
	
}
