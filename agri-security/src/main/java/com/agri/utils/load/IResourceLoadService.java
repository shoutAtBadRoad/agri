package com.agri.utils.load;

/**
 * {@link ResourceLoadService} 的子类接口，load方法少了joinpoint的参数
 * 在方法中创建使用，不会被作用于AOP中，这样使用的原因是AOP代理的方法中调用原对象的方法时并不是调用的代理对象
 * 所以只能在需要加锁的方法中获取AOP的容器来执行代理对象的方法，这样就与AOP强耦合了，所以目前就使用这样一个方案
 * 创建匿名内部类，用于分布式锁执行
 * load逻辑是
 *   数据库加载资源
 *   缓存到redis
 *   返回资源
 */
public interface IResourceLoadService extends ResourceLoadService{

    Object load(String key, String lock) throws Throwable;

}
