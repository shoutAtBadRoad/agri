package com.agri.utils.annotation;

import com.agri.utils.RedisUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;

@Log4j
public class SaveAuthorityScanner  implements BeanPostProcessor {

    @Autowired
    @Qualifier("redisUtil")
    private RedisUtil redisUtil;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        RequestMapping annotation = targetClass.getAnnotation(RequestMapping.class);
        if(annotation == null)
            return bean;
        String prefix = annotation.value()[0];
        Method[] methods = targetClass.getDeclaredMethods();
        for(Method m : methods) {
            SaveAuth saveAuth = m.getAnnotation(SaveAuth.class);
            PostMapping annotation1 = m.getAnnotation(PostMapping.class);
            if(saveAuth == null || annotation1 == null) {
                continue;
            }
            String[] auths = saveAuth.roles();
            String postfix = annotation1.value()[0];
            String url = prefix + postfix;
            log.info("url : " + url + "\n" + "auths:" + Arrays.toString(auths));
            redisUtil.lSet(url, CollectionUtils.arrayToList(auths));
        }
        return bean;
    }
}
