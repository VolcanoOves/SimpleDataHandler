package com.sanxs.intf;

import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.Where;

import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
public interface DataHandler<T> {

    /**
     * 按照筛选条件输出筛选之后的结果
     *
     * @param data    原始数据
     * @param where   where 筛选条件
     * @param orderBy 排序
     * @param groupBy 分组
     * @param limit   分页
     * @return 筛选结果
     */
    List<T> query(List<T> data, Where<T> where, OrderBy<T> orderBy, GroupBy<T, ?> groupBy, Limit limit);
}
