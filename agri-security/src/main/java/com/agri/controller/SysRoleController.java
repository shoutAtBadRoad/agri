package com.agri.controller;


import com.agri.model.QueryInfo;
import com.agri.model.CommonResult;
import com.agri.model.SysRole;
import com.agri.model.SysRolePerm;
import com.agri.security.model.LoginUser;
import com.agri.service.ISysRolePermService;
import com.agri.service.ISysRoleService;
import com.agri.service.PermsRolesService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    @Autowired
    private ISysRoleService iSysRoleService;

    @Autowired
    private ISysRolePermService iSysRolePermService;

    @Autowired
    private PermsRolesService permsRolesService;

    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    public CommonResult  getAllRoles(@RequestBody QueryInfo info) {
        QueryWrapper<SysRole> wrapper = null;
        if(!StringUtils.isEmpty(info.getObscure())) {
            wrapper = new QueryWrapper<SysRole>().like("name", info.getObscure())
                    .or().like("role_key", info.getObscure());
        }
        if(info.getPagesize() != 0 && info.getPagenum() != 0) {
            IPage<SysRole> page = new Page<>();
            // 设置分页相关的信息
            // 设置页大小
            page.setSize(info.getPagesize());
            // 设置第几页
            page.setCurrent(info.getPagenum());
            IPage<SysRole> roleIPage = iSysRoleService.page(page, wrapper);
//        List<SysRole> records = roleIPage.getRecords();
            return CommonResult.OK(roleIPage);
        }else {
            // 查询全部角色
            List<SysRole> list = iSysRoleService.list();
            return CommonResult.OK(list);
        }
    }

    @PostMapping("/revise")
    @SaveAuth(roles = {"admin"})
    public CommonResult reviseRoles(@RequestBody List<SysRole> roleList) {
        long updateId = ((LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getUserid();
        for (SysRole sysRole : roleList) {
            sysRole.setUpdateBy(updateId);
        }
        boolean b = iSysRoleService.updateBatchById(roleList);
        return CommonResult.OK("修改成功");
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    @Transactional
    public CommonResult deleteRoles(@RequestBody List<Long> ids) {
//        List<SysRolePerm> roleId = iSysRolePermService.list(new QueryWrapper<SysRolePerm>().in("roleId", ids));
        // 删除关系表中role的记录
        iSysRolePermService.remove(new QueryWrapper<SysRolePerm>().in("role_id", ids));
        // 删除role记录
        iSysRoleService.removeByIds(ids);
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return CommonResult.OK("删除成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin"})
    public CommonResult addRoles(@RequestBody List<SysRole> roleList) {
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (SysRole sysRole : roleList) {
            sysRole.setCreateBy(user.getUser().getUserid());
        }
        iSysRoleService.saveBatch(roleList);
        return CommonResult.OK("添加成功");
    }

}