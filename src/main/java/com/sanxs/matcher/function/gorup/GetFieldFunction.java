package com.sanxs.matcher.function.gorup;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: lambda表达式获取 function :: 取值
 **/
@FunctionalInterface
public interface GetFieldFunction<T, R> extends Function<T, R>, Serializable {
}
