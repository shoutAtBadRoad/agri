package com.agri.controller;


import com.agri.model.CommonResult;
import com.agri.model.ResultStatus;
import com.agri.model.SysUserRole;
import com.agri.service.ISysUserRoleService;
import com.agri.service.ISysUserService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
     * @return
     */
    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    public CommonResult getAllRolesOfUser(@RequestBody List<Long> ids) {
        List<Map<String, String>> rolesOfUsers = iSysUserRoleService.getRolesOfUsers(ids);
        return CommonResult.OK(rolesOfUsers);
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    public CommonResult deleteRolesOfUser(@RequestBody List<SysUserRole> userRoleList) {
        boolean b = iSysUserRoleService.removeByIds(userRoleList);
        // TODO 删除redis中的用户缓存信息
        List<Long> ids = new ArrayList<>();
        userRoleList.forEach(e -> {
            ids.add(e.getUserId());
        });
        userService.deleteUserInfoInRedis(ids);
        return CommonResult.OK("删除成功");
    }

    @PostMapping("/revise")
    @SaveAuth(roles = {"admin"})
    @Transactional
    public CommonResult reviseRolesOfUser(@RequestBody List<SysUserRole> userRoleList) {
        if(userRoleList.size() == 0) {
            return CommonResult.create(ResultStatus.ARGUMENTS_WRONG, "空请求");
        }
        Set<Long> userId = new HashSet<>();
        for (SysUserRole sysUserRole : userRoleList) {
            userId.add(sysUserRole.getUserId());
        }
        try {
            iSysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", userId));
            iSysUserRoleService.saveBatch(userRoleList);
            userService.deleteUserInfoInRedis(new ArrayList<>(userId));
        }catch (Exception e) {
            return CommonResult.error("修改失败");
        }
        return CommonResult.OK("修改成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin"})
    public CommonResult addRolesOfUser(@RequestBody List<SysUserRole> userRoleList) {
        boolean b = iSysUserRoleService.saveBatch(userRoleList);
        // TODO 删除redis中的用户缓存信息
        List<Long> ids = new ArrayList<>();
        userRoleList.forEach(e -> {
            ids.add(e.getUserId());
        });
        userService.deleteUserInfoInRedis(ids);
        return CommonResult.OK("添加成功");
    }
}
