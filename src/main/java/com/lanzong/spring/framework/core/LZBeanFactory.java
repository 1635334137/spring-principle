package com.lanzong.spring.framework.core;

/**
 * 单例工厂的顶层设计
 */
public interface LZBeanFactory {

    //根据beanName从IoC容器中获得一个实例Bean
    Object getBean(String beanName) throws Exception;

    public Object getBean(Class<?> beanClass) throws Exception;
}
