package com.agri.controller;

import com.agri.model.CommonResult;
import com.agri.model.ResultStatus;
import com.agri.model.SysUser;
import com.agri.security.sms.SmsSendService;
import com.agri.service.ISysUserService;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/sms")
@Api("短信服务接口")
public class SmsController {

    @Resource
    private SmsSendService smsSendService;

    @Resource
    private ISysUserService userService;

    @GetMapping("/send/code")
    @ApiOperation(value = "短信验证码发送接口", response = String.class)
    @SaveAuth
    public CommonResult<String> getSmsCode(@ApiParam("手机号") @RequestParam String phoneNumber) {
        // 检查手机号
        if(StringUtils.isEmpty(phoneNumber))
            return CommonResult.create(ResultStatus.OK, "手机号为空");
        SysUser user = userService.getOne(new QueryWrapper<SysUser>().eq("phonenumber", phoneNumber));
        if(Objects.isNull(user))
            return CommonResult.create(ResultStatus.PHONE_FREE, "手机号尚未注册");
        // 通知监听器发送短信
        smsSendService.sendSms(phoneNumber);
        return CommonResult.create(ResultStatus.OK, "短信发送成功，十分钟有效");
    }
}
