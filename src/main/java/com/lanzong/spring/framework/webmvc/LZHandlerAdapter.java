package com.lanzong.spring.framework.webmvc;

import com.lanzong.spring.framework.annotation.LZRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//用来动态匹配Method参数，包括类转换，动态赋值
//适配器模式
public class LZHandlerAdapter {
    public boolean supports(Object handler){
        return (handler instanceof LZHandlerMapping);
    }

    public LZModelAndView handle(HttpServletRequest req, HttpServletResponse resp,Object handler) throws Exception{
        LZHandlerMapping handlerMapping = (LZHandlerMapping) handler;

        //每个方法有一个参数列表，这里保存的是形参列表
        Map<String,Integer> paramMapping = new HashMap<>();

        //这里处理使用@LZRequestParam("xxx")注解的参数，不使用注解的不处理
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]){
                if(a instanceof LZRequestParam){
                    String paramName = ((LZRequestParam)a).value();
                    if(!"".equals(paramName.trim())){
                        paramMapping.put(paramName,i);
                    }
                }
            }
        }

        //处理顺序为：参数个数、类型、顺序、方法名

        //处理参数的类型
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramMapping.put(type.getName(),i);
            }

        }

        //用户通过URL传来的参数列表
        Map<String,String[]> reqParameterMap = req.getParameterMap();

        //构造实参列表
        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String,String[]> param : reqParameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");
            if(!paramMapping.containsKey(param.getKey())){continue;}
            int index = paramMapping.get(param.getKey());
            //页面传来的都是String类型的，而方法上定义的类似千变万化的，所以要进行类型转换
            paramValues[index] = caseStringValue(value,paramTypes[index]);
        }

        if(paramMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }
        if(paramMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        //从handler中取出Controller、Method然后利用反射进行调用
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);

        if(result == null){return null;}
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == LZModelAndView.class;
        if(isModelAndView){
            return (LZModelAndView) result;
        }else {
            return null;
        }
    }

    //处理传来的参数和方法参数类型不一致，进行类型转换的处理类，这里只处理了String、Integer、int三种
    private Object caseStringValue(String value, Class<?> clazz) {
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            return Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {
            return null;
        }
    }
}
