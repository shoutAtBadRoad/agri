package com.agri.controller;


import com.agri.model.ResultSet;
import com.agri.model.SysUser;
import com.agri.service.ISysUserService;
import com.agri.utils.annotation.AESUtil;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/all")
    @SaveAuth(roles = {"admin"})
    public ResultSet getAllUser(@RequestBody List<Long> ids, @RequestParam("size") Long cSize, @RequestParam("cPage") Long page) {
        IPage<SysUser> userIPage = new Page<>();
        userIPage.setSize(cSize);
        userIPage.setPages(page);
        if(ids == null || ids.size() == 0) {
            IPage<SysUser> sysUserIPage = iSysUserService.page(userIPage);
            List<SysUser> records = sysUserIPage.getRecords();
            return ResultSet.OK(records);
        }else {
            List<SysUser> users = iSysUserService.getUsers(ids, cSize, page);
            return ResultSet.OK(users);
        }
    }

    /**
     * 修改密码，后端需要对前端的base64进行转码，拿到密码明文再使用对称加密，得到密文才能存入数据库，所以这个接口不支持修改密码，会做单独的修改密码接口
     * @param userList
     * @return
     */
    @PostMapping("/revise")
    @SaveAuth
    public ResultSet reviseUsers(@RequestBody List<SysUser> userList) {
        if(Objects.isNull(userList) || userList.size() == 0) {
            return ResultSet.OK("没有要修改的对象");
        }
        boolean b = iSysUserService.updateBatchById(userList);
        //TODO 如果修改失败，可以写重试机制进行重试
        List<Long> ids = new ArrayList<>();
        userList.forEach(e -> {
            ids.add(e.getUserid());
        });
        iSysUserService.deleteUserInfoInRedis(ids);
        return ResultSet.OK("修改成功");
    }

    /**
     * 用户删除是真删除还是假删除，先做假删除，调用修改接口就行，这个先保留
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @SaveAuth(roles = {"admin"})
    public ResultSet deleteUsers(@RequestBody List<Long> ids) {
        return ResultSet.OK("删除成功");
    }

    /**
     * 适用于管理员后台创建账号
     * @return
     */
    @PostMapping("/add")
    @SaveAuth(roles = {"admin"})
    public ResultSet addUsers(@RequestBody List<SysUser> userList) {
        boolean b = iSysUserService.saveBatch(userList);
        return ResultSet.OK("创建成功");
    }

    /**
     * 适用于用户自己修改密码
     * @param user
     * @return
     */
    @PostMapping("/revisePass")
    @SaveAuth
    public ResultSet revisePass(@RequestBody SysUser user, HttpServletRequest request) {
        //TODO 先AES解密
//        byte[] bytes = user.getPassword().getBytes();
//        String s;
//        try {
//            s = AESUtil.decryptAES(bytes);
//        }catch (Exception e) {
//            e.printStackTrace();
//            return ResultSet.error("内部出错，修改失败");
//        }
//        user.setPassword(s);
        boolean b = iSysUserService.revisePass(user, request.getHeader("Authorization"));
        return ResultSet.OK("修改成功");
    }

    /**
     * 接受AES密钥和位移向量
     * @return
     */
    @PostMapping("/aes")
    public ResultSet getAESKeyAndIV() {
        Map<String, String> map = new HashMap<>();
        map.put("key", AESUtil.KEY);
        map.put("iv", AESUtil.IV);
        return ResultSet.OK(map);
    }

}
