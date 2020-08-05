package com.sanxs.matcher.function.gorup;

import com.sanxs.matcher.function.gorup.aggregates.*;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 常用聚合函数集合
 **/
public class Aggregates {

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> avg(GetFieldFunction<Data, Number> in, AggregateFunction<GroupData, Double> out) {
        return new Avg<>(in, out);
    }

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> max(GetFieldFunction<Data, Long> in, AggregateFunction<GroupData, Long> out) {
        return new Max<>(in, out);
    }

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> count(GetFieldFunction<Data, Object> in, AggregateFunction<GroupData, Long> out) {
        return new Count<>(in, out);
    }

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> min(GetFieldFunction<Data, Long> in, AggregateFunction<GroupData, Long> out) {
        return new Min<>(in, out);
    }

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> sum(GetFieldFunction<Data, Number> in, AggregateFunction<GroupData, Double> out) {
        return new Sum<>(in, out);
    }

}
