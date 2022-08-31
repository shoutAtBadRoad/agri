package com.agri.mapper;


import com.agri.model.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    Page<Map<String, String>> getRolesOfUsers(List<Long> ids, IPage<SysUserRole> page);

    List<String> getRolesOfUser(Long userid);

}
