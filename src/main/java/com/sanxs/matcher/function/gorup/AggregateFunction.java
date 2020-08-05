package com.sanxs.matcher.function.gorup;

import java.io.Serializable;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 定义setXxx(Object x)的lambda表达式接口函数
 */
@FunctionalInterface
public interface AggregateFunction<GroupData, AggregateResult> extends Serializable {
    /**
     * 定义setXxx(Object x)的lambda表达式接口函数
     *
     * @param groupData 需要赋值的对象类型
     * @param result    需要赋值的对象字段
     */
    void apply(GroupData groupData, AggregateResult result);
}
