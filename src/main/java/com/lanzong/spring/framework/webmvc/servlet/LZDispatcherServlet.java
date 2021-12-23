package com.lanzong.spring.framework.webmvc.servlet;

import com.lanzong.spring.framework.annotation.LZController;
import com.lanzong.spring.framework.annotation.LZRequestMapping;
import com.lanzong.spring.framework.context.LZApplicationContext;
import com.lanzong.spring.framework.webmvc.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//作为MVC的启动入口
public class LZDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    private List<LZHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<LZHandlerMapping,LZHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<LZViewResolver> viewResolvers = new ArrayList<>();

    private LZApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req,HttpServletResponse resp)throws Exception{
        LZHandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req,resp,new LZModelAndView("404"));
            return;
        }
        LZHandlerAdapter ha = getHandlerAdapter(handler);

        LZModelAndView mv = ha.handle(req,resp,handler);

        processDispatchResult(req,resp,mv);
    }

    private LZHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){return null;}
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        for (LZHandlerMapping handler : this.handlerMappings){
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){continue;}
            return handler;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LZModelAndView mv) throws Exception {
        if(null == mv){return;}
        if(this.viewResolvers.isEmpty()){return;}

        if(this.viewResolvers != null){
            for (LZViewResolver viewResolver : this.viewResolvers){
                LZView view = viewResolver.resolveViewName(mv.getViewName(),null);
                if(view != null){
                    view.render(mv.getModel(),req,resp);
                    return;
                }
            }
        }
    }

    private LZHandlerAdapter getHandlerAdapter(LZHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        LZHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //在这里把IoC容器初始化了
        context = new LZApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    protected void initStrategies(LZApplicationContext context) {

        initMultipartResolver(context);//文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析

        initHandlerMappings(context);//
        initHandlerAdapters(context);//

        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);

        initViewResolvers(context);

        initFlashMapManager(context);
    }


    private void initFlashMapManager(LZApplicationContext context) {}
    private void initRequestToViewNameTranslator(LZApplicationContext context) {}
    private void initHandlerExceptionResolvers(LZApplicationContext context) {}
    private void initThemeResolver(LZApplicationContext context) {}
    private void initLocaleResolver(LZApplicationContext context) {}
    private void initMultipartResolver(LZApplicationContext context) {}

    private void initHandlerMappings(LZApplicationContext context) {

        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames){
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(LZController.class)){
                    continue;
                }
                String baseUrl = "";
                if(clazz.isAnnotationPresent(LZRequestMapping.class)){
                    LZRequestMapping requestMapping = clazz.getAnnotation(LZRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                Method[] methods = clazz.getMethods();
                for (Method method : methods){
                    if(!method.isAnnotationPresent(LZRequestMapping.class)){
                        continue;
                    }
                    LZRequestMapping requestMapping = method.getAnnotation(LZRequestMapping.class);
                    String regex = ("/"+baseUrl+requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new LZHandlerMapping(pattern,controller,method));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(LZApplicationContext context) {
        for (LZHandlerMapping handlerMapping : this.handlerMappings){
            this.handlerAdapters.put(handlerMapping,new LZHandlerAdapter());
        }
    }

    private void initViewResolvers(LZApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);

        for (File template : templateRootDir.listFiles()){
            this.viewResolvers.add(new LZViewResolver(templateRoot));
        }
    }
}
