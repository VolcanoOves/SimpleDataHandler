package com.sanxs.matcher.function;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
@FunctionalInterface
public interface WhereMatchFunction<T> {
    /**
     * where 进行匹配
     *
     * @param data 需要匹配的数据
     * @return 返回是否匹配成功
     */
    boolean match(T data);
}
