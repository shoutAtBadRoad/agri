package com.agri.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultSet<T> {

    private Integer code;
    private T body;
    private String msg;


    public static <T>  ResultSet<T> OK(ResultStatus status, T body, String message) {
        ResultSet<T> resultSet = new ResultSet<>(status.getCode(), body, message);
        return resultSet;
    }

    public static <T> ResultSet<T> OK(ResultStatus status, T body) {
        return  OK(status, body, null);
    }

    public static <T>  ResultSet<T> error(ResultStatus status, T body, String message) {
        ResultSet<T> resultSet = new ResultSet<>(status.getCode(), body, message);
        return resultSet;
    }

    public static <T> ResultSet<T> error(ResultStatus status, T body) {
        return  OK(status, body, null);
    }
}
