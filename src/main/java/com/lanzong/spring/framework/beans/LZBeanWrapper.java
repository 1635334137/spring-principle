package com.lanzong.spring.framework.beans;

//用于封装创建后的对象实例
public class LZBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public LZBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedClass.getClass();
    }
}
