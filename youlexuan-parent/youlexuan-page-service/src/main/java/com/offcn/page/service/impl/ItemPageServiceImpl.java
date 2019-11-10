package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/10 20:06
 */
@Service(timeout = 4000)
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {
        System.out.println(goodsId + "========");
        try {
            //加载freemarker的文件数据，并将字符集设置为utf-8
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            //创建map集合并将查到的goods数据存入map集合中
            Map map = new HashMap();
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goods", goods);
            map.put("goodsDesc", goodsDesc);
            //用writer流将文件输出
            Writer writer = new FileWriter(pagedir + goodsId + ".html");
            template.process(map, writer);
            writer.close();
            return true;
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return false;
        }
    }
}
