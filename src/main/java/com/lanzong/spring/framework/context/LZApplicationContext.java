package com.lanzong.spring.framework.context;

import com.lanzong.spring.framework.annotation.LZAutowired;
import com.lanzong.spring.framework.annotation.LZController;
import com.lanzong.spring.framework.annotation.LZService;
import com.lanzong.spring.framework.beans.LZBeanWrapper;
import com.lanzong.spring.framework.beans.config.LZBeanDefinition;
import com.lanzong.spring.framework.beans.config.LZBeanPostProcessor;
import com.lanzong.spring.framework.context.support.LZBeanDefinitionReader;
import com.lanzong.spring.framework.context.support.LZDefaultListableBeanFactory;
import com.lanzong.spring.framework.core.LZBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 直接接触用户的入口：和顶层设计的关系：本类 --> 父类 --> 接口（实现接口的refresh，众多公共逻辑之一）
 * IoC 控制反转 以及 DI 依赖注入
 *
 */
public class LZApplicationContext extends LZDefaultListableBeanFactory implements LZBeanFactory {

    private String[] configLocations;//配置位置
    private LZBeanDefinitionReader reader;

    //单例的IoC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    //通用的IoC容器（存储创建好的封装实例）
    private Map<String, LZBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public LZApplicationContext(String... configLoactions) {
        this.configLocations = configLoactions;
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //依赖注入【DI】，从这里开始，读取BeanDefinition中的信息
    //然后通过反射机制创建一个实例并返回
    //Spring不会直接把原始对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：1.保留原来的OOP关系 2.需要对它进行扩展、增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {
        //bean初始化的具体实现

        LZBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        try {
            //用于在初始化之前和之后处理的回调接口
            LZBeanPostProcessor beanPostProcessor = new LZBeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);
            if(null == instance){
                return null;
            }
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            LZBeanWrapper beanWrapper = new LZBeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanName,beanWrapper);
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            populateBean(beanName,instance);//注解相关处理
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //传入一个beanDefinition，就返回一个实例Bean
    private Object instantiateBean(LZBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if(this.factoryBeanObjectCache.containsKey(className)){
                instance = this.factoryBeanObjectCache.get(className);
            }else {
                //通过反射创建实例
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //存入单例容器
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }

    private void populateBean(String beanName,Object instance) {
        Class clazz = instance.getClass();
        // isAnnotationPresent 如果指定类型的注释存在于此元素上，则返回 true，否则返回 false
        if(!(clazz.isAnnotationPresent(LZController.class)||
                clazz.isAnnotationPresent(LZService.class))){
            return;
        }

        //Field 提供有关类或接口的单个字段的信息，以及对它的动态访问权限
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            if(!field.isAnnotationPresent(LZAutowired.class)){
                continue;
            }
            LZAutowired autowired = field.getAnnotation(LZAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    @Override
    public void refresh() throws Exception {
        //1.定位配置文件位置
        reader = new LZBeanDefinitionReader(this.configLocations);

        //2.加载配置文件，扫描相关的类，把他们封装成beanDefinition
        List<LZBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3.注册，把配置信息放到容器里面（伪IoC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4.把不是延时加载的类提前初始化
        doAutowrited();
    }

    private void doRegisterBeanDefinition(List<LZBeanDefinition> beanDefinitions) throws Exception {
        for (LZBeanDefinition beanDefinition : beanDefinitions){
            //ConcurrentHashMap实现的伪IoC容器
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The" + beanDefinition.getFactoryBeanName() +"is exists!");
            }
            //把类存储到伪容器中
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }

    private void doAutowrited() {
        for (Map.Entry<String,LZBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){//根据传入的Class的方法调用判断是否是延时加载，即LZBeanDefinition.isLazyInit
                try {
                    //不是延时加载的类进行初始化
                    getBean(beanName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    //获取配置实例，查看配置文件相关信息
    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
