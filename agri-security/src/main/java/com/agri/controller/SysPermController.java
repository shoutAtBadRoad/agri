package com.agri.controller;


import com.agri.model.*;
import com.agri.security.model.LoginUser;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @SaveAuth(roles = {"admin", "coder"})
    @ApiOperation(value = "权限信息查询接口", response = List.class)
    public CommonResult getAllPerms(@ApiParam("分页大小") @RequestBody QueryInfo queryInfo) {
        if(queryInfo == null) {
            return CommonResult.create(ResultStatus.ARGUMENTS_WRONG, "请求参数错误");
        }
        String obscure = queryInfo.getObscure();
        QueryWrapper<SysPerm> wrapper  = null;
        if(!StringUtils.isEmpty(obscure)) {
            wrapper = new QueryWrapper<>();
            wrapper.like("perm_name", obscure)
                    .or().like("path", obscure)
                    .or().like("remark", obscure);
        }
        IPage<SysPerm> page = new Page<>();
        // 设置分页相关的信息
        // 设置页大小
        page.setSize(queryInfo.getPagesize());
        // 设置第几页
        page.setCurrent(queryInfo.getPagenum());
        IPage<SysPerm> page1 = iSysPermService.page(page, wrapper);
        return CommonResult.OK(page1);
    }

    @PostMapping("/revise")
    @SaveAuth(roles = {"admin", "coder"})
    @ApiOperation(value = "权限信息修改接口", response = String.class)
    public CommonResult revisePerms(@ApiParam("传入的权限列表") @RequestBody(required = true) List<SysPerm> permList) {
        CommonResult res = null;
        if((res = checkArg(permList)) != null) {
            return res;
        }
        SecurityContext context = SecurityContextHolder.getContext();
        LoginUser principal = (LoginUser) context.getAuthentication().getPrincipal();
        for (SysPerm sysPerm : permList) {
            sysPerm.setUpdateBy(principal.getUser().getUserid());
        }
        iSysPermService.updateBatchById(permList);
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return CommonResult.OK("修改成功");
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin", "coder"})
    @ApiOperation(value = "权限删除列表", response = String.class)
    @Transactional
    public CommonResult deletePerms(@ApiParam("需要删除的权限id列表") @RequestBody List<Long> ids) {
        CommonResult res = null;
        if((res = checkArg(ids)) != null) {
            return res;
        }
//        List<SysRolePerm> RolePerms = iSysRolePermService.list(new QueryWrapper<SysRolePerm>().in("permId", ids));
        // 删除关系表中权限
        iSysRolePermService.remove(new QueryWrapper<SysRolePerm>().in("perm_id", ids));
        // 删除相关权限
        iSysPermService.removeByIds(ids);
        // TODO 删除redis中的缓存
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return CommonResult.OK("删除成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin", "coder"})
    @ApiOperation(value = "权限添加接口", response = String.class)
    public CommonResult addPerms(@ApiParam("权限列表") @RequestBody List<SysPerm> permList) {
        CommonResult res = null;
        if((res = checkArg(permList)) != null) {
            return res;
        }
        SecurityContext context = SecurityContextHolder.getContext();
        LoginUser principal = (LoginUser) context.getAuthentication().getPrincipal();
        for (SysPerm sysPerm : permList) {
            sysPerm.setCreateBy(principal.getUser().getUserid());
        }
        iSysPermService.saveBatch(permList);
        return CommonResult.OK("添加成功");
    }

    private CommonResult checkArg(List<?> permList) {
        if(permList == null) {
            return CommonResult.create(ResultStatus.ARGUMENTS_WRONG, "请求参数错误");
        }
        if(permList.size() == 0) {
            //TODO 对空请求过滤
            return CommonResult.create(ResultStatus.OK, "空请求");
        }
        return null;
    }
}
