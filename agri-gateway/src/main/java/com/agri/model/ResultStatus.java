package com.agri.model;

/**
 * 返回值状态码
 */
public enum ResultStatus {


    OK(200,"ok"),


    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403,"Forbidden"),
    NOT_FOUND(404, "Not Found"),


    ERROR(500,"Internal Server Error"),


    /**
     * 密码错误
     */
    PASS_WRONG(601, "Wrong Password"),
    /**
     * 账户被锁定
     */
    ACCOUNT_LOCKED(602,"Account Locked")
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
