package com.agri.security.login;

import com.agri.controller.LoginController;
import com.agri.mapper.SysUserRoleMapper;
import com.agri.model.LoginType;
import com.agri.model.ResultStatus;
import com.agri.model.SysUser;
import com.agri.security.model.LoginUser;
import com.agri.service.ISysUserRoleService;
import com.agri.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 手机号密码登陆服务
 * @author jyp
 * @since 2022-9-21
 */
@Component
public class PhonePassDetailsServiceImpl implements UserDetailsService {

    @Resource
    private ISysUserService userService;

    @Resource
    private SysUserRoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        if(LoginController.loginMethod.get() != LoginType.PASS_PHONE.getType()) {
            throw new UsernameNotFoundException("不是" + LoginType.PASS_PHONE);
        }
        SysUser user = userService.getOne(new QueryWrapper<SysUser>().eq("phonenumber", phoneNumber));
        if(Objects.isNull(user))
            throw new UsernameNotFoundException(ResultStatus.PASS_WRONG.getReasonPhrase());
        List<String> rolesOfUser = roleMapper.getRolesOfUser(user.getUserid());
        LoginUser loginUser = new LoginUser(user, rolesOfUser);
        return loginUser;
    }
}
