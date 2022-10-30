package com.agri.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> implements Serializable {

    @JSONField(name = "code")
    private Integer code;
    @JSONField(name = "body")
    private T body;
    @JSONField(name = "msg")
    private String msg;

    public static <T> CommonResult<T> create(ResultStatus status, T body) {
        return new CommonResult<T>(status.getCode(), body, status.getReasonPhrase());
    }

    public static <T> CommonResult<T> OK(T body) {
        return OK(ResultStatus.OK, body);
    }

    public static <T>  CommonResult<T> OK(ResultStatus status, T body, String message) {
        CommonResult<T> CommonResult = new CommonResult<>(status.getCode(), body, message);
        return CommonResult;
    }

    public static <T> CommonResult<T> OK(ResultStatus status, T body) {
        return  OK(status, body, null);
    }

    public static <T>  CommonResult<T> error(T body) {
        CommonResult<T> CommonResult = new CommonResult<>(ResultStatus.ERROR.getCode(), body, null);
        return CommonResult;
    }

    public static <T>  CommonResult<T> error(ResultStatus status, T body, String message) {
        CommonResult<T> CommonResult = new CommonResult<>(status.getCode(), body, message);
        return CommonResult;
    }

    public static <T> CommonResult<T> error(ResultStatus status, T body) {
        return  OK(status, body, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
