package com.agri.model;

public enum ResultStatus {


    OK(200),
    AUTHORITY(401),
    REJECT(403),
    NOTFOUND(404),
    ERROR(500)
    ;

    private Integer code;

    ResultStatus(int i) {
    }

    public Integer getCode() {
        return code;
    }
}
