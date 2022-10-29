package com.agri.service;

import com.agri.model.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient("security")
public interface AuthService {

    @PostMapping(value = "/auth/apiAuth")
    String verifyAuthentication(@RequestHeader("Authorization") String token, @RequestParam("uri") String uir);

}
