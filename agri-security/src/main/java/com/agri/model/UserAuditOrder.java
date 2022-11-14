package com.agri.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 小农用户的身份认证工单
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user_audit_order")
public class UserAuditOrder {

    /**
     * 工单自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人Id
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号码
     */
    private String identityNumber;

    /**
     * 身份证正面照片
     */
    private String identityImageFront;

    /**
     * 身份证反面照片
     */
    private String identityImageBack;

    /**
     * 审核人id
     */
    private Long auditId;

    /**
     * 审核意见
     */
    private String auditSuggestion;

    /**
     * 工单状态，0待审核，1审核不通过，2审核通过
     */
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
