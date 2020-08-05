package com.sanxs.matcher.function.gorup.aggregates;

import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import com.sanxs.matcher.function.gorup.AggregateFunction;
import com.sanxs.matcher.function.gorup.GetFieldFunction;
import com.sanxs.utils.FunctionUtils;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangshan
 * @Date: 2020/8/5
 * @Description: 聚合取计数
 **/
@NoArgsConstructor
public class Count<Data, GroupData extends Data> extends AbstractGroupByAggregateHandler<Data, GroupData, Object, Long> {

    private Long count = 0L;


    public Count(GetFieldFunction<Data, Object> in, AggregateFunction<GroupData, Long> out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void handle(Data data) {
        Object value = FunctionUtils.getFieldValue(in, data);
        if (value != null) {
            count++;
        }
    }

    @Override
    public void aggregate(GroupData groupData) {
        FunctionUtils.setFieldValue(out, groupData, count);
    }
}
