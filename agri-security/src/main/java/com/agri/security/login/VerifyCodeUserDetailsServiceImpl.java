package com.agri.security.login;

import com.agri.controller.LoginController;
import com.agri.mapper.SysUserMapper;
import com.agri.mapper.SysUserRoleMapper;
import com.agri.model.LoginType;
import com.agri.model.ResultStatus;
import com.agri.model.SysUser;
import com.agri.model.User;
import com.agri.security.model.LoginUser;
import com.agri.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 短信验证登陆服务
 * @author jyp
 * @since 2022-9-21
 */
@Component
public class VerifyCodeUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 判断是否是对应登陆方式
        if(LoginController.loginMethod.get() != LoginType.CODE_PHONE.getType()) {
            throw  new UsernameNotFoundException(String.valueOf(LoginType.CODE_PHONE.getType()));
        }
        String code = (String) redisUtil.get(username);
        if(StringUtils.isEmpty(code))
            throw new UsernameNotFoundException("没有找到验证码");
        User user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("phonenumber", username));
        if(Objects.isNull(user))
            throw new UsernameNotFoundException(ResultStatus.PHONE_FREE.getReasonPhrase());
        user.setPassword(code);
        List<String> rolesOfUser = roleMapper.getRolesOfUser(user.getUserid());
        LoginUser loginUser = new LoginUser(user, rolesOfUser);
        return loginUser;
    }
}
