package com.agri.controller;


import com.agri.model.*;
import com.agri.service.ISysPermService;
import com.agri.service.ISysRolePermService;
import com.agri.service.PermsRolesService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.List;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
@RestController
@RequestMapping("/sysPerm")
public class SysPermController {

    @Autowired
    private ISysPermService iSysPermService;

    @Autowired
    private ISysRolePermService iSysRolePermService;

    @Autowired
    private PermsRolesService permsRolesService;

    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    public ResultSet getAllPerms(@RequestParam("size") Long cSize, @RequestParam("page") Long cPage) {
        IPage<SysPerm> page = new Page<>();
        // 设置分页相关的信息
        // 设置页大小
        page.setSize(cSize);
        // 设置第几页
        page.setCurrent(cPage);
        List<SysPerm> permList = iSysPermService.page(page).getRecords();
        return ResultSet.OK(permList);
    }

    @PostMapping("/revise")
    @SaveAuth(roles = {"admin"})
    public ResultSet revisePerms(@RequestBody(required = true) List<SysPerm> permList) {
        iSysPermService.updateBatchById(permList);
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return ResultSet.OK("修改成功");
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    @Transactional
    public ResultSet deletePerms(@RequestBody List<Long> ids) {
        List<SysRolePerm> rolePerms = iSysRolePermService.list(new QueryWrapper<SysRolePerm>().in("permId", ids));
        // 删除关系表中权限
        iSysRolePermService.removeByIds(rolePerms);
        // 删除相关权限
        iSysPermService.removeByIds(ids);
        // TODO 删除redis中的缓存
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return ResultSet.OK("删除成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin"})
    public ResultSet addPerms(@RequestBody List<SysPerm> permList) {
        iSysPermService.saveBatch(permList);
        return ResultSet.OK("添加成功");
    }
}
