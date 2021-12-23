package com.lanzong.spring.framework.beans.config;

/**
 * 用来存储配置文件中的信息
 * 相当于保存在内存中的配置
 * Bean定义
 */
public class LZBeanDefinition {
    private String beanClassName;//原生bean的全类名
    private boolean lazyInit = false;//标记是否延迟加载
    private String factoryBeanName;//保存beanName，在IoC容器中存储的key

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
