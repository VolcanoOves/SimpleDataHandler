package com.sanxs.matcher.service;

import com.sanxs.matcher.GroupBy;

import java.util.List;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 分组处理罗辑
 **/
public interface GroupByFilterService<Data> {

    /**
     * group 过滤器
     *
     * @param data    原始数据
     * @param groupBy group罗辑
     */
    <GroupData extends Data> List<Data> apply(List<Data> data, GroupBy<Data, GroupData> groupBy);
}
