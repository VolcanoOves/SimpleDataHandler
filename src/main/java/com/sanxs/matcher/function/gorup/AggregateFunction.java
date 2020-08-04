package com.sanxs.matcher.function.gorup;

import java.io.Serializable;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 对聚合结果进行赋值
 */
@FunctionalInterface
public interface AggregateFunction<GroupData, AggregateResult> extends Serializable {
    void apply(GroupData groupData, AggregateResult result);
}
