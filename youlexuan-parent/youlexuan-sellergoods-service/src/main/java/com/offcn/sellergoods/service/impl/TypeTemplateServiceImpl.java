package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.mapper.TbTypeTemplateMapper;
import com.offcn.pojo.*;
import com.offcn.pojo.TbTypeTemplateExample.Criteria;
import com.offcn.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper type_templateMapper;
	@Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return type_templateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) type_templateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate type_template) {
		type_templateMapper.insert(type_template);
		//删除redis中的品牌以及规格信息
		redisTemplate.delete("brandList");
		redisTemplate.delete("specList");
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate type_template){
		type_templateMapper.updateByPrimaryKey(type_template);
		//删除redis中的品牌以及规格信息
		redisTemplate.delete("brandList");
		redisTemplate.delete("specList");
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return type_templateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			type_templateMapper.deleteByPrimaryKey(id);
		}
		//删除redis中的品牌以及规格信息
		redisTemplate.delete("brandList");
		redisTemplate.delete("specList");
	}
	
	
	@Override
	public PageResult findPage(TbTypeTemplate type_template, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(type_template!=null){			
						if(type_template.getName()!=null && type_template.getName().length()>0){
				criteria.andNameLike("%"+type_template.getName()+"%");
			}			if(type_template.getSpecIds()!=null && type_template.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+type_template.getSpecIds()+"%");
			}			if(type_template.getBrandIds()!=null && type_template.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+type_template.getBrandIds()+"%");
			}			if(type_template.getCustomAttributeItems()!=null && type_template.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+type_template.getCustomAttributeItems()+"%");
			}	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)type_templateMapper.selectByExample(example);
		saveRedis();
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return type_templateMapper.selectOptionList();
	}

	@Override
	public List<Map> findSpecList(Long id) {
		TbTypeTemplate tbTypeTemplate = type_templateMapper.selectByPrimaryKey(id);
		List<Map> list = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
		for(Map map : list){
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
            List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
            map.put("options", options);
        }
		return list;
	}

	//将模板中的spec和brand数据存入reids。
	private void saveRedis(){
		//获取所有的模板
		List<TbTypeTemplate> typeTemplates = findAll();
		//获取模板中的品牌信息和规格信息，并且将品牌信息与规格信息以模板id为key值，存入redis缓存中。
		for (TbTypeTemplate typeTemplate : typeTemplates) {
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
			List<Map> specList = findSpecList(typeTemplate.getId());
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
		}
	}
}