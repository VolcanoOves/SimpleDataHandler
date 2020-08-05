package com.sanxs.matcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 分页对象
 */
@Getter
@Setter
@AllArgsConstructor
public class Limit {
    /**
     * 起始位置
     */
    private int rows;

    /**
     * 数据大小
     */
    private int offset;
}
