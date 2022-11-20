package com.agri.controller;


import com.agri.model.*;
import com.agri.service.ISysRolePermService;
import com.agri.service.ISysRoleService;
import com.agri.service.PermsRolesService;
import com.agri.utils.PageUtil;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @SaveAuth(roles = {"admin", "coder"})
    public CommonResult getPermsOfRoles(@RequestBody List<Long> ids, @RequestParam("size") Long cSize, @RequestParam("page") Long cPage) {
        IPage<Map<String, String>> permsOfRoles = iSysRolePermService.getPermsOfRoles(ids, cSize, cPage);
        return CommonResult.OK(permsOfRoles);
    }

    @PostMapping("/query")
    @SaveAuth(roles = {"admin", "coder"})
    @ApiOperation(value = "角色权限查询")
    public CommonResult<?> getPerms(@RequestBody Map<String, Object> queryInfo) {
        if(queryInfo == null) {
            return CommonResult.error(ResultStatus.ARGUMENTS_WRONG, "查询参数为空");
        }
        PageUtil<SysPerm> pageUtil = new PageUtil<>();
        IPage<SysPerm> page = null;
        try {
            page = pageUtil.page(queryInfo);
        }catch (Exception e) {
            return CommonResult.error(ResultStatus.ARGUMENTS_WRONG, e.getMessage());
        }
        IPage<SysPerm> perms = iSysRolePermService.getPerms(queryInfo, page);
        return CommonResult.OK(perms);
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin", "coder"})
    @Transactional
    public CommonResult deletePermsOfRoles(@RequestBody List<SysRolePerm> RolePermList) {
        RolePermList.forEach(e -> {
                    iSysRolePermService.remove(new QueryWrapper<SysRolePerm>()
                            .eq("role_id", e.getRoleId())
                            .eq("perm_id", e.getPermId()));
                }
        );
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return CommonResult.OK("删除成功");
    }

    @PostMapping("/add")
    @SaveAuth(roles = {"admin", "coder"})
    public CommonResult addPermsOfRoles(@RequestBody List<SysRolePerm> RolePermList) {
        for(SysRolePerm rolePerm : RolePermList) {
            iSysRolePermService.save(rolePerm);
        }
        // 删除缓存
        permsRolesService.deletePermsOfRolesInRedis();
        return CommonResult.OK("添加成功");
    }

//    @PostMapping("/notAssigned")
//    @SaveAuth(roles = {"admin"})
//    public CommonResult getRolesNotAssigned(@RequestBody Long roleId) {
//        if(roleId == null) {
//            return CommonResult.create(ResultStatus.ARGUMENTS_WRONG, "参数错误");
//        }
//        iSysRolePermService.
//    }

}
