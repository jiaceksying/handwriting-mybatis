package com.ksying.mybatis.framework.sqlnode;

import com.ksying.mybatis.framework.sqlnode.iface.SqlNode;
import com.ksying.mybatis.framework.sqlsource.DynamicContext;
import com.ksying.mybatis.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 封装包含 ${} 的 sql 文本信息
 *
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/3/26 17:59
 */
@Data
@AllArgsConstructor
public class TextSqlNode implements SqlNode {
    private String sqlText;


    /**
     * 判断当前 sql 文本中是否包含 ${}
     *
     * @param sqlText
     * @return
     */
    public Boolean isDynamic(String sqlText) {
        if (sqlText.indexOf("${") > -1) {
            return true;
        }
        return false;
    }

    @Override
    public void apply(DynamicContext context) {
        BindingTokenHandle tokenHandle = new BindingTokenHandle(context);
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", tokenHandle);
        String sql = tokenParser.parse(sqlText);
        context.appendSql(sql);
    }

    private static class BindingTokenHandle implements TokenHandler {
        private DynamicContext context;
        public BindingTokenHandle(DynamicContext context) {
            this.context = context;
        }
        @Override
        public String handleToken(String content) {
            Object paramObject = context.getBinds("_parameter");
            if (content == null) {
                return "";
            } else if (SimpleTypeRegistry.isSimpleType(paramObject.getClass())) {
                return paramObject.toString();
            }
            Object value = OgnlUtils.getValue(content, paramObject);
            return value.toString();
        }
    }
}

