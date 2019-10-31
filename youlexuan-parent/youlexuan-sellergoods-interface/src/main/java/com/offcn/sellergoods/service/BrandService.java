package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/10/22 16:42
 */
public interface BrandService {

    /**
     * 返回全部列表
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 返回分页数据
     * @param page 本页的页码
     * @param rows 本页的数据条数
     * @return 返回查询到的本页数据和数据库中的数据总条数
     */
    PageResult findPage(int page, int rows);

    /**
     * 保存品牌信息
     * @param brand 从前端传来，需要保存的信息
     */
    void saveBrand(TbBrand brand);

    /**
     * 修改品牌信息
     * @param brand 从前端传来的修改后的数据。
     * @return 返回值有两个属性，success表示是否保存成功，message输出提示
     */
    void updateBrand(TbBrand brand);

    /**
     * 根据数据id值批量删除数据
     * @param selectIds 需要删除的数据的id值
     */
    void deleteBrand(List<Long> selectIds);

    /**
     * 根据id查询对应的数据。结果只有一个。
     * @param id 需要查询的数据的id值
     * @return 查询到的数据，结果只有一个
     */
    TbBrand findOne(long id);

    /**
     * 返回分页数据
     * @param page 本页的页码
     * @param rows 本页的数据条数
     * @param brand 本次查询的条件
     * @return 返回查询到的本页数据和数据库中的数据总条数
     */
    PageResult searchBrand(int page, int rows, TbBrand brand);

    /**
     * 品牌下拉框数据
     */
    List<Map> selectOptionList();
}
