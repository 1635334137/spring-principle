package com.lanzong.spring.framework.webmvc;

import java.io.File;
import java.util.Locale;

//完成模板名称和模板解析引擎的匹配
public class LZViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;
    private String viewName;

    public LZViewResolver(String templateRoot){
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public LZView resolveViewName(String viewName, Locale locale)throws Exception{
        this.viewName = viewName;
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:(viewName+DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath()+"/"+viewName).replaceAll("/+","/"));
        return new LZView(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
