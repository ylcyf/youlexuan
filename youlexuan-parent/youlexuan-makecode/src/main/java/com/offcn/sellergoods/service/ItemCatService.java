package com.offcn.sellergoods.service;
import java.util.List;
import com.offcn.pojo.TbItemCat;

import com.offcn.entity.PageResult;
/**
 * 商品类目服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService {

	/**
	 * 返回全部列表
	 * @return
	 */
	List<TbItemCat> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	void add(TbItemCat item_cat);
	
	
	/**
	 * 修改
	 */
	void update(TbItemCat item_cat);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	TbItemCat findOne(Long id);
	
	
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
	PageResult findPage(TbItemCat item_cat, int pageNum,int pageSize);
	
}
