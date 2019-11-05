package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.pojo.TbGoods;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	void add(TbGoods goods);

	/**
	 * 重载增加方法
	 */
	void add(Goods goods);


	/**
	 * 修改
	 */
	void update(TbGoods goods);

	/**
	 * 重载修改方法
	 */
	void update(Goods goods);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	Goods findOne(Long id);


	/**
	 * 批量删除
	 * @param ids
	 */
	void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	 * 重载分页方法
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(TbGoods goods, int pageNum, int pageSize, String name);

	/**
	 * 修改goods表的状态，1为审核通过，2为驳回
	 * @param status
	 * @return
	 */
	void updateStatus(List<Long> ids, String status);
}
