package com.agri.service.impl;


import com.agri.mapper.UserAuditOrderMapper;
import com.agri.model.UserAuditOrder;
import com.agri.service.IUserAuditOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jyp
 * @since 2022-11-12
 */
@Service
public class UserAuditOrderServiceImpl extends ServiceImpl<UserAuditOrderMapper, UserAuditOrder> implements IUserAuditOrderService {

    @Resource
    private UserAuditOrderMapper mapper;

    @Override
    public IPage<UserAuditOrder> getOrder(Map<String, Object> params, IPage<UserAuditOrder> page) {
        IPage<UserAuditOrder> orders = mapper.getOrders(params, page);
        return orders;
    }

    @Override
    public void changeUserStatus(UserAuditOrder order) {

    }
}
