package com.lanzong.spring.framework.context.support;

import com.lanzong.spring.framework.beans.config.LZBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//是众多IoC容器子类的典型代表
public class LZDefaultListableBeanFactory extends LZAbstractApplicationContext{

    //存储注册信息的BeanDefinition
    protected final Map<String, LZBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
}
