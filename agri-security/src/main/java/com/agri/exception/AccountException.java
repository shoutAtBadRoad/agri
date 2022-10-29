package com.agri.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AccountException extends RuntimeException{

    public static final String ACCOUNT_FORBIDDEN = "账户已被禁用，请联系管理员";

    public static final int FORBIDDEN_FLAG = 1;

    private String reason;
}
