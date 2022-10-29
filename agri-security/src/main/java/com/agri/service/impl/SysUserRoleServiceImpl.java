package com.agri.service.impl;

import com.agri.mapper.SysUserRoleMapper;
import com.agri.model.SysUserRole;
import com.agri.service.ISysUserRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Override
    public List<Map<String, String>> getRolesOfUsers(List<Long> ids) {
//        IPage<SysUserRole> page = new Page<>();
        Page<Map<String, String>> rolesOfUsers = userRoleMapper.getRolesOfUsers(ids);
        List<Map<String, String>> records = rolesOfUsers.getRecords();
        return records;
    }
}
