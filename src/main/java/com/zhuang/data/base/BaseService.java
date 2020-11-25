package com.zhuang.data.base;

import com.zhuang.data.DbAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseService<Entity, Key, Mapper extends BaseMapper<Entity, Key>> {

    @Autowired
    protected DbAccessor dbAccessor;
    @Autowired
    protected Mapper mapper;

    public Entity get(Key id) {
        return dbAccessor.select(id, getEntityClass());
    }

    public Entity getOne(Object objParams) {
        return dbAccessor.selectOne(objParams, getEntityClass());
    }

    public List<Entity> getList(Object objParams) {
        return dbAccessor.selectList(objParams, getEntityClass());
    }

    public int getCount(Object objParams) {
        return dbAccessor.selectCount(objParams, getEntityClass());
    }

    public void add(Entity entity) {
        dbAccessor.insert(entity);
    }

    public void update(Entity entity) {
        dbAccessor.update(entity);
    }

    public void updateExcludeNullFields(Entity entity) {
        dbAccessor.update(entity, true);
    }

    public void delete(Key id) {
        dbAccessor.delete(id, getEntityClass());
    }

    public void save(Entity entity) {
        dbAccessor.insertOrUpdate(entity);
    }

    public Class<Entity> getEntityClass() {
        Class<Entity> entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return entityClass;
    }

}
