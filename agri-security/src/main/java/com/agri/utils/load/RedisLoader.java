package com.agri.utils.load;

import com.agri.utils.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 配合接口{@link ResourceLoadService}使用
 * 1、传入一个ResourceLoadService的实现类，重写{@link ResourceLoadService#load}方法
 * 2、load是资源加载逻辑：如从数据库加载到内存，再缓存到redis
 * 3、getResource是资源的获取逻辑，如：redis中的string、map、set、list、map中的k-v获取,需要定制实现
 * @author jyp
 * @since 2020-9-7
 */
@Component
@Slf4j
public class RedisLoader extends LoadCacheWhitLock {

    public static final ConcurrentHashMap<String, ResourceLoadService> services = new ConcurrentHashMap<>();

    LocalLockValue value = new LocalLockValue();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5,10,60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    @Qualifier(value = "redisScript")
    private RedisScript<Long> delLock;

    @Autowired
    @Qualifier(value = "isSameVal")
    private RedisScript<Long> isSame;

    @Override
    public Object loadWithLock(ResourceLoadService service, String lockKey, String rKey, Class<?> clazz, Object... args) {
        return super.loadWithLock(service, lockKey, rKey, clazz, args);
    }

    public Object loadWithLock(ResourceLoadService service, String lockKey, String rKey) {
        return loadWithLock(service, lockKey, rKey, null);
    }

    @Override
    public Object getResource(String rKey, Class<?> clazz, Object... args) {
        if(clazz == null) {
            return redisUtil.get(rKey);
        }
        if(clazz == Map.class) {
            // map的情况
            Map map = redisUtil.hmget(rKey);
            return map.size()==0 ? null : map;
        }else if(clazz == List.class) {
            // 数组的情况
        }else if(clazz == Set.class){
            // set的情况
        }
        // string的情况
        return redisUtil.get(rKey);
    }


    @Override
    public boolean doLock(String lockKey) {
        String val = value.get();
        log.info(Thread.currentThread().getName() + "尝试获取锁");
        return redisUtil.setIfAbsent(lockKey, val, 1000000);
    }

    /**
     * 需要使用lua，将val值的判断和删除变成原子操作
     * @param lockKey
     */
    @Override
    public void releaseLock(String lockKey) {
        String val = value.get();
        List<String> list = new ArrayList<>();
        list.add(lockKey);
        redisUtil.execute(delLock, list, val);
        log.info(Thread.currentThread().getName() + "释放锁");
        value.remove();
    }

    @Override
    public void extendLock(String lockKey) {
        log.info(Thread.currentThread().getName() + ": 看门狗启动");
        extendTask task = new extendTask(lockKey, value.get(), redisUtil, isSame);
        executor.execute(task);
    }

    static class LocalLockValue extends ThreadLocal<String> {
        @Override
        protected String initialValue() {
            return UUID.randomUUID().toString();
        }
    }

    @AllArgsConstructor
    static class extendTask implements Runnable {

        public String lockKey;

        public String value;

        public RedisUtil redisUtil;

        public RedisScript<Long> isSame;

        // public StringRedisTemplate stringRedisTemplate;


        @SneakyThrows
        @Override
        public void run() {
            Thread.sleep(5000);
            /**
             * 这里有个坑，配置了fastjson的序列化器之后，存储在redis-value中的string是jsonobject形式的
             * 而在lua脚本中传入的string就是string本身，不是json字符串格式的
             * redis中的string都是"\"str\""这种形式的，直接传入string自然不能相等，所以要先转成json格式的
             */
            /**
             * 针对上述问题，主要还是分别使用了redisTemplate和StringRedisTemplate，一个用了序列化器，一个没有，所以配合不了，现在全部换成RedisTemplate
             */
//            String s = JSONObject.toJSONString(value);
            List<String> list = new ArrayList<>();
            list.add(lockKey);
            Long v = (Long) redisUtil.execute(isSame, list, value, 10);
            while (v == 1L) {
                log.info("看门狗给lock续期了");
                Thread.sleep(5000);
                v = (Long) redisUtil.execute(isSame, list, value, 10);
            }
        }
    }
}

