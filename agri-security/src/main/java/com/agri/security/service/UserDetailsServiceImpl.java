package com.agri.security.service;


import com.agri.mapper.MenuMapper;
import com.agri.mapper.UserMapper;
import com.agri.model.User;
import com.agri.security.model.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_name", username));
        if(Objects.isNull(user)) {
            throw new RuntimeException("用戶名或密碼錯誤");
        }

        //TODO 檢查權限信息
        List<String> list = menuMapper.selectPermsByUserId(user.getUserid());
        LoginUser loginUser = new LoginUser(user, list);
        // 把數據封裝成userDetails返回
        return loginUser;
    }
}
