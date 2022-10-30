package com.agri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * 注入lua脚本实例
 * {@link com.agri.utils.RedisUtil#execute(RedisScript, List, Object...)} 负责执行脚本
 * @author jyp
 * @since 2022/9/15
 */
@Configuration(proxyBeanMethods = false)
public class LuaRedisAutoConfiguration {

    @Bean
    public DefaultRedisScript<Long> redisScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        //resource目录下的scripts文件下的.lua文件
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/lockdel.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<Long> isSameVal() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        //resource目录下的scripts文件下的.lua文件
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/isSameVal.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }


}
