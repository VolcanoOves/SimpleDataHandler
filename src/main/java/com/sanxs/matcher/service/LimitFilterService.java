package com.sanxs.matcher.service;

import com.sanxs.matcher.Limit;
import com.sanxs.matcher.OrderBy;

import java.util.List;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 分页处理器
 **/
public interface LimitFilterService<Data> {
    /**
     * 执行过滤
     *
     * @param data  数据
     * @param limit 分页对象
     * @return 排序之后的结果
     */
    List<Data> apply(List<Data> data, Limit limit);
}
