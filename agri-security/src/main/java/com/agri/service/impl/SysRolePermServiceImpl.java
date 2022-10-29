package com.agri.service.impl;


import com.agri.mapper.SysRolePermMapper;
import com.agri.model.SysRolePerm;
import com.agri.model.SysRolePerm;
import com.agri.service.ISysRolePermService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
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
public class SysRolePermServiceImpl extends MppServiceImpl<SysRolePermMapper, SysRolePerm> implements ISysRolePermService {

    @Resource
    private SysRolePermMapper sysRolePermMapper;

    @Override
    public IPage<Map<String, String>> getPermsOfRoles(List<Long> ids, Long cSize, Long cPage) {
        IPage<Map<String,String>> page = new Page<>();
        page.setSize(cSize);
        page.setCurrent(cPage);
        IPage<Map<String, String>> permsOfRoles = sysRolePermMapper.getPermsOfRoles(ids, page);
        return permsOfRoles;
    }
}
