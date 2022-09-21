package com.agri.service.impl;

import com.agri.config.SecurityConfig;
import com.agri.controller.LoginController;
import com.agri.exception.BeyondLoginTimeException;
import com.agri.filter.jwtfilter.RenewalJwtHandler;
import com.agri.model.LoginType;
import com.agri.model.RedisConstant;
import com.agri.security.model.LoginUser;
import com.agri.service.LoginService;
import com.agri.utils.JwtUtil;
import com.agri.utils.RedisUtil;
import com.agri.utils.UriUtil;
import com.agri.utils.annotation.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登陆服务
 * @author jyp
 * @since 2022-8-20
 */
@Service(value = "loginServiceImpl")
public class LoginServiceImpl implements LoginService {

    /**
     * 认证管理器 {@link SecurityConfig#authenticationManagerBean()}
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    HttpServletRequest request;

    @Override
    public String loginReturnToken(String username, String password) throws AuthenticationException, NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        //TODO 等前端的密码使用AES加密后，这里的密码就需要解一下密
//        password = AESUtil.decryptAES(password.getBytes());
        String ipAddress = UriUtil.getIpAddress(request);
        if(!checkLock(username, ipAddress)) {
            throw new BeyondLoginTimeException();
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(token);
        }catch (BadCredentialsException e) {
            if(LoginController.loginMethod.get() == LoginType.CODE_PHONE.getType())
                throw e;
            // 抛出用户名密码不正确的异常，告诉前端还有几次重试机会或者账户已经被锁定，几分钟后解锁
            int leaves = 0;
            if ((leaves = loginTimesCount(username, ipAddress, false)) > 0) {
                throw new BeyondLoginTimeException(leaves);
            } else {
                throw new BeyondLoginTimeException();
            }
        }
        // 如果登陆成功，把登陆次数锁定删除
        releaseLock(username, ipAddress);
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

    private void releaseLock(String username, String ipAddress) {
        String redisKey = ipAddress + RedisConstant.ACCOUNT_LOCK_PREFIX + username;
        Object key = redisUtil.get(redisKey);
        if(!Objects.isNull(key)) {
            redisUtil.del(redisKey);
        }
    }

    private boolean checkLock(String username, String ipAddress) {
        return loginTimesCount(username, ipAddress, true) - RedisConstant.ACCOUNT_RETRY_COUNTS < 0;
    }

    private int loginTimesCount(String username, String ipAddress, Boolean isCheck) {
        String redisKey = ipAddress + RedisConstant.ACCOUNT_LOCK_PREFIX + username;
        Object o = redisUtil.get(redisKey);
        int val;
        if(Objects.isNull(o))
            val = 0;
        else
            val = (int) o;
        if(isCheck) {
            return val;
        }
        if(val == 0) {
            redisUtil.set(redisKey, ++val, RedisConstant.ACCOUNT_LOCK_TIME);
        }else {
            val++;
            redisTemplate.opsForValue().increment(redisKey);
            redisUtil.expire(redisKey, RedisConstant.ACCOUNT_LOCK_TIME);
        }
        return RedisConstant.ACCOUNT_RETRY_COUNTS - val;
    }


}
