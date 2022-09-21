package com.agri.model;

/**
 * 登陆方式    {@link com.agri.service.impl.LoginServiceImpl}
 * @author jyp
 * @since 2022-9-21
 */
public enum LoginType {
    /**
     * 用户名密码方式
     */
    PASSWORD(0),

    /**
     * 手机号验证码方式
     */
    CODE_PHONE(1),

    /**
     * 手机号密码方式
     */
    PASS_PHONE(2);

    private final int type;

    LoginType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
