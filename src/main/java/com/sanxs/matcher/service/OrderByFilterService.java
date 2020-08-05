package com.sanxs.matcher.service;

import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.OrderBy;

import java.util.List;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: OrderBy处理器
 **/
public interface OrderByFilterService<Data> {
    /**
     * 排序罗辑
     *
     * @param data    数据
     * @param orderBy 排序对象
     * @param groupBy 分组端详
     */
    void apply(List<Data> data, OrderBy<Data> orderBy, GroupBy<Data, ?> groupBy);
}
