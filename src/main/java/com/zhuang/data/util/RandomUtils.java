package com.zhuang.data.util;

import java.util.Random;

public class RandomUtils {

    private static Random random = new Random();

    public static int getInt(int bound) {
        return random.nextInt(bound);
    }
}
