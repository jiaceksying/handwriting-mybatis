package com.ksying.mybatis.version.v3;

import com.ksying.mybatis.factory.SqlSessionFactory;
import com.ksying.mybatis.factory.SqlSessionFactoryBuilder;
import com.ksying.mybatis.io.Resources;
import com.ksying.mybatis.pojo.User;
import com.ksying.mybatis.sqlsession.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/4/5 11:52
 */
public class Mybatis {
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void build() {
        String local = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(local);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }


    @Test
    public void query() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User();
        user.setId(1);
        user.setName("zhangsan");
        List<Object> users = sqlSession.selectList("test.findUserByIdAndName", user);
        System.out.println(users);

    }
}
