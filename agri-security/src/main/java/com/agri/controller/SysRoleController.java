package com.agri.controller;


import com.agri.model.ResultSet;
import com.agri.model.SysRole;
import com.agri.model.SysRolePerm;
import com.agri.service.ISysRolePermService;
import com.agri.service.ISysRoleService;
import com.agri.service.PermsRolesService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
    public ResultSet  getAllRoles(@RequestParam("size") Long cSize, @RequestParam("page") Long cPage) {
        IPage<SysRole> page = new Page<>();
        // 设置分页相关的信息
        // 设置页大小
        page.setSize(cSize);
        // 设置第几页
        page.setCurrent(cPage);
        IPage<SysRole> roleIPage = iSysRoleService.page(page);
        List<SysRole> records = roleIPage.getRecords();
        return ResultSet.OK(records);
    }

    @PostMapping("/revise")
    @SaveAuth(roles = {"admin"})
    public ResultSet reviseRoles(@RequestBody List<SysRole> roleList) {
        boolean b = iSysRoleService.updateBatchById(roleList);
        return ResultSet.OK("修改成功");
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    @Transactional
    public ResultSet deleteRoles(@RequestBody List<Long> ids) {
        List<SysRolePerm> roleId = iSysRolePermService.list(new QueryWrapper<SysRolePerm>().in("roleId", ids));
        // 删除关系表中role的记录
        iSysRolePermService.removeByIds(roleId);
        // 删除role记录
        iSysRoleService.removeByIds(ids);
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return ResultSet.OK("删除成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin"})
    public ResultSet addRoles(@RequestBody List<SysRole> roleList) {
        iSysRoleService.saveBatch(roleList);
        return ResultSet.OK("添加成功");
    }

}
