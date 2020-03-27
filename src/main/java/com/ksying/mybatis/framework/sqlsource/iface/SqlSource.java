package com.ksying.mybatis.framework.sqlsource.iface;

import com.ksying.mybatis.framework.sqlsource.BoundSql;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 14:39
 */
public interface SqlSource {
    BoundSql getBoundSql(Object paramObject);
}
