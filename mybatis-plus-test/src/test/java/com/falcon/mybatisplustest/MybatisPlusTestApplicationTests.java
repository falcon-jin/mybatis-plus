package com.falcon.mybatisplustest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.falcon.mybatisplustest.entity.User;
import com.falcon.mybatisplustest.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SpringBootTest
class MybatisPlusTestApplicationTests {
    @Autowired
    public UserMapper userMapper;

    @Test
    void testLambdaQueryChainWrapper() {
        System.out.println(new LambdaQueryChainWrapper(userMapper).list());
    }
    @Test
    void testQueryChainWrapper() {
        System.out.println(new QueryChainWrapper(userMapper).list());
    }
    @Test
    void testUpdateChainWrapper() {
        UpdateChainWrapper id = (UpdateChainWrapper) new UpdateChainWrapper(userMapper).eq("id", 1);
        User entity = new User();
        entity.setId(1L);entity.setName("123");

        System.out.println(id.update(entity));
    }

    @Test
    void testInsertBatch() {
        List<User> list = new ArrayList<>();
        User e = new User();

        e.setName("123");

        list.add(e);
        User e1 = new User();

        e1.setName("12323");

        list.add(e1);
        int i = userMapper.insertBatch(list);
        System.out.println(1);

    }
    @Test
    void testSelect() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getId,1).eq(User::getEmail,"123");
        List<User> users = userMapper.selectList(queryWrapper);

    }





}
