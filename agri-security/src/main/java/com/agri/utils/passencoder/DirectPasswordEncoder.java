package com.agri.utils.passencoder;

import com.agri.config.SecurityConfig;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 直接比较器,不对密码加密的情况下使用  {@link SecurityConfig#authenticationManagerBean()}
 * @author jyp
 * @since 2022-9-21
 */
public class DirectPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(rawPassword.toString());
    }
}
