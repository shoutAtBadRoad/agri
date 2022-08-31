package com.agri.service.impl;

import com.agri.filter.jwtfilter.RenewalJwtHandler;
import com.agri.security.model.LoginUser;
import com.agri.service.LoginService;
import com.agri.utils.JwtUtil;
import com.agri.utils.RedisUtil;
import com.agri.utils.annotation.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service(value = "loginServiceImpl")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String loginReturnToken(String username, String password) throws NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        //TODO 等前端的密码使用AES加密后，这里的密码就需要解一下密
//        password = AESUtil.decryptAES(password.getBytes());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);
        Authentication authenticate = authenticationManager.authenticate(token);

        if(Objects.isNull(authenticate)) {
            throw new RuntimeException("登陸失敗");
        }

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String id = String.valueOf(loginUser.getUser().getUserid());
        String jwt = JwtUtil.createJWT(id);

        // 存儲redis
        redisUtil.set(id, loginUser);
        redisUtil.set(jwt, jwt, RenewalJwtHandler.DEFAULT_EXPIRE_TIME);

        return jwt;
    }

    @Override
    public String logtout(HttpServletRequest request) {
        //獲取securityContextHolder中的用戶id
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        long userid = loginUser.getUser().getUserid();
        //刪除redis中的值
        redisUtil.del("" + userid);
        String token = request.getHeader("Authorization");
        redisUtil.del(token);
        return "註銷成功";
    }
}
