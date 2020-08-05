package com.sanxs.matcher.service;

import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.function.GroupMatchFunction;
import com.sanxs.matcher.function.OrderMatchFunction;
import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import com.sanxs.utils.FunctionUtils;

import java.util.*;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description:
 **/
public class DefaultOrderByFilterServiceImpl<Data> implements OrderByFilterService<Data> {
    @Override
    public void apply(List<Data> data, OrderBy<Data> orderBy, GroupBy<Data, ?> groupBy) {
        if (orderBy != null) {

            if (groupBy != null) {
                List<String> groupByFieldSet = new ArrayList<>();
                List<String> aggregateFieldSet = new ArrayList<>();

                for (GroupMatchFunction<Data, ?> groupMatchFunction : groupBy.getGroupMatchFunctions()) {
                    groupByFieldSet.add(FunctionUtils.getMethodName(groupMatchFunction));
                }

                for (AbstractGroupByAggregateHandler<Data, ?, ?, ?> handler : groupBy.getAggregateHandlers()) {
                    aggregateFieldSet.add(FunctionUtils.getMethodName(handler.getOut()));
                }
            }

            // 如果是没有进行分组
            data.sort((a, b) -> {
                for (OrderBy.OrderModel<Data> orderModel : orderBy.getOrderMatchFunctions()) {
                    OrderMatchFunction<Data, ?> orderMatchFunction = orderModel.getOrderMatchFunction();

                    Object aValue = FunctionUtils.getFieldValue(orderMatchFunction, a);
                    Object bValue = FunctionUtils.getFieldValue(orderMatchFunction, b);

                    if (!Objects.equals(aValue, bValue)) {
                        if (aValue == null) {
                            return orderModel.isAsc() ? -1 : 1;
                        }
                        if (bValue == null) {
                            return orderModel.isAsc() ? 1 : -1;
                        }

                        int r = aValue.hashCode() > bValue.hashCode() ? 1 : -1;
                        return orderModel.isAsc() ? r : (-1 * r);
                    }
                }
                return 0;
            });

        }
    }
}
