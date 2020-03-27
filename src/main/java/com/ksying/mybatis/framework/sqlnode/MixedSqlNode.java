package com.ksying.mybatis.framework.sqlnode;

import com.ksying.mybatis.framework.sqlnode.iface.SqlNode;
import com.ksying.mybatis.framework.sqlsource.DynamicContext;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * MixedSqlNode 是对所有类型的 SqlNode 的一个封装
 * 塞入的时候不用做任何判断，只需要按顺序塞入 sqlNodes 中即可
 *
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 17:37
 */
@AllArgsConstructor
public class MixedSqlNode implements SqlNode {

    private List<SqlNode> sqlNodes = new ArrayList<>();


    @Override
    public void apply(DynamicContext context) {
        sqlNodes.forEach(sqlNode -> sqlNode.apply(context));
    }
}
