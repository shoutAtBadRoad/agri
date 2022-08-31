package com.agri.service;


import com.agri.model.SysUser;
import com.agri.security.model.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    boolean revisePass(SysUser user, String token);

    void deleteUserInfoInRedis(List<Long> ids);

    void loadUserInfoById(Long id);

}
