package com.offcn.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.content.service.ContentService;
import com.offcn.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/6 15:48
 */
@RestController
@RequestMapping("content")
public class ContentController {
    @Reference
    private ContentService contentService;

    @RequestMapping("findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
}
