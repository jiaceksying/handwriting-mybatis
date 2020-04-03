package com.ksying.mybatis.framework.sqlsource;

import com.ksying.mybatis.framework.sqlnode.iface.SqlNode;
import com.ksying.mybatis.framework.sqlsource.iface.SqlSource;
import com.ksying.mybatis.util.GenericTokenParser;
import com.ksying.mybatis.util.ParameterMappingTokenHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 只包含#{}只需要处理一次替换工作，在构造方法中进行
 *
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 18:35
 */
@Data
@AllArgsConstructor
public class StaticSqlSource implements SqlSource {
    private String sql;
    private List<ParameterMapping> parameterMappings;


    @Override
    public BoundSql getBoundSql(Object paramObject)  {
        return new BoundSql(sql, parameterMappings);
    }
}
