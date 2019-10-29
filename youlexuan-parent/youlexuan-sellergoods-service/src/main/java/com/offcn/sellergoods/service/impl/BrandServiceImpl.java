package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/10/22 16:54
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 返回分页数据
     *
     * @param page 本页的页码
     * @param rows 本页的数据条数
     * @return 返回查询到的本页数据和数据库中的数据总条数
     */
    @Override
    public PageResult findPage(int page, int rows) {
        PageHelper.startPage(page, rows);
        Page<TbBrand> pages = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(pages.getTotal(), pages.getResult());
    }

    /**
     * 保存品牌信息
     *
     * @param brand 从前端传来，需要保存的信息
     */
    @Override
    public void saveBrand(TbBrand brand) {
        int insert = brandMapper.insert(brand);
        if (insert < 1) {
            throw new RuntimeException("保存失败");
        }
    }

    /**
     * 修改品牌信息
     *
     * @param brand 从前端传来的修改后的数据。
     * @return 返回值有两个属性，success表示是否保存成功，message输出提示
     */
    @Override
    public void updateBrand(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 根据数据id值批量删除数据
     *
     * @param selectIds 需要删除的数据的id值
     */
    @Override
    public void deleteBrand(List<Long> selectIds) {
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(selectIds);
        int i = brandMapper.deleteByExample(example);
        if (i < selectIds.size()) {
            throw new RuntimeException("删除失败");
        }
    }

    /**
     * 根据id查询对应的数据。结果只有一个。
     *
     * @param id 需要查询的数据的id值
     * @return 查询到的数据，结果只有一个
     */
    @Override
    public TbBrand findOne(long id) {
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        return brandMapper.selectByExample(example).get(0);
    }

    /**
     * 返回分页数据
     *
     * @param page  本页的页码
     * @param rows  本页的数据条数
     * @param brand 本次查询的条件
     * @return 返回查询到的本页数据和数据库中的数据总条数
     */
    @Override
    public PageResult searchBrand(int page, int rows, TbBrand brand) {
        TbBrandExample example = new TbBrandExample();
        if (brand != null) {
            TbBrandExample.Criteria criteria = example.createCriteria();
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        PageHelper.startPage(page, rows);
        Page<TbBrand> pages = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(pages.getTotal(), pages.getResult());
    }

    /**
     * 列表数据
     */
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
