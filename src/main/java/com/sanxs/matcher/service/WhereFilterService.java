package com.sanxs.matcher.service;

import com.sanxs.matcher.Where;

import java.util.List;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: Where 条件过滤器
 **/
public interface WhereFilterService<Data> {
    /**
     * 执行过滤
     *
     * @param data
     * @param where
     * @return
     */
    List<Data> apply(List<Data> data, Where<Data> where);
}
