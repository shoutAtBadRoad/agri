package com.agri.security.login;


import com.agri.controller.LoginController;
import com.agri.mapper.SysUserRoleMapper;
import com.agri.mapper.UserMapper;
import com.agri.model.LoginType;
import com.agri.model.ResultStatus;
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

/**
 * 用户名密码登陆服务
 * @author jyp
 * @since 2022-8-20
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(LoginController.loginMethod.get() != LoginType.PASSWORD.getType()) {
            throw  new UsernameNotFoundException("不是" + LoginController.loginMethod.get());
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_name", username));
        if(Objects.isNull(user)) {
            throw new UsernameNotFoundException(ResultStatus.PASS_WRONG.getReasonPhrase());
        }
        // 檢查權限信息
        List<String> list = sysUserRoleMapper.getRolesOfUser(user.getUserid());
        LoginUser loginUser = new LoginUser(user, list);
        // 把數據封裝成userDetails返回
        return loginUser;
    }
}
