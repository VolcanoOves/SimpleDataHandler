package com.sanxs.matcher.function.gorup.aggregates;

import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import com.sanxs.matcher.function.gorup.AggregateFunction;
import com.sanxs.matcher.function.gorup.GetFieldFunction;
import com.sanxs.utils.FunctionUtils;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangshan
 * @Date: 2020/8/5
 * @Description: 聚合平均
 **/
@NoArgsConstructor
public class Avg<Data, GroupData extends Data> extends AbstractGroupByAggregateHandler<Data, GroupData, Integer, Double> {

    private Long sum = 0L;
    private Integer count = 0;

    public Avg(GetFieldFunction<Data, Integer> in, AggregateFunction<GroupData, Double> out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void handle(Data data) {
        Object value = FunctionUtils.getFieldValue(in, data);
        if (value != null) {
            sum += Long.parseLong(value.toString());
            count++;
        }
    }

    @Override
    public void aggregate(GroupData groupData) {
        FunctionUtils.setFieldValue(out, groupData, count != 0 ? (double) (sum / count) : null);
    }
}
