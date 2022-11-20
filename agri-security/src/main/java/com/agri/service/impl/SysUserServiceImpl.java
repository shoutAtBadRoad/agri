package com.agri.service.impl;


import com.agri.exception.DuplicateUserException;
import com.agri.mapper.SysUserMapper;
import com.agri.mapper.SysUserRoleMapper;
import com.agri.mapper.UserMapper;
import com.agri.model.SysUser;
import com.agri.model.SysUserRole;
import com.agri.model.User;
import com.agri.security.model.LoginUser;
import com.agri.service.ISysUserRoleService;
import com.agri.service.ISysUserService;
import com.agri.utils.RedisUtil;
import com.agri.utils.annotation.lock.Locked;
import com.agri.utils.annotation.lock.Param;
import com.agri.utils.annotation.lock.RKey;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

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
    private ISysUserRoleService userRoleService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<SysUser> getUsers(List<Long> ids, Long cSize, Long cPage) {
        IPage<SysUser> page = new Page<>();
        page.setSize(cSize);
        page.setCurrent(cPage);
        IPage<SysUser> users = sysUserMapper.getUsers(ids, page);
        return users.getRecords();
    }

    @Override
    public IPage<Map<String, Object>> getUsersWithType(List<Long> ids, IPage<SysUser> page, Map<String, Object> infos) {
        IPage<Map<String, Object>> usersWithType = sysUserMapper.getUsersWithType(ids, page, infos);
        return usersWithType;
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
    @Transactional
    public List<SysUser> reviseUsers(List<SysUser> userList) throws DuplicateUserException{
        // 邮箱、手机号查重
        List<String> mails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        for (SysUser user : userList) {
            if(user.getEmail() != null) mails.add(user.getEmail());
            if(user.getPhonenumber() != null) phones.add(user.getPhonenumber());
            if(user.getPassword() != null) {
                user.setPassword(null);
            }
        }
        List<SysUser> dupEmail = null, dupPhone = null;
        if(mails.size() > 0)
             dupEmail = sysUserMapper.selectList(new QueryWrapper<SysUser>().in("email", mails));
        if(phones.size() > 0)
            dupPhone = sysUserMapper.selectList(new QueryWrapper<SysUser>().in("phonenumber", phones));
        Map<String, Long> dupUsers = new HashMap<>();
        if(dupEmail != null)
        for (SysUser user : dupEmail) {
            dupUsers.put(user.getEmail(), user.getUserid());
        }
        if(dupPhone != null)
        for (SysUser user : dupPhone) {
            dupUsers.put(user.getPhonenumber(), user.getUserid());
        }
        List<SysUser> duplicate = new ArrayList<>();
        List<SysUser> single = new ArrayList<>();
        List<String> forbiddenUsers = new ArrayList<>();
        for (SysUser user : userList) {
            if( (dupUsers.containsKey(user.getEmail()) && !dupUsers.get(user.getEmail()).equals(user.getUserid()))
                    ||
                    ((dupUsers.containsKey(user.getPhonenumber())) && !dupUsers.get(user.getPhonenumber()).equals(user.getUserid()))) {
                duplicate.add(user);
            }else {
                single.add(user);
                forbiddenUsers.add(user.getUserid().toString());
            }
        }
        //TODO 如果修改了用户角色，修改用户角色关联表
        List<SysUserRole> userRoles = new ArrayList<>();
        for(SysUser user : single) {
            if(!StringUtils.isEmpty(user.getUserType()))
                userRoles.add(new SysUserRole(user.getUserid(), Long.valueOf(user.getUserType())));
        }
//        userRoleService.saveOrUpdateBatch(userRoles);
        for(SysUserRole userRole : userRoles) {
            int i = sysUserRoleMapper.update(userRole, new QueryWrapper<SysUserRole>().eq("user_id", userRole.getUserId()));
            if(i == 0) {
                sysUserRoleMapper.insert(userRole);
            }
        }
        if(single.size() > 0) this.updateBatchById(single);
        if(duplicate.size() > 0) {
            throw new DuplicateUserException(duplicate);
        }
        // 将修改过的用户的缓存删除
        redisUtil.del(forbiddenUsers);
        return single;
    }

    @Override
    public void deleteUserInfoInRedis(List<Long> ids) {
        List<String>  id = new ArrayList<>();
        for (Long aLong : ids) {
            id.add(String.valueOf(aLong));
        }
        redisUtil.del(id);
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

    @Override
    public boolean addUsers(List<SysUser> userList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        for(SysUser user : userList) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreateBy(loginUser.getUser().getUserid());
        }
        return this.saveBatch(userList);
    }
}
