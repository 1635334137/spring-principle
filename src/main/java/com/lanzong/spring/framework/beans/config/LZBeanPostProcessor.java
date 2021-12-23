package com.lanzong.spring.framework.beans.config;

//只做说明，不做具体实现
public class LZBeanPostProcessor {

    //为在bean的初始化之前提供回调入口
    public Object postProcessBeforeInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }

    //为在bean的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }
}
