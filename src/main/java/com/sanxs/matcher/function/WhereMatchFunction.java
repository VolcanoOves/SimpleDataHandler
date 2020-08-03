package com.sanxs.matcher.function;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
@FunctionalInterface
public interface WhereMatchFunction<T> {

    boolean match(T data);
}
