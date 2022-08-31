package com.agri.service.impl;


import com.agri.mapper.SysRolePermMapper;
import com.agri.model.SysRolePerm;
import com.agri.service.ISysRolePermService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private SysRolePermMapper sysRolePermMapper;

    @Override
    public List<Map<String, String>> getPermsOfRoles(List<Long> ids, Long cSize, Long cPage) {
        IPage<List<Map<String,String>>> page = new Page<>();
        page.setSize(cSize);
        page.setPages(cPage);
        IPage<Map<String, String>> permsOfRoles = sysRolePermMapper.getPermsOfRoles(ids, page);
        return permsOfRoles.getRecords();
    }
}
