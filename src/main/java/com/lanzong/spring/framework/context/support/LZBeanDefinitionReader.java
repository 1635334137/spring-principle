package com.lanzong.spring.framework.context.support;

import com.lanzong.spring.framework.beans.config.LZBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//完成对application.properties配置文件的解析工作
//对配置文件进行查找、读取、解析
public class LZBeanDefinitionReader {
    private List<String> registyBeanClasses = new ArrayList<>();
    private Properties config = new Properties();

    //固定配置文件中的key，相对于XML的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public LZBeanDefinitionReader(String... locations){
        //通过URL定位找到其所对应的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            //load：从输入流中读取属性列表（键和元素对）
            config.load(is);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(null!= is){
                try {
                    is.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        //读取传入的配置文件的key值为：scanPackage
        doScanner(config.getProperty(SCAN_PACKAGE));
    }


    private void doScanner(String scanPackage) {
        //System.out.println("Namessss:"+scanPackage);
        //转换为文件路径，实际上就是把.替换为/
        URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()){
            if (file.isDirectory()){//如果是目录（包名）则递归到文件
                doScanner(scanPackage+"."+file.getName());
            }else{
                if(!file.getName().endsWith(".class")){//如果不是class文件则跳过
                    continue;
                }
                String className = (scanPackage+"."+file.getName().replace(".class",""));
                //获取类的全路径然后存储到Array数组中
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return config;
    }

    //把配置文件中扫描的所有配置信息转换为Beanfinition对象，以便于之后的IoC操作
    public List<LZBeanDefinition> loadBeanDefinitions(){
        List<LZBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registyBeanClasses){
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){//判定指定的Class对象是否表示一个接口类型
                    continue;
                }
                //getSimpleName 返回源代码中给出的底层类的简称
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces){
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //把配置信息解析成一个Beanfinition
    private LZBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        LZBeanDefinition beanDefinition = new LZBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    //将类名首字母改为小写
    //这是简化版的
    private String toLowerFirstCase(String simpleName){
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
