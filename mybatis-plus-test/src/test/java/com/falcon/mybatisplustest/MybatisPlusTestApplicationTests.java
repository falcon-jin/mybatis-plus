package com.falcon.mybatisplustest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.falcon.mybatisplustest.entity.User;
import com.falcon.mybatisplustest.mapper.UserMapper;
import com.falcon.mybatisplustest.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MybatisPlusTestApplicationTests {
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public IUserService userService;


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
       // queryWrapper.lambda().eq(User::getId,1).eq(User::getEmail,"123");
        //User users = userMapper.selectOne(queryWrapper,true);
        Page<User> userPage = userMapper.selectPage(new Page<>(2, 5), queryWrapper);
        System.out.println(userPage);

    }

    @Test
    void testSaveOrUpdate() {
        List<User> list = userService.list();
        ArrayList<User> entityList = new ArrayList<>();
        userService.saveOrUpdateBatch(entityList);

    }



}
