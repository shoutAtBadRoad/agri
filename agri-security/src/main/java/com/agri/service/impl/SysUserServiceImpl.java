package com.agri.service.impl;


import com.agri.mapper.SysUserMapper;
import com.agri.mapper.SysUserRoleMapper;
import com.agri.mapper.UserMapper;
import com.agri.model.SysUser;
import com.agri.model.User;
import com.agri.security.model.LoginUser;
import com.agri.service.ISysUserService;
import com.agri.utils.RedisUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SysUser> getUsers(List<Long> ids, Long cSize, Long cPage) {
        IPage<SysUser> page = new Page<>();
        page.setSize(cSize);
        page.setPages(cPage);
        IPage<SysUser> users = sysUserMapper.getUsers(ids, page);
        return users.getRecords();
    }

    @Override
    public boolean revisePass(SysUser user, String token) {
        // 此处用户的密码已经是明文
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        int i = sysUserMapper.updateById(user);
        if(i == 1) {
            // 修改成功,用户需要重新登陆
            Long userid = user.getUserid();
            redisUtil.del(String.valueOf(userid));
            redisUtil.del(token);
            return true;
        }
        return false;
    }

    @Override
    public void deleteUserInfoInRedis(List<Long> ids) {
        String[] objects = (String[]) ids.toArray();
        redisUtil.del(objects);
    }

    @Override
    public void loadUserInfoById(Long id) {
        User user = userMapper.selectById(id);
        if(Objects.isNull(user)) {
            throw new RuntimeException("用戶名或密碼錯誤");
        }
        // 檢查權限信息
        List<String> list = sysUserRoleMapper.getRolesOfUser(user.getUserid());
        LoginUser loginUser = new LoginUser(user, list);
        redisUtil.set(String.valueOf(id), loginUser);
    }
}
