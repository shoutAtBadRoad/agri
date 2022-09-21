package com.agri.security.sms;

import org.springframework.context.ApplicationEvent;

/**
 * 短信发送驱动时间  {@link SmsSendService#sendSms(String)}
 * @author jyp 
 * @since 2022-9-20
 */
public class SmsSendEvent extends ApplicationEvent {

    private final String phoneNumber;

    public SmsSendEvent(Object source, String phoneNumber) {
        super(source);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
