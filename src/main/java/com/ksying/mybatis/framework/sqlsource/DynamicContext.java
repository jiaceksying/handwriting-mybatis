package com.ksying.mybatis.framework.sqlsource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 18:59
 */
public class DynamicContext {
    private StringBuffer sb = new StringBuffer();
    private Map<String, Object> binds = new HashMap<>();

    public DynamicContext(Object paramObject) {
        this.binds.put("_parameter", paramObject);
    }

    public void appendSql(String sqlText) {
        sb.append(sqlText);
        sb.append(" ");
    }

    public String getSql() {
        return sb.toString();
    }

    public Object getBinds(String key) {
        return binds.get(key);
    }
}
