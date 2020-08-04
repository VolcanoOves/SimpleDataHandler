package com.sanxs.utils;

import com.sanxs.data.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Author: Yang shan
 * @Date: 2020/8/4
 * @Description:
 */
class CopyUtilsTest {

    /**
     * 字段copy测试
     */
    @Test
    public void copyTest() {
        TestData a = new TestData(1L, "张三", 26, 1);
        TestData b = new TestData();

        CopyUtils.copy(a, b);

        Assertions.assertNotSame(a, b);
        Assertions.assertEquals(a.toString(), b.toString());
    }
}