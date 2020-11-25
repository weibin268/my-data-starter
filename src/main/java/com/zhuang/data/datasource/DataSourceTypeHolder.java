package com.zhuang.data.datasource;

public class DataSourceTypeHolder {

    private static final ThreadLocal<DataSourceType> dataSourceTypeThreadLocal = new ThreadLocal<>();

    public static DataSourceType get() {
        return dataSourceTypeThreadLocal.get();
    }

    public static void set(DataSourceType dataSourceType) {
        dataSourceTypeThreadLocal.set(dataSourceType);
    }

    public static void master() {
        DataSourceTypeHolder.set(DataSourceType.Master);
    }

    public static void salve() {
        DataSourceTypeHolder.set(DataSourceType.Slave);
    }

}
