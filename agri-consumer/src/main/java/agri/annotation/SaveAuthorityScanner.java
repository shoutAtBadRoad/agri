package agri.annotation;

import agri.utils.RedisUtil;
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

@Component
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
            System.out.println("url : " + url + "\n" + "auths:" + Arrays.toString(auths));
            redisUtil.lSet(url, CollectionUtils.arrayToList(auths));
        }
        return bean;
    }
}
