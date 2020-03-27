package com.ksying.mybatis.framework.sqlnode;

import com.ksying.mybatis.framework.sqlnode.iface.SqlNode;
import com.ksying.mybatis.framework.sqlsource.DynamicContext;
import lombok.AllArgsConstructor;

/**
 * 封装 不包含 ${} 的 sql 文本节点信息
 *
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 18:06
 */
@AllArgsConstructor
public class StaticTextSqlNode implements SqlNode {
    private String sqlText;

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(sqlText);
    }
}
