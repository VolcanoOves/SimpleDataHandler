package com.sanxs.utils;

import cn.hutool.core.bean.BeanUtil;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 对象拷贝工具类
 **/
public class CopyUtils {
    /**
     * 复制对象值 不复制对象地址
     *
     * @param src  sources
     * @param dest target
     */
    public static void copy(Object src, Object dest) {
        BeanUtil.copyProperties(src, dest);
    }
}
