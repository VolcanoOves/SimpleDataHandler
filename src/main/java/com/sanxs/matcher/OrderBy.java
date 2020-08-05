package com.sanxs.matcher;

import com.sanxs.matcher.function.OrderMatchFunction;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 排序对象
 */
@Getter
public class OrderBy<Data> {
    /**
     * 需要排序的字段
     * eg: TestData::getId 代表 id 排序
     */
    private final List<OrderModel<Data, ?>> orderMatchFunctions;

    public OrderBy() {
        this.orderMatchFunctions = new LinkedList<>();
    }

    /**
     * 追加排序规则 正序
     *
     * @param orderMatchFunction 排序匹配
     */
    public <GroupData extends Data> OrderBy<Data> appendAsc(OrderMatchFunction<GroupData, ?> orderMatchFunction) {
        OrderModel<Data, GroupData> orderModel = new OrderModel<>();
        orderModel.setAsc(true);
        orderModel.setOrderMatchFunction(orderMatchFunction);

        this.orderMatchFunctions.add(orderModel);
        return this;
    }

    /**
     * 追加排序规则 倒叙
     *
     * @param orderMatchFunction 排序匹配
     */
    public <GroupData extends Data> OrderBy<Data> appendDesc(OrderMatchFunction<GroupData, ?> orderMatchFunction) {
        OrderModel<Data, GroupData> orderModel = new OrderModel<>();
        orderModel.setAsc(false);
        orderModel.setOrderMatchFunction(orderMatchFunction);

        this.orderMatchFunctions.add(orderModel);
        return this;
    }

    @Getter
    @Setter
    public static class OrderModel<Data, GroupData extends Data> {
        private OrderMatchFunction<GroupData, ?> orderMatchFunction;
        private boolean asc = true;
    }
}
