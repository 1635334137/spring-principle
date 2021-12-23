package com.lanzong.spring.demo.action;

import com.lanzong.spring.demo.service.IModifyService;
import com.lanzong.spring.demo.service.IQueryService;
import com.lanzong.spring.framework.annotation.LZAutowired;
import com.lanzong.spring.framework.annotation.LZController;
import com.lanzong.spring.framework.annotation.LZRequestMapping;
import com.lanzong.spring.framework.annotation.LZRequestParam;
import com.lanzong.spring.framework.webmvc.LZModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@LZController
@LZRequestMapping("/web")
public class MyAction {

    @LZAutowired
    IQueryService queryService;

    @LZAutowired
    IModifyService modifyService;

    @LZRequestMapping("/query.json")
    public LZModelAndView query(HttpServletRequest req, HttpServletResponse resp, @LZRequestParam("name")String name){
        String result = queryService.query(name);
        return out(resp,result);
    }

    @LZRequestMapping("/add*.json")
    public LZModelAndView add(HttpServletRequest req, HttpServletResponse resp, @LZRequestParam("name")String name,@LZRequestParam("addr")String addr){
        String result = modifyService.add(name,addr);
        return out(resp,result);
    }

    @LZRequestMapping("/remove.json")
    public LZModelAndView remove(HttpServletRequest req, HttpServletResponse resp, @LZRequestParam("id")Integer id){
        String result = modifyService.remove(id);
        return out(resp,result);
    }

    @LZRequestMapping("/edit.json")
    public LZModelAndView edit(HttpServletRequest req, HttpServletResponse resp, @LZRequestParam("id")Integer id,@LZRequestParam("name")String name){
        String result = modifyService.edit(id,name);
        return out(resp,result);
    }

    private LZModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "MyAction{" +
                "queryService=" + queryService +
                ", modifyService=" + modifyService +
                '}';
    }
}
