package com.agri.controller;

import com.agri.model.ResultSet;
import com.agri.model.ResultStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/white")
@RestController
public class WhiteTestController {

    @GetMapping("/test")
    public ResultSet test() {
        return ResultSet.OK(ResultStatus.OK, "success");
    }

    @PostMapping("/perm")
    public ResultSet testPerm() {
        return ResultSet.OK(ResultStatus.OK, "success");
    }
}
