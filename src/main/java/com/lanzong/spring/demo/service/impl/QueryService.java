package com.lanzong.spring.demo.service.impl;

import com.lanzong.spring.demo.service.IQueryService;
import com.lanzong.spring.framework.annotation.LZService;

import java.text.SimpleDateFormat;
import java.util.Date;

@LZService
public class QueryService implements IQueryService {
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\""+name+"\",time:\""+time+"\"}";
        return json;
    }

    @Override
    public String toString() {
        return "QueryService{}";
    }
}
