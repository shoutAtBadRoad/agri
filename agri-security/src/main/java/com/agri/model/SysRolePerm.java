package com.agri.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
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
public class SysRolePerm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableField("role_id")
    @MppMultiId
    private Long roleId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    @MppMultiId
    private Long permId;


}
