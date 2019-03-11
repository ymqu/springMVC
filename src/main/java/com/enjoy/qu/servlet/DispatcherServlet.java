package com.enjoy.qu.servlet;

import com.enjoy.qu.annotation.EnjoyAutowired;
import com.enjoy.qu.annotation.EnjoyController;
import com.enjoy.qu.annotation.EnjoyRequestMapping;
import com.enjoy.qu.annotation.EnjoyService;
import com.enjoy.qu.controller.QuController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    List<String> classNames = new ArrayList<String>();
    Map<String, Object> beans = new HashMap<String, Object>();
    Map<String, Object> handMap = new HashMap<String, Object>();

    @Override
    public void init(ServletConfig config) throws ServletException {

        //Scan
        ScanPackage("com.enjoy");
        doInstance();
        doAutowired();
        urlMapping();
    }

    private void urlMapping() {
        for(Map.Entry<String,Object> entry: beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(EnjoyController.class)) {
                EnjoyRequestMapping map1 = clazz.getAnnotation(EnjoyRequestMapping.class);
                String classPath = map1.value();

                Method[] methods = clazz.getMethods();
                for(Method  method: methods){
                    if(method.isAnnotationPresent(EnjoyRequestMapping.class)){

                        EnjoyRequestMapping map2 = method.getAnnotation(EnjoyRequestMapping.class);
                        String methodPath = map2.value();
                        handMap.put(classPath + methodPath, method);

                    }
                }
            }
        }
    }

    private void doAutowired() {
        for(Map.Entry<String,Object> entry: beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if( clazz.isAnnotationPresent(EnjoyController.class)){
                Field[] fields = clazz.getDeclaredFields();
                for(Field field: fields){
                    if (field.isAnnotationPresent(EnjoyAutowired.class)) {

                        EnjoyAutowired auto = field.getAnnotation(EnjoyAutowired.class);
                        String key = auto.value();

                        field.setAccessible(true);
                        try {
                            field.set(instance,beans.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else{continue;}
                }
            }else {continue;}
        }
    }

    private void doInstance() {
        for (String className : classNames) {
            String cn = className.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(cn);
                if(clazz.isAnnotationPresent(EnjoyController.class)){
                    Object value1 = clazz.newInstance();
                    EnjoyRequestMapping map1 = clazz.getAnnotation(EnjoyRequestMapping.class);

                    String key = map1.value();
                    beans.put(key, value1);
                }else if(clazz.isAnnotationPresent(EnjoyService.class)){
                    Object value2 = clazz.newInstance();
                    beans.put(clazz.getAnnotation(EnjoyService.class).value(), value2);
                }else{ continue;}

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //basePackage = com.enjoy  to url = /com/enjoy
    private void ScanPackage(String basePackage) {

        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.","/"));

        String fileStr = url.getFile();     // com/enjoy fileStr;

        File file1 = new File(fileStr);

        String[] paths = file1.list();

        for(String path: paths){
            File filePath = new File(paths + path);
            if(filePath.isDirectory()){
                ScanPackage(basePackage +"."+path);
            }
            else{
                classNames.add(basePackage+"." +filePath.getName());  //com.enjoy.OrderController.class
            }
        }


    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String  url = req.getRequestURI();
       String context = req.getContextPath();  //cut context path;

       String path = url.replace(context, "");

       Method method = (Method) handMap.get(path);
       QuController instance = (QuController) beans.get("/" + path.split("/")[1]);

       Object args[]= hand(req, resp, method);
       method.invoke(instance, args);
    }


}
