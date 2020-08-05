package com.sanxs.matcher.function.gorup.aggregates;

import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import com.sanxs.matcher.function.gorup.AggregateFunction;
import com.sanxs.matcher.function.gorup.GetFieldFunction;
import com.sanxs.utils.FunctionUtils;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangshan
 * @Date: 2020/8/5
 * @Description: 聚合取总和
 **/
@NoArgsConstructor
public class Sum<Data, GroupData extends Data> extends AbstractGroupByAggregateHandler<Data, GroupData, Number, Double> {

    private Double sum = 0d;

    public Sum(GetFieldFunction<Data, Number> in, AggregateFunction<GroupData, Double> out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void handle(Data data) {
        Object value = FunctionUtils.getFieldValue(in, data);
        if (value != null) {
            sum = sum + Double.parseDouble(value.toString());
        }
    }

    @Override
    public void aggregate(GroupData groupData) {
        FunctionUtils.setFieldValue(out, groupData, sum);
    }
}
