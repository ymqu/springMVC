package com.enjoy.qu.controller;

import com.enjoy.qu.annotation.EnjoyAutowired;
import com.enjoy.qu.annotation.EnjoyController;
import com.enjoy.qu.annotation.EnjoyRequestMapping;
import com.enjoy.qu.annotation.EnjoyRequestParam;
import com.enjoy.qu.service.QuService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@EnjoyController
@EnjoyRequestMapping("/Qu")
public class QuController {

    @EnjoyAutowired("QuServiceImpl")
    private QuService quService;

    @EnjoyRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
            @EnjoyRequestParam("name") String name,@EnjoyRequestParam("age") String age
    ){

        try {
            PrintWriter pw = response.getWriter();
            String result = quService.query(name,age);
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
