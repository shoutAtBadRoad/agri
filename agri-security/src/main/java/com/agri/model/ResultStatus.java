package com.agri.model;

/**
 * 返回值状态码
 */
public enum ResultStatus {


    /**
     * 请求成功
     */
    OK(200,"ok"),

    /**
     * 无权限
     */
    UNAUTHORIZED(401, "Unauthorized"),
    /**
     * 禁止访问
     */
    FORBIDDEN(403,"Forbidden"),
    /**
     * 资源未找到
     */
    NOT_FOUND(404, "Not Found"),

    /**
     * 服务器内部错误
     */
    ERROR(500,"Internal Server Error"),


    /**
     * 密码错误
     */
    PASS_WRONG(601, "Wrong Password"),
    /**
     * 账户被锁定
     */
    ACCOUNT_LOCKED(602,"Account Locked"),
    /**
     * 手机号未注册
     */
    PHONE_FREE(603, "Phone Free"),
    /**
     * 手机号已经注册
     */
    PHONE_CLAIMED(604, "Phone Claimed"),
    /**
     * 手机号或邮箱重复
     */
    PHONE_MAIL_CLAIMED(605, "Phone or Email Claimed"),

    /**
     * 请求参数错误
     */
    ARGUMENTS_WRONG(701, "Wrong Request Arguments")
    ;


    private final Integer code;

    private final String reasonPhrase;

    ResultStatus(int i, String s) {
        code = i;
        reasonPhrase = s;
    }

    public Integer getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
