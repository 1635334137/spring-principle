package com.lanzong.spring.demo.service;

public interface IModifyService {

    public String add(String name,String addr);

    public String edit(Integer id,String name);

    public String remove(Integer id);
}
