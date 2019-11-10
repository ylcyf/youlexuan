package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/7 11:07
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页在Solr中查询item数据
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        //创建返回值Map
        Map<String, Object> map = new HashMap<>();
        //添加查询条件
        /*SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //查询结果
        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);
        map.put("rows", items.getContent());*/
        map.putAll(highList(searchMap));
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);

        if(categoryList.size() > 0){
            map.putAll(findBrandAndSpec((categoryList).get(0)));
        }
        return map;
    }

    //将查询的关键词高亮
    private Map<String, Object> highList(Map searchMap){
        Map<String, Object> map = new HashMap<>();
        //创建高亮查询条件
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //设置高亮的域
        HighlightOptions options = new HighlightOptions().addField("item_title");
        //设置高亮的前缀、后缀
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        //设置查询关键字
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.setHighlightOptions(options);
        query.addCriteria(criteria);

        //判断是否传入品牌信息，如果传入则进一步筛选品牌
        //添加一个SimpleFilterQuery，对查询条件结果进行进一步的过滤，如果直接用SimpleQuery可能因为搜索条件冲突，导致结果出错
        if(searchMap.get("brand") != null && !searchMap.get("brand").equals("")){
            Criteria contains = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(contains);
            query.addFilterQuery(filterQuery);
            //query.addCriteria(contains);
            map.put("brand", searchMap.get("brand"));
        }

        //判断是否传入分类信息，如果传入则进一步筛选分类
        if(searchMap.get("category") != null){
            Criteria contains = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(contains);
            query.addFilterQuery(filterQuery);
            //query.addCriteria(contains);
            map.put("category", searchMap.get("category"));
        }

        //判断是否传入价格信息，如果传入则进一步筛选价格
        if(searchMap.get("price") != null){
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            Criteria contains = new Criteria("item_price");
            if(split[1].equals("*")){
                contains.greaterThanEqual(split[0]);
                searchMap.put("price", "大于" + split[0]);
            }else {
                contains.between(split[0], split[1]);
            }
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(contains);
            query.addFilterQuery(filterQuery);
            //query.addCriteria(contains);
            map.put("price", searchMap.get("price"));
        }

        //判断是否传入规格信息，如果传入则进一步筛选规格
        if(searchMap.get("spec") != null){
            Map<String, String> spec = (Map)searchMap.get("spec");
            for (String key : spec.keySet()) {
                Criteria contains = new Criteria("item_spec_" + key).contains(spec.get(key));
                query.addCriteria(contains);
            }
        }

        //排序，将对结果进行排序
        if(searchMap.get("sort") != null && ((String)searchMap.get("sort")).length() > 0){
            String sortField = (String)searchMap.get("sortField");
            String sort = (String) searchMap.get("sort");
            if(sort.equals("ASC")) {
                Sort asc = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(asc);
            }else {
                Sort desc = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(desc);
            }
        }

        //分页查询，根据分页信息返回分页结果
        int pageNum = Integer.parseInt(searchMap.get("pageNum") + "");
        int pageSize = Integer.parseInt(searchMap.get("pageSize") + "");
        query.setOffset((pageNum -1) * pageSize);
        query.setRows(pageSize);

        HighlightPage<TbItem> items = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //设置高亮入口集合
        for (HighlightEntry<TbItem> entry : items.getHighlighted()) {
            TbItem item = entry.getEntity();
            if(entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0){
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        //返回查询结果
        map.put("rows", items.getContent());
        map.put("totalPages", items.getTotalPages());//返回总页数
        map.put("total", items.getTotalElements());//返回总记录数
        return map;
    }

    //根据分类分组查询出搜索结果的分类信息
    private List<String> searchCategoryList(Map searchMap){
        List<String> list = new ArrayList<>();
        SimpleQuery query = new SimpleQuery();
        //先按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组查询选项
        GroupOptions group = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(group);
        //获取查询结果
        GroupPage<TbItem> items = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果集
        GroupResult<TbItem> groupResult = items.getGroupResult("item_category");
        //获得分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获得分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //循环赋值给list集合
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }
        return list;
    }

    //根据模板ID查询redis中的品牌信息和规格信息
    private Map<String, Object> findBrandAndSpec(String category){
        Map<String, Object> map = new HashMap<>();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        Object brandList = redisTemplate.boundHashOps("brandList").get(typeId);
        Object specList = redisTemplate.boundHashOps("specList").get(typeId);
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }
}
