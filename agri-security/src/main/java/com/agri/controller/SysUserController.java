package com.agri.controller;


import com.agri.exception.DuplicateUserException;
import com.agri.model.CommonResult;
import com.agri.model.ResultStatus;
import com.agri.model.SysUser;
import com.agri.model.SysUserRole;
import com.agri.security.model.LoginUser;
import com.agri.service.ISysUserRoleService;
import com.agri.service.ISysUserService;
import com.agri.utils.annotation.AESUtil;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
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
@RequestMapping("/sysUser")
public class SysUserController {

    @Autowired
    private ISysUserService iSysUserService;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    public CommonResult getAllUser(@RequestBody Map<String, Object> infos){
                                //@RequestBody List<Long> ids, @RequestParam("size") Long cSize, @RequestParam("cPage") Long page) {
        //TODO 修改为实体类接收
//        infos = (Map<String, Object>) infos.get("params");
        List<Long> ids = (List<Long>) infos.get("ids");
        if(ids == null) {
            return CommonResult.create(ResultStatus.ARGUMENTS_WRONG, "参数错误");
        }
        // 添加查询条件
        String mobile = (String) infos.get("phonenumber");
        if(StringUtils.isEmpty(mobile)) {
            infos.put("phonenumber", null);
        }
        long size = ((Integer)infos.get("pagesize")).longValue();
        long page = ((Integer) infos.get("pagenum")).longValue();
        IPage<SysUser> userIPage = new Page<>();
        userIPage.setSize(size);
        userIPage.setCurrent(page);
        if(ids.size() == 0) {
            IPage<Map<String, Object >> sysUserIPage = iSysUserService.getUsersWithType(null, userIPage, infos);
//            List<SysUser> records = sysUserIPage.getRecords();
            for(Map<String, Object> map : sysUserIPage.getRecords()) {
                map.put("userid", map.get("userid").toString());
            }
            return CommonResult.OK(sysUserIPage);
        }else {
            IPage<Map<String, Object>> users = iSysUserService.getUsersWithType(ids, userIPage, infos);
            for(Map<String, Object> map : users.getRecords()) {
                map.put("userid", map.get("userid").toString());
            }
            return CommonResult.OK(users);
        }
    }

    /**
     * 修改密码，后端需要对前端的base64进行转码，拿到密码明文再使用对称加密，得到密文才能存入数据库，所以这个接口不支持修改密码，会做单独的修改密码接口
     * @param userList
     * @return
     */
    @PostMapping("/revise")
    @SaveAuth(roles = {"admin", "coder", "farmer", "user"})
    public CommonResult reviseUsers(@RequestBody List<SysUser> userList) {
        if(Objects.isNull(userList) || userList.size() == 0) {
            return CommonResult.OK("没有要修改的对象");
        }
//        boolean b = iSysUserService.updateBatchById(userList);
        try {
            userList = iSysUserService.reviseUsers(userList);
        }catch (DuplicateUserException e) {
            return CommonResult.create(ResultStatus.PHONE_MAIL_CLAIMED, e.getUserList());
        }
        //TODO 如果修改失败，可以写重试机制进行重试
        List<Long> ids = new ArrayList<>();
        userList.forEach(e -> {
            ids.add(e.getUserid());
        });
        iSysUserService.deleteUserInfoInRedis(ids);
        return CommonResult.OK("修改成功");
    }

    /**
     * 用户删除是真删除还是假删除，先做假删除，调用修改接口就行，这个先保留
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @SaveAuth(roles = {"admin", "coder"})
    public CommonResult deleteUsers(@RequestBody List<Long> ids) {
        iSysUserService.removeByIds(ids);
        return CommonResult.OK("删除成功");
    }

    @Resource
    private ISysUserRoleService userRoleService;

    /**
     * 适用于管理员后台创建账号
     * @return
     */
    @Transactional
    @PostMapping("/add")
    @SaveAuth(roles = {"admin", "coder"})
    public CommonResult<?> addUsers(@RequestBody List<SysUser> userList) {
        if(iSysUserService.addUsers(userList)) {
            // 添加用户角色关系
            List<SysUserRole> list = new ArrayList<>();
            userList.forEach(user -> {
                list.add(new SysUserRole(user.getUserid(), Long.valueOf(user.getUserType())));
                userRoleService.saveBatch(list);
            });
            return CommonResult.OK("创建成功");
        }
        else
            return CommonResult.error("添加失败");
    }

    /**
     * 适用于用户自己修改密码
     * @param user
     * @return
     */
    @PostMapping("/revisePass")
    @SaveAuth(roles = {"admin", "coder", "farmer", "user"})
    public CommonResult revisePass(@RequestBody SysUser user, HttpServletRequest request) {
        //TODO 先AES解密
//        byte[] bytes = user.getPassword().getBytes();
//        String s;
//        try {
//            s = AESUtil.decryptAES(bytes);
//        }catch (Exception e) {
//            e.printStackTrace();
//            return CommonResult.error("内部出错，修改失败");
//        }
//        user.setPassword(s);
        boolean b = iSysUserService.revisePass(user, request.getHeader("Authorization"));
        return CommonResult.OK("修改成功");
    }

    /**
     * 接受AES密钥和位移向量
     * @return
     */
    @PostMapping("/aes")
    @SaveAuth(roles = {"admin", "coder", "farmer", "user"})
    public CommonResult getAESKeyAndIV() {
        Map<String, String> map = new HashMap<>();
        map.put("key", AESUtil.KEY);
        map.put("iv", AESUtil.IV);
        return CommonResult.OK(map);
    }

    @PostMapping("/count")
    @SaveAuth(roles = {"admin", "coder", "farmer", "user"})
    public CommonResult<?> getCount(@RequestBody Map<String, Object> queryInfo) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        String phone = null, email = null;
        if((phone = (String) queryInfo.get("phonenumber")) != null) {
            wrapper.eq("phonenumber", phone);
        }
        if((email = (String) queryInfo.get("email")) != null) {
            wrapper.eq("email", email);
        }
        int count = iSysUserService.count(wrapper);
        return CommonResult.OK(count);
    }

}
