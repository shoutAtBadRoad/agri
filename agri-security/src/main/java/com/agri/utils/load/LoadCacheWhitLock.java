package com.agri.utils.load;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Objects;

/**
 * @author jyp
 * 分布式锁抽象类
 * loadWithLock方法：加锁、获取资源、解锁的主逻辑
 * 默认实现类 {@link RedisLoader}
 */
@Slf4j
public abstract class LoadCacheWhitLock {

    public Object loadWithLock(ResourceLoadService service, String lockKey, String rKey, Class<?> clazz, Object... args) {
        // 获取分布式锁
        try {
//            Object resource = getResource(rKey, clazz, service, args);
//            if(!Objects.isNull(resource))
//                return resource;
            while (!doLock(lockKey)) {
                Thread.sleep(200);
            }
            log.info(Thread.currentThread().getName() + "获取到了锁");
            // 检查资源是否存在
            Object resource = getResource(rKey, clazz, service, args);
            if(!Objects.isNull(resource)) {
                log.info(Thread.currentThread().getName() + "直接拿到了资源");
                return resource;
            }
            // 异步启动watch dog，进行lock续期
            extendLock(lockKey);
            // 不存在进行加载
//            Thread.sleep(12000);
            //
            Object object;
            if (args.length == 1)
                object = service.load(rKey, lockKey, (ProceedingJoinPoint) args[0]);
            else {
                IResourceLoadService service1 = (IResourceLoadService) service;
                object = service1.load(rKey, lockKey);
            }
            // 返回资源
            if(!Objects.isNull(clazz))
                return object;
            return getResource(rKey, null, service, args);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("资源加载异常");
        } catch (Throwable throwable) {
            throw new RuntimeException();
        } finally {
            // 释放锁
            releaseLock(lockKey);
        }
    }



    public abstract Object getResource(String rKey, Class<?> clazz, Object... args);

    public abstract boolean doLock(String LockKey);

    public abstract void releaseLock(String lockKey);

    public abstract void extendLock(String lockKey);

}
