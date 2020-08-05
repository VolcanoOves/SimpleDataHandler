package com.sanxs.matcher.service;

import com.sanxs.matcher.GroupBy;
import com.sanxs.matcher.OrderBy;
import com.sanxs.matcher.function.GroupMatchFunction;
import com.sanxs.matcher.function.OrderMatchFunction;
import com.sanxs.matcher.function.gorup.AbstractGroupByAggregateHandler;
import com.sanxs.utils.FunctionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description:
 **/
public class DefaultOrderByFilterServiceImpl<Data> implements OrderByFilterService<Data> {
    @Override
    @SuppressWarnings("unchecked")
    public void apply(List<Data> data, OrderBy<Data> orderBy, GroupBy<Data, ?> groupBy) {
        if (orderBy != null) {

            // 检测排序字段是否合法
            this.validationOrderField(groupBy, orderBy);

            if (groupBy != null) {
                List<String> groupByFieldSet = new ArrayList<>();
                List<String> aggregateFieldSet = new ArrayList<>();

                for (GroupMatchFunction<Data, ?> groupMatchFunction : groupBy.getGroupMatchFunctions()) {
                    groupByFieldSet.add(FunctionUtils.getClassFieldName(groupMatchFunction));
                }

                for (AbstractGroupByAggregateHandler<Data, ?, ?, ?> handler : groupBy.getAggregateHandlers()) {
                    aggregateFieldSet.add(FunctionUtils.getClassFieldName(handler.getOut()));
                }
            }

            // 如果是没有进行分组
            data.sort((a, b) -> {
                for (OrderBy.OrderModel<Data, ?> orderModel : orderBy.getOrderMatchFunctions()) {
                    OrderMatchFunction<Data, ?> orderMatchFunction = (OrderMatchFunction<Data, ?>) orderModel.getOrderMatchFunction();

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

    /**
     * 检测排序字段的合法性
     * <p>
     * 如果group为空
     * 那么排序字段为Data的基础字段
     * <p>
     * 如果group不为空
     * 那么排序字段为分组字段或者是聚合字段
     *
     * @param groupBy     分组
     * @param orderBy     排序
     * @param <GroupData> 分组对象
     */
    private <GroupData extends Data> void validationOrderField(GroupBy<Data, GroupData> groupBy, OrderBy<Data> orderBy) {
        if (groupBy != null) {
            Set<String> groupMethodNames = new HashSet<>();
            // 取分组字段
            groupMethodNames.addAll(groupBy.getGroupMatchFunctions().stream().map(FunctionUtils::getClassFieldName).collect(Collectors.toSet()));
            // 取聚合字段
            groupMethodNames.addAll(groupBy.getAggregateHandlers().stream().map(item -> FunctionUtils.getClassFieldName(item.getOut())).collect(Collectors.toSet()));

            // 所有排序字段
            Set<String> orderMethodNames = orderBy.getOrderMatchFunctions().stream().map(item -> FunctionUtils.getClassFieldName(item.getOrderMatchFunction())).collect(Collectors.toSet());

            if (!groupMethodNames.containsAll(orderMethodNames)) {
                throw new RuntimeException("非法排序字段，当使用Group时，只能对聚合字段或聚合结果进行排序");
            }

        }
    }
}
