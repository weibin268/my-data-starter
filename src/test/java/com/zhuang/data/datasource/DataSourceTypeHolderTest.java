package com.zhuang.data.datasource;

import org.junit.Test;

public class DataSourceTypeHolderTest {

    @Test
    public void get() {
        DataSourceTypeHolder.master();
        System.out.println(DataSourceTypeHolder.get());
    }
}