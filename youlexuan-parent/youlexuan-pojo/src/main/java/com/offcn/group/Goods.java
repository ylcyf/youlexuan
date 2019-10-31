package com.offcn.group;

import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;

import java.util.List;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/10/29 21:13
 */
public class Goods {
    private TbGoods goods;
    private TbGoodsDesc goodsDesc;
    private List<TbItem> itemList;

    public Goods() {
    }

    public Goods(TbGoods goods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
        this.goods = goods;
        this.goodsDesc = goodsDesc;
        this.itemList = itemList;
    }

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
