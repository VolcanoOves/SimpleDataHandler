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
     * 执行过滤
     *
     * @param data
     * @param orderBy
     * @param groupBy
     */
    void apply(List<Data> data, OrderBy<Data> orderBy, GroupBy<Data,?> groupBy);
}
