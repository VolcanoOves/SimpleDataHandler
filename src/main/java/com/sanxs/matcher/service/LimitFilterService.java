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
     * @param data
     * @param limit
     * @return
     */
    List<Data> apply(List<Data> data, Limit limit);
}
