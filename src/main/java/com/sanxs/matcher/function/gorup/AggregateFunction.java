package com.sanxs.matcher.function.gorup;

import java.io.Serializable;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 对聚合结果进行赋值 lambda 表达式将赋值函数取出，合适的时候赋值
 */
@FunctionalInterface
public interface AggregateFunction<GroupData, AggregateResult> extends Serializable {
    /**
     * 对聚合结果进行赋值 lambda 表达式将赋值函数取出，合适的时候赋值
     *
     * @param groupData 赋值对象的lambda表达式
     * @param result    对应的数值
     */
    void apply(GroupData groupData, AggregateResult result);
}
