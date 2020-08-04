package com.sanxs.matcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description:
 */
@Getter
@Setter
@AllArgsConstructor
public class Limit {
    private int rows;
    private int offset;
}
