package com.ksying.mybatis.version.v2;

import com.ksying.mybatis.framework.config.Configuration;
import com.ksying.mybatis.framework.config.MappedStatement;
import com.ksying.mybatis.framework.sqlnode.IfSqlNode;
import com.ksying.mybatis.framework.sqlnode.MixedSqlNode;
import com.ksying.mybatis.framework.sqlnode.StaticTextSqlNode;
import com.ksying.mybatis.framework.sqlnode.TextSqlNode;
import com.ksying.mybatis.framework.sqlnode.iface.SqlNode;
import com.ksying.mybatis.framework.sqlsource.BoundSql;
import com.ksying.mybatis.framework.sqlsource.DynamicSqlSource;
import com.ksying.mybatis.framework.sqlsource.ParameterMapping;
import com.ksying.mybatis.framework.sqlsource.RawSqlSource;
import com.ksying.mybatis.framework.sqlsource.iface.SqlSource;
import com.ksying.mybatis.util.OgnlUtils;
import com.ksying.mybatis.util.SimpleTypeRegistry;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Simple package jdbc function
 * Extract the db info and sql info into mybatis.properties
 *
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/3/25 23:25
 */
public class Mybatis {
    private Configuration configuration = new Configuration();
    private String namespace;
    private Boolean isDynamic = false;

    /**
     * 获取并解析xml配置文件,封装为一个Configuration对象
     */
    private void loadConfiguration() {
        String location = "mybatis-comfig.xml";
        InputStream inputStream = parseXml(location);
        // 解析xml文件，获得document对象
        Document document = createDocument(inputStream);
        loadConfigurationElement(document.getRootElement());
    }

    private void loadConfigurationElement(Element rootElement) {
        // 解析 environment 标签，获取数据源信息，并封装到configuration中
        Element environments = rootElement.element("environment");
        parseEnvironments(environments);
        // 解析 mappers 标签，获取 mapper.xml 路径，进一步解析mapper.xml
        Element mappers = rootElement.element("mappers");
        parseMappers(mappers);

    }

    private void parseMappers(Element mappers) {
        List<Element> mapperList = mappers.elements();
        mapperList.forEach(mapper -> parseMapper(mapper));
    }

    private void parseMapper(Element mapper) {
        // 获取各个mapper标签的路径，并解析出各个 mapper.xml 的 document 对象
        String resource = mapper.attributeValue("resource");
        InputStream inputStream = parseXml(resource);
        Document document = createDocument(inputStream);
        parseXmlMapper(document.getRootElement());
    }

    private void parseXmlMapper(Element rootElement) {
        namespace = rootElement.attributeValue("namespace");
        // 处理select标签
        List<Element> selectList = rootElement.elements("select");
        selectList
                .forEach(element -> parseStatementElement(element));
    }

    private void parseStatementElement(Element element) {
        String statementId = element.attributeValue("id");
        if (statementId != null && !"".equals(statementId)) {
            // mybatis statementId 的组成是 namespace.id
            statementId = namespace + "." + statementId;
            // 参数类型
            String paramType = element.attributeValue("parameterType");
            Class parameterTypeClass = resolveType(paramType);
            // 返回类型
            String resultType = element.attributeValue("resultType");
            Class resultTypeClass = resolveType(resultType);
            // statementType 指定创建的statement类型 prepareStatement statement ..
            String statementType = element.attributeValue("statementType");
            statementType = statementType == null || "".equals(statementType) ? "prepared" : statementType;

            SqlSource sqlSource = createSqlSource(element);

            MappedStatement mappedStatement = new MappedStatement(statementId, sqlSource, statementType, parameterTypeClass, resultTypeClass);

            configuration.addMappedStatementMap(statementId, mappedStatement);


        }

    }

    private SqlSource createSqlSource(Element element) {
        SqlSource sqlSource = parseScriptNode(element);
        return sqlSource;
    }

    private SqlSource parseScriptNode(Element element) {
        MixedSqlNode rootSqlNode = parseDynamicTags(element);
        SqlSource sqlSource;
        if (isDynamic) {
            sqlSource = new DynamicSqlSource(rootSqlNode);
        } else {
            sqlSource = new RawSqlSource(rootSqlNode);
        }

        return null;
    }

