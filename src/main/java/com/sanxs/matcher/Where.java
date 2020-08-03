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
 * @Description:
 */
@Getter
public class Where<T> {
    private final List<WhereMatchFunctionWrapper> whereMatchFunctions = new LinkedList<>();

    public Where<T> and(WhereMatchFunction<T> function) {
        this.whereMatchFunctions.add(new WhereMatchFunctionWrapper(WhereMatchDelimiterEnum.AND, function));
        return this;
    }

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
