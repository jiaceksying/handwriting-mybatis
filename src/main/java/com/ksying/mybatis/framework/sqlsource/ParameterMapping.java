package com.ksying.mybatis.framework.sqlsource;

import lombok.Data;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 16:16
 */
@Data
public class ParameterMapping {
    private String name;

    private Class type;

    public ParameterMapping(String name) {
        this.name = name;
    }
}
