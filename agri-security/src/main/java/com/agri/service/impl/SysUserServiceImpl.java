package com.agri.service.impl;


import com.agri.mapper.SysUserMapper;
import com.agri.mapper.SysUserRoleMapper;
import com.agri.mapper.UserMapper;
import com.agri.model.SysUser;
import com.agri.model.User;
import com.agri.security.model.LoginUser;
import com.agri.service.ISysUserService;
import com.agri.utils.RedisUtil;
import com.agri.utils.annotation.lock.Locked;
import com.agri.utils.annotation.lock.Param;
import com.agri.utils.annotation.lock.RKey;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Log4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
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
    @Locked
    public LoginUser loadUserInfoById(@Param Long id, @RKey String rKey) {
        log.info("redisKey:" + rKey + "---开始加载数据");
        User user = userMapper.selectById(id);
        if(Objects.isNull(user)) {
            throw new RuntimeException("用戶名或密碼錯誤");
        }
        // 檢查權限信息
        List<String> list = sysUserRoleMapper.getRolesOfUser(user.getUserid());
        LoginUser loginUser = new LoginUser(user, list);
        redisUtil.set(String.valueOf(id), loginUser);
        return loginUser;
    }
}
