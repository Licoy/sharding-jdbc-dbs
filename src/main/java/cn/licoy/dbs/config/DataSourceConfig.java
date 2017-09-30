package cn.licoy.dbs.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.NoneDatabaseShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.NoneTableShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.mysql.jdbc.Driver;
import org.hibernate.mapping.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author licoy.cn
 * @version 2017/9/30
 */
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() throws SQLException {
        return buildDataSource();
    }


    private DataSource buildDataSource() throws SQLException {
        //设置分库映射
        Map<String,DataSource> dataSourceMap = Collections.synchronizedMap(new HashMap<>());
        //添加数据库
        dataSourceMap.put("dbs_0",createDataSource("dbs_0"));
        dataSourceMap.put("dbs_1",createDataSource("dbs_1"));
        //设置默认数据库
        DataSourceRule rule = new DataSourceRule(dataSourceMap,"dbs_0");

        TableRule orderTableRule = TableRule.builder("t_order")
                .actualTables(Arrays.asList("t_order_0","t_order_1"))
                .dataSourceRule(rule)
                .generateKeyColumn("order_id")
                .build();

        ShardingRule shardingRule = ShardingRule.builder()
                .dataSourceRule(rule)
                .tableRules(Arrays.asList(orderTableRule))
                .databaseShardingStrategy(new DatabaseShardingStrategy("user_id",new ModuloDatabaseShardingAlgorithm()))
                .tableShardingStrategy(new TableShardingStrategy("order_id",new ModuloTableShardingAlgorithm()))
                .build();

        DataSource dataSource = ShardingDataSourceFactory.createDataSource(shardingRule);

        return dataSource;
    }


    private static DataSource createDataSource(final String dataSourceName) {
        //使用druid连接数据库
        DruidDataSource result = new DruidDataSource();
        result.setDriverClassName(Driver.class.getName());
        result.setUrl(String.format("jdbc:mysql://localhost:3306/%s", dataSourceName));
        result.setUsername("root");
        result.setPassword("root");
        return result;
    }


}
