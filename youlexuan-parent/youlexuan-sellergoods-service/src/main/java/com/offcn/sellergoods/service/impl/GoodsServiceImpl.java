package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	@Autowired
    private SolrTemplate solrTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);
	}

	/**
	 * 重载增加方法
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());

		saveItemList(goods);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}

	/**
	 * 重载修改方法
	 */
	@Override
	public void update(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goods.getGoods().setIsDelete("0");
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		//删除所有goodsId为本商品的item
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        List<TbItem> items = itemMapper.selectByExample(example);
        List<String> itemId = new ArrayList<>();
        for (TbItem item : items) {
            itemId.add(item.getId() + "");
        }
        //删除solr中的索引
        solrTemplate.deleteById(itemId);
        solrTemplate.commit();

        itemMapper.deleteByExample(example);


		//新增
		saveItemList(goods);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		return new Goods(tbGoods, tbGoodsDesc, tbItems);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//将数组转换为List集合
		List<Long> list = new ArrayList<>();
		Collections.addAll(list, ids);

		//修改goods表数据为已删除
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(list);
		List<TbGoods> goods = goodsMapper.selectByExample(example);
		for (TbGoods good : goods) {
			good.setIsDelete("1");
			good.setAuditStatus("3");
			goodsMapper.updateByPrimaryKey(good);
		}

		//修改item表数据为未启用
		TbItemExample example1 = new TbItemExample();
		TbItemExample.Criteria criteria1 = example1.createCriteria();
		criteria1.andGoodsIdIn(list);
		criteria1.andStatusEqualTo("1");
		List<TbItem> items = itemMapper.selectByExample(example1);
		List<String> itemId = new ArrayList<>();
		for (TbItem item : items) {
			item.setStatus("0");
			itemMapper.updateByPrimaryKey(item);
            itemId.add(item.getId() + "");
        }
        //删除solr中的索引
        solrTemplate.deleteById(itemId);
        solrTemplate.commit();

		/*//批量删除TBGoods数据
		TbGoodsExample goodsExample = new TbGoodsExample();
		Criteria criteria = goodsExample.createCriteria();
		criteria.andIdIn(list);
		goodsMapper.deleteByExample(goodsExample);

		//批量删除TBGoodsDesc数据
		TbGoodsDescExample goodsDescExample = new TbGoodsDescExample();
		TbGoodsDescExample.Criteria criteria1 = goodsDescExample.createCriteria();
		criteria1.andGoodsIdIn(list);
		goodsDescMapper.deleteByExample(goodsDescExample);

		//批量删除TbItem数据
		TbItemExample itemExample = new TbItemExample();
		TbItemExample.Criteria criteria2 = itemExample.createCriteria();
		criteria2.andGoodsIdIn(list);
		itemMapper.deleteByExample(itemExample);*/
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		parseNullTo0();
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andIsDeleteNotEqualTo("1");
		if(goods!=null){
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
		}

		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize, String name) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andSellerIdEqualTo(name);

		if(goods!=null){
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 修改状态 1为审核通过，2为驳回
	 */
	@Override
	public void updateStatus(List<Long> ids, String status) {
		//修改goods表的AuditStatus属性
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(ids);
		List<TbGoods> goods = goodsMapper.selectByExample(example);
		for (TbGoods good : goods) {
			good.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(good);
		}
		//审核通过则更新solr中的索引
		TbItemExample itemExample = new TbItemExample();
		TbItemExample.Criteria criteria1 = itemExample.createCriteria();
		criteria1.andGoodsIdIn(ids);
		criteria1.andStatusEqualTo("1");
		List<TbItem> items = itemMapper.selectByExample(itemExample);
		if(status.equals("1")) {
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
		}
	}

	private void saveItemList(Goods goods) {
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {

			// 添加库存
			for (TbItem item : goods.getItemList()) {
				// 标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods, item);

				itemMapper.insert(item);
			}
		} else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());
			item.setPrice(goods.getGoods().getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(99999);
			item.setSpec("{}");
			setItemValus(goods, item);
			itemMapper.insert(item);
		}
	}

	private void setItemValus(Goods goods, TbItem item) {
		item.setGoodsId(goods.getGoods().getId());// 商品spu编号
		item.setSellerId(goods.getGoods().getSellerId());// 商家名
		item.setCategoryid(goods.getGoods().getCategory3Id());// 商品分类编号
		item.setCreateTime(new Date());// 创建日期
		item.setUpdateTime(new Date());// 修改日期

		// 品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		// 分类名
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		// 商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());

		// 图片地址(多张图片获取第一个)
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (imageList.size() > 0) {
			item.setImage((String) imageList.get(0).get("url"));
		}
	}

	//将goods表的isDelete中的null转换为0
	private void parseNullTo0(){
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		List<TbGoods> goods = goodsMapper.selectByExample(example);
		for (TbGoods good : goods) {
			good.setIsDelete("0");
			goodsMapper.updateByPrimaryKey(good);
		}
	}

}
