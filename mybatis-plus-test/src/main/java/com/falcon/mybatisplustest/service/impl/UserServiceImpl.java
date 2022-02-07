package com.falcon.mybatisplustest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.falcon.mybatisplustest.entity.User;
import com.falcon.mybatisplustest.mapper.UserMapper;
import com.falcon.mybatisplustest.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements IUserService {
}
