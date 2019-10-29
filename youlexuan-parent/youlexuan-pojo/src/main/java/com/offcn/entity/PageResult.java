package com.offcn.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description： 分页数据，将数据总数和分页数据返回前端页面
 * @date ：2019/10/23 16:15
 */
public class PageResult implements Serializable {

    private static final long serialVersionUID = -2204754721687887088L;
    private long total;
    private List rows;

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
