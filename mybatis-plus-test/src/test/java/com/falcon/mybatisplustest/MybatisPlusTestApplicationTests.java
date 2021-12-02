package com.falcon.mybatisplustest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.falcon.mybatisplustest.entity.User;
import com.falcon.mybatisplustest.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisPlusTestApplicationTests {
    @Autowired
    public UserMapper userMapper;

    @Test
    void contextLoads() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getId,1);
        List<User> users = userMapper.selectList(queryWrapper);
        System.out.println(users);
    }

}
