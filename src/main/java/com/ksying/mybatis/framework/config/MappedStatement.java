package com.ksying.mybatis.framework.config;

import com.ksying.mybatis.framework.sqlsource.iface.SqlSource;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用来封装映射文件中的CRUD标签脚本内容，比如select标签
 *
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 14:40
 */
@Data
@AllArgsConstructor
public class MappedStatement {
    private String statementId;
    private SqlSource sqlSource;
    private String statementType;
    private Class parameterTypeClass;
    private Class resultTypeClass;

}
