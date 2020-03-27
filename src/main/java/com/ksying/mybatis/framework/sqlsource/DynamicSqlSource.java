package com.ksying.mybatis.framework.sqlsource;

import com.ksying.mybatis.framework.sqlnode.iface.SqlNode;
import com.ksying.mybatis.framework.sqlsource.iface.SqlSource;
import lombok.AllArgsConstructor;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 18:35
 */
@AllArgsConstructor
public class DynamicSqlSource implements SqlSource {
    private SqlNode rootSqlNode;

    @Override
    public BoundSql getBoundSql(Object paramObject) {
        DynamicContext dynamicContext = new DynamicContext(paramObject);
        // 将所有的sqlNode拼接为一条未处理 #{} 和 ${} 的 sql 语句
        rootSqlNode.apply(dynamicContext);

        return null;
    }
}
