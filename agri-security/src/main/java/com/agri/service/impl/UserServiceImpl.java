package com.agri.service.impl;


import com.agri.mapper.UserMapper;
import com.agri.model.User;
import com.agri.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
