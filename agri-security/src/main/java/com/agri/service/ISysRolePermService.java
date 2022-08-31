package com.agri.service;


import com.agri.model.SysRolePerm;
import com.github.jeffreyning.mybatisplus.service.IMppService;

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
public interface ISysRolePermService extends IMppService<SysRolePerm> {

    List<Map<String, String >> getPermsOfRoles(List<Long> ids, Long cSize, Long cPage);

}
