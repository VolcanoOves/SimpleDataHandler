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

    /**
     * 验证数据是否有效
     *
     * @param data  数据
     * @param where where 对象
     * @return 筛选结果
     */
    boolean validation(Data data, Where<Data> where);
}
