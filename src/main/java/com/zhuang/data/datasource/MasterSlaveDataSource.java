package com.zhuang.data.datasource;

import com.zhuang.data.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class MasterSlaveDataSource extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(MasterSlaveDataSource.class);

    private Integer salveCount = 0;

    public MasterSlaveDataSource(DataSource masterDataSource, DataSource... slaveDataSources) {
        Map<Object, Object> targetDataSource = new HashMap<>();
        targetDataSource.put(DataSourceType.Master.toString().toLowerCase(), masterDataSource);
        if (slaveDataSources.length > 1) {
            for (DataSource slaveDataSource : slaveDataSources) {
                targetDataSource.put(DataSourceType.Slave.toString().toLowerCase() + (salveCount++), slaveDataSource);
            }
        } else if (slaveDataSources.length == 1) {
            targetDataSource.put(DataSourceType.Slave.toString().toLowerCase(), slaveDataSources[0]);
        }
        this.setTargetDataSources(targetDataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType dataSourceType = DataSourceTypeHolder.get();
        if (dataSourceType == null) {
            dataSourceType = DataSourceType.Master;
        }
        String result;
        if (dataSourceType == DataSourceType.Slave && salveCount > 1) {
            result = dataSourceType.toString().toLowerCase() + RandomUtils.getInt(salveCount);
        } else {
            result = dataSourceType.toString().toLowerCase();
        }
        logger.debug("use datasource -> " + result);
        return result;
    }

}
