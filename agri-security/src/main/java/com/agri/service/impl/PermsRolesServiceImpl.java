package com.agri.service.impl;

import com.agri.mapper.PermsMapper;
import com.agri.model.RedisConstant;
import com.agri.model.ResultSet;
import com.agri.model.ResultStatus;
import com.agri.service.PermsRolesService;
import com.agri.utils.RedisUtil;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PermsRolesServiceImpl implements PermsRolesService {
    
    @Autowired
    public RedisUtil redisUtil;
    
    @Autowired
    public RedisTemplate redisTemplate;
    
    @Resource
    public PermsMapper permsMapper;
    
    @Override
    public Map<String, List<String>> getPermsOfRoles() {
        Map<String, List<String>> map = redisUtil.hmget(RedisConstant.RESOURCE_ROLES_MAP);
        if(!redisUtil.hasKey(RedisConstant.RESOURCE_ROLES_MAP)) {
            map = refreshPermsMap();
        }
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
        // TODO 权限检查接口
        if(rolesList == null)
            return true;
        else {
            Set<String> set = new HashSet<>(permissions);
            set.retainAll(rolesList);
            return set.size() > 0;
        }
    }

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
