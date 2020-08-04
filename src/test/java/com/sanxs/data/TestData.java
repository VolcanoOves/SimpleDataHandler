package com.sanxs.data;

import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Yang shan
 * @Date: 2020/8/3
 * @Description: 测试专用数据模型
 */
@Getter
@Setter
public class TestData {

    public TestData() {
    }

    /**
     * 自增长ID
     */
    private Long id;

    /**
     * 名称测试用UUID
     */
    private String name;

    /**
     * 年龄 0-99
     */
    private Integer age;

    /**
     * 性别 0男 1女
     */
    private Integer gender;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
