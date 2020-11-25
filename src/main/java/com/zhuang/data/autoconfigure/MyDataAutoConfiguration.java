package com.zhuang.data.autoconfigure;

import com.zhuang.data.DbAccessor;
import com.zhuang.data.handler.DbExecuteHandlerFactory;
import com.zhuang.data.handler.DbExecutionHandler;
import com.zhuang.data.mybatis.MyBatisDbAccessor;
import com.zhuang.data.mybatis.interceptor.DbExecutionInterceptor;
import com.zhuang.data.mybatis.interceptor.PaginationInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.List;
import java.util.Properties;

@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@EnableConfigurationProperties(MyDataProperties.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class MyDataAutoConfiguration {

    @Autowired
    private MyDataProperties myDataProperties;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired(required = false)
    private List<DbExecutionHandler> dbExecutionHandlerList;

    @Bean(destroyMethod = "")//destroyMethod置空,默认会调用close方法
    public DbAccessor dbAccessor() {
        Properties properties = com.zhuang.data.config.MyDataProperties.getInstance().getProperties();
        if (myDataProperties.getUnderscoreNaming() != null) {
            properties.setProperty(com.zhuang.data.config.MyDataProperties.UNDERSCORE_NAMING, myDataProperties.getUnderscoreNaming().toString());
        }
        if (myDataProperties.getDbExecutionHandlers() != null && myDataProperties.getDbExecutionHandlers().size() > 0) {
            String dbExecutionHandlers = String.join(",", myDataProperties.getDbExecutionHandlers());
            properties.setProperty(com.zhuang.data.config.MyDataProperties.DB_EXECUTION_HANDLERS, dbExecutionHandlers);
        }
        if (dbExecutionHandlerList != null && dbExecutionHandlerList.size() > 0) {
            dbExecutionHandlerList.forEach(c -> DbExecuteHandlerFactory.addDbExecutionHandler(c));
        }
        DbAccessor dbAccessor = new MyBatisDbAccessor(sqlSessionFactory, sqlSessionTemplate, false);
        return dbAccessor;
    }

    @Bean
    public Interceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public Interceptor dbExecutionInterceptor() {
        return new DbExecutionInterceptor();
    }

}
