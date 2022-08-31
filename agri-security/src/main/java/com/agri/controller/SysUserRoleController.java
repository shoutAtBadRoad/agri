package com.agri.controller;


import com.agri.model.ResultSet;
import com.agri.model.SysUserRole;
import com.agri.service.ISysUserRoleService;
import com.agri.service.ISysUserService;
import com.agri.utils.annotation.SaveAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
@RestController
@RequestMapping("/sysUserRole")
public class SysUserRoleController {

    @Autowired
    private ISysUserRoleService iSysUserRoleService;

    @Autowired
    private ISysUserService userService;

    /**
     * 给一组用户id，查询他们的拥有的角色
     * @param ids
     * @param cSize
     * @param cPage
     * @return
     */
    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    public ResultSet getAllRolesOfUser(@RequestBody List<Long> ids, @RequestParam("size") Long cSize, @RequestParam("page")Long cPage) {
        List<Map<String, String>> rolesOfUsers = iSysUserRoleService.getRolesOfUsers(ids, cSize, cPage);
        return ResultSet.OK(rolesOfUsers);
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    public ResultSet deleteRolesOfUser(@RequestBody List<SysUserRole> userRoleList) {
        boolean b = iSysUserRoleService.removeByIds(userRoleList);
        // TODO 删除redis中的用户缓存信息
        List<Long> ids = new ArrayList<>();
        userRoleList.forEach(e -> {
            ids.add(e.getUserId());
        });
        userService.deleteUserInfoInRedis(ids);
        return ResultSet.OK("删除成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin"})
    public ResultSet addRolesOfUser(@RequestBody List<SysUserRole> userRoleList) {
        boolean b = iSysUserRoleService.saveBatch(userRoleList);
        // TODO 删除redis中的用户缓存信息
        List<Long> ids = new ArrayList<>();
        userRoleList.forEach(e -> {
            ids.add(e.getUserId());
        });
        userService.deleteUserInfoInRedis(ids);
        return ResultSet.OK("添加成功");
    }
}
