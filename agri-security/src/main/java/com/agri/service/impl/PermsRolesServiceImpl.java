package com.agri.service.impl;

import com.agri.mapper.PermsMapper;
import com.agri.model.RedisConstant;
import com.agri.service.PermsRolesService;
import com.agri.utils.RedisUtil;
import com.agri.utils.annotation.lock.Locked;
import com.agri.utils.annotation.lock.RKey;
import com.agri.utils.load.IResourceLoadService;
import com.agri.utils.load.RedisLoader;
import com.agri.utils.load.ResourceLoadService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class PermsRolesServiceImpl implements PermsRolesService {
    
    @Autowired
    public RedisUtil redisUtil;
    
    @Autowired
    public RedisTemplate redisTemplate;
    
    @Resource
    public PermsMapper permsMapper;

    @Resource
    private RedisLoader loader;

    /**
     * 加分布式锁
     * @return
     */
    @Override
    public Map<String, List<String>> getPermsOfRoles() {

        if(redisUtil.hasKey(RedisConstant.RESOURCE_ROLES_MAP)) {
            return redisUtil.hmget(RedisConstant.RESOURCE_ROLES_MAP);
        }

        // 分布式锁的名字，对所有调用此方法的线程唯一
        String lockKey = RedisConstant.LOCK_RESOURCE_ROLES_MAP;
        // 缓存中获取资源加载的方法对象
        ConcurrentHashMap<String, ResourceLoadService> services = RedisLoader.services;
        ResourceLoadService service = services.getOrDefault(lockKey, new IResourceLoadService() {
            @Override
            public Object load(String key, String lock) throws Throwable {
                List<Map<String, String>> permsOfRole = permsMapper.getPermsOfRole();
                Map<String, List<String>> map = new HashMap<>();
                for (Map<String, String> perms : permsOfRole) {
                    String k = perms.get("path");
                    List<String> perm = map.getOrDefault(k, new ArrayList<String>());
                    perm.add(perms.get("name"));
                    map.put(k, perm);
                }
                redisUtil.hmset(RedisConstant.RESOURCE_ROLES_MAP, map);
                return map;
            }

            @Override
            public Object load(String key, String lock, ProceedingJoinPoint joinPoint) throws Throwable {
                return null;
            }
        });
        if(!services.containsKey(lockKey))
            services.put(lockKey, service);
//        }
        // 调用loader的loadWithLock方法进行加锁获取资源
        Map<String, List<String>> map = (Map<String, List<String>>) loader.loadWithLock(service, lockKey, RedisConstant.RESOURCE_ROLES_MAP, Map.class);
        return map;
    }

    @Override
    public List<String> getRoles(String uri) {
        Map<String, List<String>> permsOfRoles = getPermsOfRoles();
        return permsOfRoles.get(uri);
    }

    @Override
    public Boolean checkPerms(String uri, List<String> permissions) {
        List<String> rolesList = getRoles(uri);
        // 默认不检查权限
        if(rolesList == null || rolesList.contains("guest"))
            return true;
        // TODO 权限检查接口
        else {
            Set<String> set = new HashSet<>(permissions);
            set.retainAll(rolesList);
            return set.size() > 0;
        }
    }

    @Override
    public void deletePermsOfRolesInRedis() {
        redisUtil.del(RedisConstant.RESOURCE_ROLES_MAP);
    }

    @Deprecated
    public Map<String, List<String>> refreshPermsMap() {
        Map<String, List<String>> map = null;
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(RedisConstant.LOCK_RESOURCE_ROLES_MAP, 1, 10, TimeUnit.SECONDS);
        if(ifAbsent != null)
            if(ifAbsent) {
                List<Map<String, String>> permsOfRole = permsMapper.getPermsOfRole();
                map = new HashMap<>();
                for (Map<String, String> perms : permsOfRole) {
                    String key = perms.get("path");
                    List<String> perm = map.getOrDefault(key, new ArrayList<String>());
                    perm.add(perms.get("name"));
                    map.put(key, perm);
                }
                redisUtil.hmset(RedisConstant.RESOURCE_ROLES_MAP, map);
                redisUtil.del(RedisConstant.LOCK_RESOURCE_ROLES_MAP);
            }else {
                while (true) {
                    boolean b = redisUtil.hasKey(RedisConstant.LOCK_RESOURCE_ROLES_MAP);
                    if(!b)
                        break;
                    try {
                        Thread.sleep(500);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        return map;
    }
    

}
