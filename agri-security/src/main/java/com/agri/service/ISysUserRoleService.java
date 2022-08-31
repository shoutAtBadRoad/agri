package com.agri.service;


import com.agri.model.SysUserRole;
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
public interface ISysUserRoleService extends IService<SysUserRole> {

    List<Map<String, String>> getRolesOfUsers(List<Long> ids, Long cSize, Long cPage);

}
