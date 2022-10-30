package com.agri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

/**
 * 配置事件广播器
 */
@Configuration
public class EventMulticasterConfig {
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster result = new SimpleApplicationEventMulticaster();
        result.setTaskExecutor(this.applicationEventMulticasterThreadPool().getObject());
        return result;
    }

    @Bean
    public ThreadPoolExecutorFactoryBean applicationEventMulticasterThreadPool() {
        ThreadPoolExecutorFactoryBean result = new ThreadPoolExecutorFactoryBean();
        result.setThreadNamePrefix("Sms-");
        result.setCorePoolSize(5);
        return result;
    }
}
