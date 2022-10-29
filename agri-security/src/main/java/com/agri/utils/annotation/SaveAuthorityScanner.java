package com.agri.utils.annotation;

import com.agri.mapper.SysPermMapper;
import com.agri.mapper.SysRoleMapper;
import com.agri.model.SysPerm;
import com.agri.model.SysRole;
import com.agri.model.SysRolePerm;
import com.agri.service.ISysRolePermService;
import com.agri.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.log4j.Log4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j
public class SaveAuthorityScanner  implements BeanPostProcessor {

    @Autowired
    @Qualifier("redisUtil")
    private RedisUtil redisUtil;

    @Autowired
    private ISysRolePermService RolePermService;

    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysPermMapper permMapper;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        RequestMapping annotation = targetClass.getAnnotation(RequestMapping.class);
        if(annotation == null)
            return bean;
        String prefix = annotation.value()[0];
        Method[] methods = targetClass.getDeclaredMethods();

        for(Method m : methods) {
            List<Long> roleIds = new ArrayList<>();
            SaveAuth saveAuth = m.getAnnotation(SaveAuth.class);
            PostMapping annotation1 = m.getAnnotation(PostMapping.class);
            if(saveAuth == null || annotation1 == null) {
                continue;
            }
            String[] auths = saveAuth.roles();
            String postfix = annotation1.value()[0];
            String url = prefix + postfix;
            log.info("url : " + url + "\n" + "auths:" + Arrays.toString(auths));
//            redisUtil.lSet(url, CollectionUtils.arrayToList(auths));
            // 先存入角色信息
//            if(url.equals("/sysUser/revisePass")) {
                for (String auth : auths) {
                    SysRole role = roleMapper.selectOne(new QueryWrapper<SysRole>().eq("role_key", auth));
                    if (role == null) {
                        role = new SysRole();
                        role.setName(auth);
                        role.setRoleKey(auth);
                        roleMapper.insert(role);
                    }
                    roleIds.add(role.getId());
                }
                // 存入权限信息
                SysPerm perm = new SysPerm();
                perm.setPermName("先没名字");
                perm.setPath(url);
                permMapper.insert(perm);
                Long permId = perm.getId();
                // 存入角色权限关系
                SysRolePerm sysRolePerm = new SysRolePerm();
                sysRolePerm.setPermId(permId);
                for (Long roleId : roleIds) {
                    sysRolePerm.setRoleId(roleId);
                    RolePermService.save(sysRolePerm);
                }
            }
//        }
        return bean;
    }
}
