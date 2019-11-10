package com.offcn.search.service;

import java.util.Map;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/7 10:20
 */
public interface ItemSearchService {

    /**
     * 主页的搜索功能
     */
    Map<String, Object> search(Map searchMap);
}
