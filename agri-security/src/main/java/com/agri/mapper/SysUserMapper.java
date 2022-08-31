package com.agri.mapper;


import com.agri.model.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    IPage<SysUser> getUsers(List<Long> ids, IPage<SysUser> page);

}
