package com.agri.exception;

import com.agri.model.SysUser;
import com.agri.model.User;

import java.util.List;

public class DuplicateUserException extends Exception{

    private List<SysUser> userList;

    public DuplicateUserException(List<SysUser> userList) {
        this.userList = userList;
    }

    public List<SysUser> getUserList() {
        return userList;
    }

    public void setUserList(List<SysUser> userList) {
        this.userList = userList;
    }
}
