package com.agri.security.sms;

import com.agri.security.sms.SmsSendEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SmsSendService implements ApplicationEventPublisherAware {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void sendSms(String phone) {
        // 通知事件会去调用Sms接口发送短信
        this.applicationEventPublisher.publishEvent(new SmsSendEvent(this, phone));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
