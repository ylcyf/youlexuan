package com.offcn.solrutil;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/9 16:05
 */
public class SolrTest {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 删除全部
     */
    @Test
    public void delALl(){
        /*SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
        System.out.println("删除成功");*/
    }
}
