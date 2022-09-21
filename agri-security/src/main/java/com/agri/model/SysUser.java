package com.agri.model;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jyp
 * @since 2022-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysUser extends User implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主鍵
     */
    @TableId
    private Long userid;

    /**
     * 用戶名
     */
    private String userName;

    /**
     * 暱稱
     */
    private String nickName;

    /**
     * 密碼
     */
    private String password;

    /**
     * 賬號狀態（0正常 1停用）
     */
    private String status;

    /**
     * 郵箱
     */
    private String email;

    /**
     * 手機號
     */
    private String phonenumber;

    /**
     * 性別（0男 1女 2未知）
     */
    private String sex;

    /**
     * 頭像
     */
    private String avatar;

    /**
     * 用戶類型（0管理員 1普通用戶）
     */
    private String userType;

    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private Long updateBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 刪除標誌（0未刪除 1已刪除）
     */
    private Integer delFlag;


}
