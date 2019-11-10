package com.offcn.page.service;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/10 19:55
 */
public interface ItemPageService {

    /**
     * 根据商品的id值生成商品详情的静态页面
     */
    boolean genItemHtml(Long goodsId);
}
