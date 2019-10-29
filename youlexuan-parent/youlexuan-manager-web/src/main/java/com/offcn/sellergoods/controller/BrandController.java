package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/10/22 16:56
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    // @reference也是注入，但是一般用来注入分布式的远程服务对象，需要配合dubbo配置使用
    @Reference
    private BrandService brandService;
    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 返回分页数据
     * @param page 本页的页码
     * @param rows 本页的数据条数
     * @return 返回查询到的本页数据和数据库中的数据总条数
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows){
        PageResult pages = brandService.findPage(page, rows);
        System.out.println(pages);
        return pages;
    }

    /**
     * 保存品牌信息
     * @param brand 从前端传来，需要保存的信息
     * @return 返回值有两个属性，success表示是否保存成功，message输出提示
     */
    @RequestMapping("/add")
    public Result addBrand(@RequestBody TbBrand brand){
        try{
            brandService.saveBrand(brand);
            return new Result(true, "保存成功");
        }catch (Exception e){
            return new Result(false, "保存失败");
        }
    }

    /**
     * 修改品牌信息
     * @param brand 从前端传来的修改后的数据。
     * @return 返回值有两个属性，success表示是否保存成功，message输出提示
     */
    @RequestMapping("/update")
    public Result updateBrand(@RequestBody TbBrand brand){
        try{
            brandService.updateBrand(brand);
            return new Result(true, "修改成功");
        }catch (Exception e){
            return new Result(false, "修改失败");
        }
    }

    /**
     * 根据数据id值批量删除数据
     * @param selectIds 需要删除的数据的id值
     * @return 返回值有两个属性，success表示是否保存成功，message输出提示
     */
    @RequestMapping("/delete")
    public Result deleteBrand(@RequestBody List<Long> selectIds){
        try{
            brandService.deleteBrand(selectIds);
            return new Result(true, "修改成功");
        }catch (Exception e){
            return new Result(false, "修改失败");
        }
    }

    /**
     * 根据id查询对应的数据。结果只有一个。
     * @param id 需要查询的数据的id值
     * @return 查询到的数据，结果只有一个
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }

    /**
     * 返回分页数据
     * @param page 本页的页码
     * @param rows 本页的数据条数
     * @param searchEntity 本次查询的条件
     * @return 返回查询到的本页数据和数据库中的数据总条数
     */
    @RequestMapping("/search")
    public PageResult searchBrand(int page, int rows, @RequestBody TbBrand searchEntity){
        return brandService.searchBrand(page, rows, searchEntity);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
