package com.sanxs.matcher.function.gorup;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: Group By 聚合函数处理器
 **/
@Getter
@Setter
public abstract class AbstractGroupByAggregateHandler<Data, GroupData extends Data, InType, OutType> {

    protected GetFieldFunction<Data, InType> in;
    protected AggregateFunction<GroupData, OutType> out;


    /**
     * 数据 for 处理罗辑
     * 根据in的lambda表达式取得data对应字段属性进行聚会和运算
     *
     * @param data 原始数据
     */
    public abstract void handle(Data data);

    /**
     * 输出聚合结果
     * 根据out反射设置groupData中对应字段
     *
     * @param groupData group 结果类型
     */
    public abstract void aggregate(GroupData groupData);

}
