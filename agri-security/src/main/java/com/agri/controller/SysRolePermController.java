package com.agri.controller;


import com.agri.model.ResultSet;
import com.agri.model.SysRolePerm;
import com.agri.service.ISysRolePermService;
import com.agri.service.ISysRoleService;
import com.agri.service.PermsRolesService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/sysRolePerm")
public class SysRolePermController {

    @Autowired
    private ISysRolePermService iSysRolePermService;

    @Autowired
    private PermsRolesService permsRolesService;

    /**
     * 查询角色下的所有权限
     * @param ids
     * @param cSize
     * @param cPage
     * @return
     */
    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    public ResultSet getPermsOfRoles(@RequestBody List<Long> ids, @RequestParam("size") Long cSize, @RequestParam("page") Long cPage) {
        List<Map<String, String>> permsOfRoles = iSysRolePermService.getPermsOfRoles(ids, cSize, cPage);
        return ResultSet.OK(permsOfRoles);
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    public ResultSet deletePermsOfRoles(@RequestBody List<SysRolePerm> rolePermList) {
        rolePermList.forEach(e -> {
                    iSysRolePermService.remove(new QueryWrapper<SysRolePerm>()
                            .eq("role_id", e.getRoleId())
                            .eq("perm_id", e.getPermId()));
                }
        );
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return ResultSet.OK("删除成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin"})
    public ResultSet addPermsOfRoles(@RequestBody List<SysRolePerm> rolePermList) {
        iSysRolePermService.saveBatch(rolePermList);
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return ResultSet.OK("添加成功");
    }

}
