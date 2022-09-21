package com.agri.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class ResultSet<T> implements Serializable {

    @JSONField(name = "code")
    private Integer code;
    @JSONField(name = "body")
    private T body;
    @JSONField(name = "msg")
    private String msg;

    public static <T> ResultSet<T> create(ResultStatus status, T body) {
        return new ResultSet<T>(status.getCode(), body, status.getReasonPhrase());
    }

    public static <T> ResultSet<T> OK(T body) {
        return OK(ResultStatus.OK, body);
    }

    public static <T>  ResultSet<T> OK(ResultStatus status, T body, String message) {
        ResultSet<T> resultSet = new ResultSet<>(status.getCode(), body, message);
        return resultSet;
    }

    public static <T> ResultSet<T> OK(ResultStatus status, T body) {
        return  OK(status, body, null);
    }

    public static <T>  ResultSet<T> error(T body) {
        ResultSet<T> resultSet = new ResultSet<>(ResultStatus.ERROR.getCode(), body, null);
        return resultSet;
    }

    public static <T>  ResultSet<T> error(ResultStatus status, T body, String message) {
        ResultSet<T> resultSet = new ResultSet<>(status.getCode(), body, message);
        return resultSet;
    }

    public static <T> ResultSet<T> error(ResultStatus status, T body) {
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
