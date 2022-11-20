package com.agri.controller;


import com.agri.model.CommonResult;
import com.agri.model.UserAuditOrder;
import com.agri.security.model.LoginUser;
import com.agri.service.IUserAuditOrderService;
import com.agri.utils.PageUtil;
import com.agri.utils.annotation.SaveAuth;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jyp
 * @since 2022-11-12
 */
@RestController
@RequestMapping("/userAuditOrder")
@Log4j
public class UserAuditOrderController {

    @Resource
    private IUserAuditOrderService service;

    @PostMapping("/submit")
    @SaveAuth(roles = {"farmer"})
    public CommonResult<?> submitOrder(@ApiParam("工单表单")@RequestBody UserAuditOrder order) {
        CommonResult<?> res = null;
        if((res = preCheck(order)) != null) {
            return res;
        }
        LoginUser submitter = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = submitter.getUser().getUserid();
        order.setUserId(userId);
        order.setStatus(0);
        boolean saved = service.save(order);
        return CommonResult.OK("提交成功");
    }

    @PostMapping("/audit")
    @SaveAuth(roles = {"admin", "coder"})
    public CommonResult<?> auditOrder(@ApiParam("审核后的工单表单")@RequestBody UserAuditOrder order) {
        LoginUser auditor = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long auditId = auditor.getUser().getUserid();
        order.setAuditId(auditId);
        service.saveOrUpdate(order);
        service.changeUserStatus(order);
        return CommonResult.OK("审核完毕");
    }

    @PostMapping("/all")
    @SaveAuth(roles = {"admin", "farmer"})
    public CommonResult<?> getOrder(@ApiParam("获取审核工单的参数")@RequestBody Map<String, Object> params) {
        PageUtil<UserAuditOrder> util = new PageUtil<>();
        IPage<UserAuditOrder> page = null;
        try {
            page = util.page(params);
        }catch (Exception e) {
            log.error("请求工单参数异常");
            return CommonResult.error("参数错误");
        }
        IPage<UserAuditOrder> order = service.getOrder(params, page);
        return CommonResult.OK(order);
    }



    /**
     * 工单预检查
     * @param order
     * @return
     */
    public CommonResult preCheck(UserAuditOrder order) {
        //TODO
        return null;
    }

}
