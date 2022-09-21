package com.agri.security.sms;

import com.agri.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

/**
 * 短信发送监听器
 * @author jyp
 * @since 2022-9-20
 */
@Component
@Order(1)
@Slf4j
public class SmsListener implements ApplicationListener<SmsSendEvent> {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void onApplicationEvent(SmsSendEvent smsSendEvent) {
        //TODO 加入发送短信逻辑
        String code = getCode();
        redisUtil.set(smsSendEvent.getPhoneNumber(), code, 1000*60*10);
        log.info("成功发送短信：" + smsSendEvent.getPhoneNumber() + "----" + code);
    }

    private String getCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<6; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }
}
