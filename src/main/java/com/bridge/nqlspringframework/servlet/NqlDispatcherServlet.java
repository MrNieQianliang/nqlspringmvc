package com.bridge.nqlspringframework.servlet;

import com.bridge.nqlspringframework.annotation.NqlAutowired;
import com.bridge.nqlspringframework.annotation.NqlController;
import com.bridge.nqlspringframework.annotation.NqlRequestMapping;
import com.bridge.nqlspringframework.annotation.NqlServices;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @ClassName: NQLDispatcherServlet
 * @Author: alan
 * @Description:
 * @Date: 2020/4/15 16:57
 * @Version: 1.0
 */
public class NqlDispatcherServlet extends HttpServlet {

    /**
     * 属性配置文件
     */
    private Properties contextConfig = new Properties();

    private List<String> classNameList = new ArrayList<>();

    /**
     * 制作一个IOC容器
     */
    Map<String,Object> ioc = new HashMap<>();

    /**
     * 创建一个handmapping
     */
    Map<String, Method> handMapping = new HashMap<>();


    public NqlDispatcherServlet(){
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        /**
         * 加载配置文件
         */
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        doScanner(contextConfig.getProperty("scan-package"));

        doIntances();

        doAutowired();

        initHandMapping();

        System.out.println("NqlSpring is Start");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //todo
        try {
            Dispatcher(req, resp);
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    private void Dispatcher(HttpServletRequest req,HttpServletResponse resp) throws InvocationTargetException, IllegalAccessException {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        if (!this.handMapping.containsKey(url)){
            try {
                resp.getWriter().write("404 not found");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Method method = this.handMapping.get(url);

        System.out.println(Thread.currentThread().getName() + " 当前线程正在使用方法" + method);

        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());

        method.invoke(ioc.get(beanName),req,resp);

        System.out.println("当前method执行的类是" + ioc.get(beanName));
    }

    /**
     * 初始化handmapping
     */
    private void  initHandMapping(){

        if (ioc.isEmpty()){
            return;
        }

        for (Map.Entry<String,Object> entry : ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();

            if(!clazz.isAnnotationPresent(NqlController.class)){
                continue;
            }

            String BaseUrl = "";

            if(clazz.isAnnotationPresent(NqlRequestMapping.class)){
                NqlRequestMapping nqlRequestMapping = clazz.getAnnotation(NqlRequestMapping.class);
                BaseUrl = nqlRequestMapping.value();
            }

            for (Method method : clazz.getMethods()){
                if (!method.isAnnotationPresent(NqlRequestMapping.class)){
                    continue;
                }

                NqlRequestMapping nqlRequestMapping = method.getAnnotation(NqlRequestMapping.class);
                String url = ("/"+BaseUrl+"/" + nqlRequestMapping.value()).replaceAll("/+","/");
                handMapping.put(url,method);
                System.out.println("handmapping put "+ url + " method方法" + method);
            }
        }
    }

    /**
     * 注入NqlAutoWired
     */
    private void doAutowired(){
        if (ioc.isEmpty()){
            return;
        }

        for (Map.Entry<String,Object> entry : ioc.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(NqlAutowired.class)){
                    continue;
                }
                System.out.println("存在NqlAutowired注解");
                NqlAutowired nqlAutowired = field.getAnnotation(NqlAutowired.class);
                String beanName = nqlAutowired.value().trim();

                if ("".equals(beanName)){
                    System.out.println("NqlAutoWired 默认值为空");
                    beanName = field.getType().getName();
                }

                field.setAccessible(true);

                try {
                    field.set(entry.getValue(),ioc.get(beanName));
                    System.out.println("数据注入成功");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }


    private void doIntances(){
        if (classNameList.isEmpty()){
            return;
        }

        try {
            for (String className : classNameList) {
                Class<?> clazz = Class.forName(className);

               if (clazz.isAnnotationPresent(NqlController.class)){
                   String beanName = toLowerFirstCase(clazz.getSimpleName());
                   Object instance = clazz.newInstance();

                   ioc.put(beanName,instance);
                   System.out.println("已经存入对象实例" + beanName);
               }else if (clazz.isAnnotationPresent(NqlServices.class)){
                   String beanName = toLowerFirstCase(clazz.getSimpleName());
                   NqlServices nqlServices = clazz.getAnnotation(NqlServices.class);
                   if ("".equals(nqlServices.value())){
                       beanName = nqlServices.value();
                   }

                   Object intance = clazz.newInstance();
                   ioc.put(beanName,intance);

                   for (Class<?> i : clazz.getInterfaces()){
                       if (ioc.containsKey(i.getName())){
                            throw new Exception("The Bena Name is Exist");
                       }
                       ioc.put(i.getName(),intance);
                       System.out.println("注入一个接口的实现");
                   }
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doScanner(String scanPackage){
        URL resources = this.getClass().getClassLoader()
                .getResource("/"+scanPackage.replaceAll("\\.","/"));
        if (resources == null){
            return;
        }

        File classPath = new File(resources.getFile());

        for (File file : classPath.listFiles()){
            if (file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if (!file.getName().endsWith(".class")){
                    System.out.println("This is File is not a class file:" + file.getName());
                    continue;
                }
                String className = (scanPackage + "."+file.getName()).replaceAll(".class","");
                classNameList.add(className);
                System.out.println("已经保存到NameList" + className);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);

        try {
            contextConfig.load(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null != inputStream){
                try {
                    inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取类名首字母小写的类的名称
     * @param className
     * @return
     */
    private String toLowerFirstCase(String className) {
        char[] charArray = className.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }
}