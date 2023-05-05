package org.csu.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.csu.api.persistence.UserMapper;

@SpringBootTest
class MallApiApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelectUser() {
        System.out.println(userMapper.selectList(null));
    }

    @Test
    void contextLoads() {
    }

}
