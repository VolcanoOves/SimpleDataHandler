package com.sanxs.matcher.function.gorup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Function;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: OrderBy聚合函数
 **/
@Getter
@Setter
public abstract class AbstractGroupByAggregateHandler<Data, GroupData extends Data, InType, OutType> {

    protected GetFieldFunction<Data, InType> in;
    protected AggregateFunction<GroupData, OutType> out;


    /**
     * for 处理
     *
     * @param data
     */
    public abstract void handle(Data data);

    /**
     * 输出聚合结果
     *
     * @param groupData
     * @return
     */
    public abstract void aggregate(GroupData groupData);

}
