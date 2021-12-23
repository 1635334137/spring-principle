package com.lanzong.spring.demo.service.impl;

import com.lanzong.spring.demo.service.IModifyService;
import com.lanzong.spring.framework.annotation.LZService;

@LZService
public class ModifyService implements IModifyService {

    @Override
    public String add(String name, String addr) {
        return "modifyService add,name="+name+",addr="+addr;
    }

    @Override
    public String edit(Integer id, String name) {
        return "modifyService edit,id="+id+",name="+name;
    }

    @Override
    public String remove(Integer id) {
        return "modifyService id="+id;
    }

    @Override
    public String toString() {
        return "ModifyService{}";
    }
}
