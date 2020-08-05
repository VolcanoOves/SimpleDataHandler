package com.sanxs.utils;

import com.sanxs.data.TestData;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: Yang shan
 * @Date: 2020/8/4
 * @Description:
 */
public class CopyUtilsDemoTest {

    /**
     * 字段copy测试
     */
    @Test
    public void copyTest() {
        TestData a = new TestData(1L, "张三", 26, 1);
        TestData b = new TestData();

        CopyUtils.copy(a, b);

        Assert.assertNotSame(a, b);
        Assert.assertEquals(a.toString(), b.toString());
    }
}