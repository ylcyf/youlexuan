package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/6 21:49
 */
@Component
public class SolrUtil {

    @Autowired(required = false)
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    private void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> items = itemMapper.selectByExample(example);
        for (TbItem item : items) {

            //将item的spec字段转换为map格式，并赋值给Solr动态字段。
            Map map = JSON.parseObject(item.getSpec(), Map.class);

            item.setSpecMap(map);

            System.out.println(item.getTitle());
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil=  (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
