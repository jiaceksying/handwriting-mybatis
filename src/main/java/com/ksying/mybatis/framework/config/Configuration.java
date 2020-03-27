package com.ksying.mybatis.framework.config;

import lombok.Data;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 12:26
 */
@Data
public class Configuration {
    private DataSource dataSource;
    private Map<String, MappedStatement> mappedStatementMap = new HashMap<>();

    public MappedStatement getMappedStatementById(String id) {
        return mappedStatementMap.get(id);
    }

    public void addMappedStatementMap(String statementId, MappedStatement mappedStatement) {
        this.mappedStatementMap.put(statementId, mappedStatement);
    }
}