    private MixedSqlNode parseDynamicTags(Element element) {
        List<SqlNode> sqlNodes = new ArrayList<>();
        // 为了获取文本信息，使用 node， 由 行数判断
        int nodeCount = element.nodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = element.node(i);
            if (node instanceof Text) {
                // 文本信息
                String sqlText = node.getText();
                if (sqlText == null || "".equals(sqlText)) {
                    continue;
                }
                TextSqlNode textSqlNode = new TextSqlNode(sqlText);
                if (textSqlNode.isDynamic(sqlText)) {
                    sqlNodes.add(textSqlNode);
                    isDynamic = true;
                } else {
                    sqlNodes.add(new StaticTextSqlNode(sqlText));
                }
            } else if (node instanceof Element) {
                // 动态标签
                Element ifElement = (Element) node;
                // 动态标签名称 if where...
                String name = node.getName();
                if ("if".equals(name)) {
                    // if 标签条件语句
                    String test = ifElement.attributeValue("test");
                    // if 标签下的mixedSqlNode
                    MixedSqlNode mixedSqlNode = parseDynamicTags(ifElement);
                    IfSqlNode ifSqlNode = new IfSqlNode(test, mixedSqlNode);
                    sqlNodes.add(ifSqlNode);
                } else if ("where".equals(node.getName())) {
                    // todo
                }

            }
        }
        return new MixedSqlNode(sqlNodes);

    }

    private Class resolveType(String paramType) {
        try {
            Class clazz = Class.forName(paramType);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseEnvironments(Element environments) {
        String def = environments.attributeValue("default");
        List<Element> elementList = environments.elements();
        elementList
                .forEach(
                        element -> {
                            if (def.equals(element.attributeValue("id"))) {
                                parseDataSource(element);
                            }
                        }
                );
    }

    private void parseDataSource(Element element) {
        Element dataSourceElement = element.element("dataSource");
        String type = dataSourceElement.attributeValue("type");
        if (type.equals("DBCP")) {
            BasicDataSource dataSource = new BasicDataSource();
            Properties properties = parseProperty(dataSourceElement);
            dataSource.setDriverClassName(properties.getProperty("driver"));
            dataSource.setUrl(properties.getProperty("url"));
            dataSource.setUsername(properties.getProperty("username"));
            dataSource.setPassword(properties.getProperty("password"));
            configuration.setDataSource(dataSource);
        }
    }

    private Properties parseProperty(Element dataSourceElement) {
        Properties properties = new Properties();
        List<Element> propElements = dataSourceElement.elements("property");
        propElements
                .forEach(
                        element -> {
                            String name = element.attributeValue("name");
                            String value = element.attributeValue("value");
                            properties.put(name, value);
                        }
                );
        return properties;
    }

    private Document createDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            return document;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream parseXml(String location) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
        return inputStream;
    }

    public List<?> selectList(String statementId, Object paramObject) throws Exception {
        List results = new ArrayList();
        // 获取连接
        Connection connection = getConnection(configuration.getDataSource());

        MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);
        SqlSource sqlSource = mappedStatement.getSqlSource();
        BoundSql boundSql = sqlSource.getBoundSql(paramObject);

        // 获取sql
        String sql = boundSql.getSql();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        // 处理参数
        handleParameter(preparedStatement, boundSql, mappedStatement, paramObject);
        ResultSet rs = preparedStatement.executeQuery();
        // 处理结果集
        handleResult(rs, mappedStatement, results);


        return results;
    }

    private void handleResult(ResultSet rs, MappedStatement mappedStatement, List results) throws Exception {
        Class resultTypeClass = mappedStatement.getResultTypeClass();
        Object result = null;
        while (rs.next()) {
            result = resultTypeClass.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Field field = resultTypeClass.getDeclaredField(columnName);
                field.set(result, rs.getObject(columnName));
            }
            results.add(result);
        }
    }

    private void handleParameter(PreparedStatement preparedStatement, BoundSql boundSql,
                                 MappedStatement mappedStatement, Object paramObject) throws Exception {
        Class parameterTypeClass = mappedStatement.getParameterTypeClass();
        if (SimpleTypeRegistry.isSimpleType(parameterTypeClass)) {
            preparedStatement.setObject(1, paramObject);
        } else {
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            for (int i = 0; i < parameterMappings.size(); i++) {
                Field field = parameterTypeClass.getDeclaredField(parameterMappings.get(i).getName());
                field.setAccessible(true);
                Object value = field.get(paramObject);
                preparedStatement.setObject(i + 1, value);
            }
        }
    }



    private Connection getConnection(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
