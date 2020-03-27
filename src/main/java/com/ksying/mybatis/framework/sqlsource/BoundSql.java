package com.ksying.mybatis.framework.sqlsource;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 16:11
 */
@Data
public class BoundSql {
    private String sql;

    private List<ParameterMapping> parameterMappings = new ArrayList<>();

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void addParameterMappings(ParameterMapping parameterMapping) {
        this.parameterMappings.add(parameterMapping);
    }
}
