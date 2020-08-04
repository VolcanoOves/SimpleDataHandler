package com.sanxs.data;

import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Yangshan
 * @Date: 2020/8/4
 * @Description:
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

    private Long maxId;
    private Double avgAge;
    private Long countId;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
