package com.sanxs.matcher.function.gorup;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description:
 **/
@FunctionalInterface
public interface GetFieldFunction<T, R> extends Function<T, R>, Serializable {
}
