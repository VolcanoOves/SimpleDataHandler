package com.sanxs.data;

import cn.hutool.json.JSON;
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

    private Long maxId;
    private Double avgAge;
    private Long countId;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
