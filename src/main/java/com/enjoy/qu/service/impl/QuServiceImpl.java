package com.enjoy.qu.service.impl;

import com.enjoy.qu.annotation.EnjoyService;
import com.enjoy.qu.service.QuService;

@EnjoyService("QuServiceImpl")
public class QuServiceImpl implements QuService {
    public String query(String name, String age) {
        return "name " + name +"age" +age;
    }
}
