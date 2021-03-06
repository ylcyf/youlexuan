package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbSeller;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SellerService {

	/**
	 * 返回全部列表
	 * @return
	 */
	List<TbSeller> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	void add(TbSeller seller);


	/**
	 * 修改
	 */
	void update(TbSeller seller);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	TbSeller findOne(String sellerId);

	/**
	 * 批量删除
	 * @param ids
	 */
	void delete(String[] sellerIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(TbSeller seller, int pageNum, int pageSize);

	//审核通过
	void adopt(TbSeller seller);

	//关闭商家
	void closeSeller(TbSeller seller);
}
