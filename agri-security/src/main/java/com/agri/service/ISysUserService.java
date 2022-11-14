package com.agri.service;


import com.agri.exception.DuplicateUserException;
import com.agri.model.SysUser;
import com.agri.security.model.LoginUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
public interface ISysUserService extends IService<SysUser> {

    List<SysUser> getUsers(List<Long> ids, Long cSize, Long cPage);

    IPage<Map<String, Object>> getUsersWithType(List<Long> ids, IPage<SysUser> page, Map<String, Object> params);

    boolean revisePass(SysUser user, String token);

    List<SysUser> reviseUsers(List<SysUser> userList) throws DuplicateUserException;

    void deleteUserInfoInRedis(List<Long> ids);

    LoginUser loadUserInfoById(Long id, String rKey);

    boolean addUsers(List<SysUser> userList);

}
