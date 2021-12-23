package com.lanzong.spring.framework.context;

public interface LZApplicationContextAware {
    //钩子方法，自主选择获取到IoC容器的上下文
    void setApplicationContext(LZApplicationContext applicationContext);
}
