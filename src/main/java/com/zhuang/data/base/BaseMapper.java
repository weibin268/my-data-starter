package com.zhuang.data.base;

public interface BaseMapper<Entity, Key> {

    <T> T getById(Key id);

    int deleteById(Key id);

}
