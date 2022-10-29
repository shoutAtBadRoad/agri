package com.agri.controller;

import com.agri.model.CommonResult;
import com.agri.model.ResultStatus;
import com.agri.utils.annotation.SaveAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/white")
@RestController
public class WhiteTestController {

    @GetMapping("/test")
    @SaveAuth(roles = {"admin", "user", "coder"})
    public CommonResult test() {
        return CommonResult.OK(ResultStatus.OK, "success");
    }

    @PostMapping("/perm")
    @SaveAuth(roles = {"admin", "user", "coder"})
    public CommonResult testPerm() {
        return CommonResult.OK(ResultStatus.OK, "success");
    }
}
