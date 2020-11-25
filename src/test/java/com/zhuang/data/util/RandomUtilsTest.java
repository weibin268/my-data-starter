package com.zhuang.data.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class RandomUtilsTest {

    @Test
    public void getInt() {
        for (int i = 0; i < 100; i++) {
            System.out.println(RandomUtils.getInt(3));
        }
    }
}