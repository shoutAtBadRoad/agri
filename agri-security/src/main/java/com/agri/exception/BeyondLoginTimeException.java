package com.agri.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BeyondLoginTimeException extends RuntimeException{

    /**
     * 标记账户是否被锁定
     */
    private boolean status = false;

    private String result = "密码多次错误，账号已锁定";

    public BeyondLoginTimeException(int counts) {
        status = true;
        result = String.format("密码错误，还可尝试%d次", counts);
    }

    public boolean isStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }
}
