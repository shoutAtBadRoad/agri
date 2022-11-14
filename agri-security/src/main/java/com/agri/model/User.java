package com.agri.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("sys_user")
public class User {

    /**
     * 用戶id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userid;

    /**
     * 用戶名
     */
    @TableField("user_name")
    private String username;

    @TableField("nick_name")
    private String nickname;

    /**
     * 密碼
     */
    private String password;

    /**
     * 手机号
     */
    private String phonenumber;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("del_flag")
    private Integer delFlag;

    private Integer status;

}
