package com.lanzong.spring.framework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

//完成URL和Controller方法的对应关系（通过url找到对应controller的方法）
//保存Controller中配置的RequestMapping和Method的对应关系
//策略模式，根据不同的URL调用不同的Method
public class LZHandlerMapping {
    private Object controller;
    private Method method;
    private Pattern pattern;

    public LZHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
