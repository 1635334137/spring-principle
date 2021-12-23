package com.lanzong.spring.demo.action;

import com.lanzong.spring.demo.service.IQueryService;
import com.lanzong.spring.framework.annotation.LZAutowired;
import com.lanzong.spring.framework.annotation.LZController;
import com.lanzong.spring.framework.annotation.LZRequestMapping;
import com.lanzong.spring.framework.annotation.LZRequestParam;
import com.lanzong.spring.framework.webmvc.LZModelAndView;

import java.util.HashMap;
import java.util.Map;

@LZController
@LZRequestMapping("/")
public class PageAction {

    @LZAutowired
    IQueryService queryService;

    @LZRequestMapping("/first.html")
    public LZModelAndView query(@LZRequestParam("teacher")String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<>();
        model.put("teacher",teacher);
        model.put("data",result);
        model.put("token","123456");
        return new LZModelAndView("first.html",model);
    }

    @Override
    public String toString() {
        return "PageAction{" +
                "queryService=" + queryService +
                '}';
    }
}
