package com.sanxs.data;

import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description: 测试专用聚合对象返回值
 **/
@Getter
@Setter
public class GroupTestData extends TestData {

    public GroupTestData() {
    }

    public GroupTestData(Long id, String name, Integer age, Integer gender, Long maxId, Double avgAge, Long countId) {
        super(id, name, age, gender);
        this.maxId = maxId;
        this.avgAge = avgAge;
        this.countId = countId;
    }

    /**
     * 最大ID
     */
    private Long maxId;

    /**
     * 平均年龄
     */
    private Double avgAge;

    /**
     * 统计个数
     */
    private Long countId;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
