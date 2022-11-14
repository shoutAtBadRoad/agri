package com.agri.service;

import com.agri.model.UserAuditOrder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jyp
 * @since 2022-11-12
 */
public interface IUserAuditOrderService extends IService<UserAuditOrder> {

    public IPage<UserAuditOrder> getOrder(Map<String, Object> params, IPage<UserAuditOrder> page);

    public void changeUserStatus(UserAuditOrder order);

}
