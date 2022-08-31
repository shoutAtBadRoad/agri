package com.agri.mapper;


import com.agri.model.SysRolePerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
@Mapper
public interface SysRolePermMapper extends MppBaseMapper<SysRolePerm> {

    IPage<Map<String, String >> getPermsOfRoles(@Param("ids") List<Long> ids, IPage<?> page);
}
