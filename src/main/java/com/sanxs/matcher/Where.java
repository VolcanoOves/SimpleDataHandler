package com.sanxs.matcher;

import com.sanxs.enums.WhereMatchDelimiterEnum;
import com.sanxs.matcher.function.WhereMatchFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: Where 条件筛选对象
 */
@Getter
public class Where<T> {
    /**
     * 条件匹配函数
     */
    private final List<WhereMatchFunctionWrapper> whereMatchFunctions = new LinkedList<>();

    /**
     * 追加and函数
     *
     * @param function 匹配罗辑
     * @return this
     */
    public Where<T> and(WhereMatchFunction<T> function) {
        this.whereMatchFunctions.add(new WhereMatchFunctionWrapper(WhereMatchDelimiterEnum.AND, function));
        return this;
    }

    /**
     * 追加or函数
     *
     * @param function 匹配罗辑
     * @return this
     */
    public Where<T> or(WhereMatchFunction<T> function) {
        this.whereMatchFunctions.add(new WhereMatchFunctionWrapper(WhereMatchDelimiterEnum.OR, function));
        return this;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class WhereMatchFunctionWrapper {
        private WhereMatchDelimiterEnum delimiter;
        private WhereMatchFunction<T> function;
    }
}
