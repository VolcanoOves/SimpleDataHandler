package com.sanxs.matcher;

import com.sanxs.matcher.function.OrderMatchFunction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
@Getter
public class OrderBy<T> {
    List<OrderModel<T>> orderMatchFunctions;

    public OrderBy() {
        this.orderMatchFunctions = new LinkedList<>();
    }

    /**
     * 追加排序规则 正序
     *
     * @param orderMatchFunction 排序匹配
     */
    public OrderBy<T> appendAsc(OrderMatchFunction<T, ?> orderMatchFunction) {
        OrderModel<T> orderModel = new OrderModel<>();
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
    public OrderBy<T> appendDesc(OrderMatchFunction<T, ?> orderMatchFunction) {
        OrderModel<T> orderModel = new OrderModel<>();
        orderModel.setAsc(false);
        orderModel.setOrderMatchFunction(orderMatchFunction);

        this.orderMatchFunctions.add(orderModel);
        return this;
    }

    @Getter
    @Setter
    public static class OrderModel<T> {
        private OrderMatchFunction<T, ?> orderMatchFunction;
        private boolean asc = true;
    }
}
