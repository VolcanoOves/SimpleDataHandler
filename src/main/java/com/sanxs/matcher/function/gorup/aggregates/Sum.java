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
public class Sum<Data, GroupData extends Data> extends AbstractGroupByAggregateHandler<Data, GroupData, Long, Long> {

    private Long sum = 0L;


    public Sum(GetFieldFunction<Data, Long> in, AggregateFunction<GroupData, Long> out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void handle(Data data) {
        Object value = FunctionUtils.getFieldValue(in, data);
        if (value != null) {
            sum = sum + Long.parseLong(value.toString());
        }
    }

    @Override
    public void aggregate(GroupData groupData) {
        FunctionUtils.setFieldValue(out, groupData, sum);
    }
}
