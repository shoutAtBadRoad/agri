package com.agri.controller;


import com.agri.model.*;
import com.agri.service.ISysPermService;
import com.agri.service.ISysRolePermService;
import com.agri.service.PermsRolesService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api("权限控制器")
public class SysPermController {

    @Autowired
    private ISysPermService iSysPermService;

    @Autowired
    private ISysRolePermService iSysRolePermService;

    @Autowired
    private PermsRolesService permsRolesService;

    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    @ApiOperation(value = "权限信息查询接口", response = List.class)
    public ResultSet getAllPerms(@ApiParam("分页大小") @RequestParam("size") Long cSize, @ApiParam("页号") @RequestParam("page") Long cPage) {
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
    @ApiOperation(value = "权限信息修改接口", response = String.class)
    public ResultSet revisePerms(@ApiParam("传入的权限列表") @RequestBody(required = true) List<SysPerm> permList) {
        iSysPermService.updateBatchById(permList);
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return ResultSet.OK("修改成功");
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    @ApiOperation(value = "权限删除列表", response = String.class)
    @Transactional
    public ResultSet deletePerms(@ApiParam("需要删除的权限id列表") @RequestBody List<Long> ids) {
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
    @ApiOperation(value = "权限添加接口", response = String.class)
    public ResultSet addPerms(@ApiParam("权限列表") @RequestBody List<SysPerm> permList) {
        iSysPermService.saveBatch(permList);
        return ResultSet.OK("添加成功");
    }
}
