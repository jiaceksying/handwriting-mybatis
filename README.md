# handwriting-mybatis
write simply mybatis framework by yourself

# mybatis
- The v1 version simply encapsulates the functions of JDBC
- The v2 version uses process-oriented to implement the core functions of mybatis
- The v3 version uses OOP to achieve the core functions of mybatis(only a test class, need dependency handwriting-mybatis-framework propject)

# mybatis core function describe
## Configuration

解析配置文件，使用 **Configuration** 进行封装：

- mybatis-config.xml 全局配置文件，主要获取数 dataSource 和 mappers 标签的信息。
- XXXMapper.xml 获取各操作的具体信息，封装为一个 MappedStatement 并存入 Map<statementId, MappedStatement> 中。

## MappedStatement 

- 一个 <select></select> 标签就是一个 MappedStatement 对象。
- 封装了当前操作标签(<select></select> <insert></insert> 等)的 id, parameterType，resultType，statementType 属性和 **SqlSource** 。

## SqlSource 

### 	1. DynamicSqlSource

### 	2. RawSqlSource

### 	3. StaticSqlSource

## SqlNode

### 	1. MixSqlNode

### 	2. TextSqlNode

### 	3. StaticSqlNode

	### 	4. IfSqlNode

## BoundSql

eg: select * from user where id = ?

- 存储完整的且 JDBC 可执行的 SQL 
- 存储 #{} 替换为 ? 后对应的入参条件
