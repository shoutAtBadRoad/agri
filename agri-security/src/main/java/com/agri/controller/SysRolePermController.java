package com.agri.controller;


import com.agri.model.CommonResult;
import com.agri.model.ResultStatus;
import com.agri.model.SysRolePerm;
import com.agri.service.ISysRolePermService;
import com.agri.service.ISysRoleService;
import com.agri.service.PermsRolesService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    @SaveAuth(roles = {"admin"})
    public CommonResult getPermsOfRoles(@RequestBody List<Long> ids, @RequestParam("size") Long cSize, @RequestParam("page") Long cPage) {
        IPage<Map<String, String>> permsOfRoles = iSysRolePermService.getPermsOfRoles(ids, cSize, cPage);
        return CommonResult.OK(permsOfRoles);
    }

    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
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
    @SaveAuth(roles = {"admin"})
    public CommonResult addPermsOfRoles(@RequestBody List<SysRolePerm> RolePermList) {
        iSysRolePermService.saveBatch(RolePermList);
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
