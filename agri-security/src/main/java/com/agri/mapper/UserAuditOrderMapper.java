package com.agri.mapper;


import com.agri.model.UserAuditOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jyp
 * @since 2022-11-12
 */
public interface UserAuditOrderMapper extends BaseMapper<UserAuditOrder> {

    public IPage<UserAuditOrder> getOrders(Map<String, Object> params, IPage<UserAuditOrder> page);

}
