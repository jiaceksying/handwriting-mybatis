package com.ksying.mybatis.framework.sqlnode.iface;

import com.ksying.mybatis.framework.sqlsource.DynamicContext;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 17:38
 */
public interface SqlNode {
    public void apply(DynamicContext context);
}
