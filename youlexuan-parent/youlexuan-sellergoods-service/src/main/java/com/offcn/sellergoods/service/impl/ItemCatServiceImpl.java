package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.pojo.TbItemCat;
import com.offcn.pojo.TbItemCatExample;
import com.offcn.pojo.TbItemCatExample.Criteria;
import com.offcn.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 商品类目服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper item_catMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return item_catMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) item_catMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat item_cat) {
		item_catMapper.insert(item_cat);
		//添加分类信息时将redis中的品牌信息全部删除
		redisTemplate.boundHashOps("itemCat").delete();
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat item_cat){
		item_catMapper.updateByPrimaryKey(item_cat);
		//修改分类信息时将redis中的品牌信息全部删除
		redisTemplate.boundHashOps("itemCat").delete();
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return item_catMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			item_catMapper.deleteByPrimaryKey(id);
			//删除分类信息时将redis中的品牌信息全部删除
			redisTemplate.boundHashOps("itemCat").delete();
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat item_cat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(item_cat!=null){			
						if(item_cat.getName()!=null && item_cat.getName().length()>0){
				criteria.andNameLike("%"+item_cat.getName()+"%");
			}	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)item_catMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbItemCat> findByParentId(Long parentId) {
		//查询数据库是否有分类信息的数据，如有则不执行更新redis。
		Set keys = redisTemplate.boundHashOps("itemCat").keys();
		if(keys.size() < 0) {
			//在用户第一次查询分类信息时将数据放入redis中，存储为“分类名：模板ID”
			List<TbItemCat> itemCats = findAll();
			for (TbItemCat itemCat : itemCats) {
				redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
			}
		}
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		return item_catMapper.selectByExample(example);
	}

}
