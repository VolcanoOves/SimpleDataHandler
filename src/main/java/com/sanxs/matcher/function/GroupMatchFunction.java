package com.sanxs.matcher.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
@FunctionalInterface
public interface GroupMatchFunction<T, R> extends Function<T, R>, Serializable {

}
