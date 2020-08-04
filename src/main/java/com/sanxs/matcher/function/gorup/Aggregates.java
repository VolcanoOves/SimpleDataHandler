package com.sanxs.matcher.function.gorup;

import com.sanxs.utils.FunctionUtils;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description:
 **/
public class Aggregates {

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> avg(GetFieldFunction<Data, Integer> in, AggregateFunction<GroupData, Double> out) {
        return new Avg<>(in, out);
    }

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> max(GetFieldFunction<Data, Long> in, AggregateFunction<GroupData, Long> out) {
        return new Max<>(in, out);
    }

    public static <Data, GroupData extends Data> AbstractGroupByAggregateHandler<Data, GroupData, ?, ?> count(GetFieldFunction<Data, Object> in, AggregateFunction<GroupData, Long> out) {
        return new Count<>(in, out);
    }


    /**
     * 平均
     *
     * @param <Data>
     */
    @NoArgsConstructor
    public static class Avg<Data, GroupData extends Data> extends AbstractGroupByAggregateHandler<Data, GroupData, Integer, Double> {

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

    @NoArgsConstructor
    public static class Max<Data, GroupData extends Data> extends AbstractGroupByAggregateHandler<Data, GroupData, Long, Long> {

        private Long max = null;

        public Max(GetFieldFunction<Data, Long> in, AggregateFunction<GroupData, Long> out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void handle(Data data) {
            Object value = FunctionUtils.getFieldValue(in, data);
            if (value != null) {
                Long v = (Long) value;
                max = max == null || v > max ? v : max;
            }
        }

        @Override
        public void aggregate(GroupData groupData) {
            FunctionUtils.setFieldValue(out, groupData, max);
        }
    }

    @NoArgsConstructor
    public static class Count<Data, GroupData extends Data> extends AbstractGroupByAggregateHandler<Data, GroupData, Object, Long> {

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
}
