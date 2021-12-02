
package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.core.metadata.OrderItem;

import java.util.List;

/**
 * 简单分页模型 DTO 用于解决跨服务数据传输问题，不影响 Page 作为返回对象序列化 JSON 产生不必要的数据
 *
 * @author hubin
 * @since 2021-05-20
 */
public class PageDTO<T> extends Page<T> {

    public PageDTO() {
    }

    public PageDTO(long current, long size) {
        this(current, size, 0);
    }

    public PageDTO(long current, long size, long total) {
        this(current, size, total, true);
    }

    public PageDTO(long current, long size, boolean searchCount) {
        this(current, size, 0, searchCount);
    }

    public PageDTO(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    @Override
    public String getCountId() {
        return this.countId;
    }

    @Override
    public Long getMaxLimit() {
        return this.maxLimit;
    }

    @Override
    public List<OrderItem> getOrders() {
        return this.orders;
    }

    @Override
    public boolean isOptimizeCountSql() {
        return this.optimizeCountSql;
    }

    @Override
    public boolean isSearchCount() {
        return this.searchCount;
    }

    /* --------------- 以下为静态构造方式 --------------- */
    public static <T> Page<T> of(long current, long size) {
        return of(current, size, 0);
    }

    public static <T> Page<T> of(long current, long size, long total) {
        return of(current, size, total, true);
    }

    public static <T> Page<T> of(long current, long size, boolean searchCount) {
        return of(current, size, 0, searchCount);
    }

    public static <T> Page<T> of(long current, long size, long total, boolean searchCount) {
        return new PageDTO<>(current, size, total, searchCount);
    }
}
