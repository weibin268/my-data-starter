package com.zhuang.data.config;

import com.zhuang.data.datasource.DataSourceTypeInterceptor;
import com.zhuang.data.datasource.MasterSlaveDataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@Import(DataSourceTypeInterceptor.class)
public class MasterSlaveDataSourceConfig {

    @Autowired
    private MybatisProperties properties;

    @Autowired(required = false)
    private Interceptor[] interceptors;

    @Autowired
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Autowired(required = false)
    private DatabaseIdProvider databaseIdProvider;

    @PostConstruct
    public void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource
                    + " (please add config file or check your Mybatis " + "configuration)");
        }
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        //设置主从数据源
        factory.setDataSource(masterSlaveDataSource());
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        factory.setConfiguration(properties.getConfiguration());
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }

        return factory.getObject();
    }

    @Value("${spring.datasource.type}")
    private Class<? extends DataSource> dataSourceClass;

    @Bean
    @ConfigurationProperties("spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(dataSourceClass).build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().type(dataSourceClass).build();
    }

    @Bean
    public AbstractRoutingDataSource masterSlaveDataSource() {
        MasterSlaveDataSource masterSlaveDataSource = new MasterSlaveDataSource(masterDataSource(), slaveDataSource());
        return masterSlaveDataSource;
    }

}
