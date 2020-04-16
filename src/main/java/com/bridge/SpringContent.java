package com.bridge;

import com.bridge.nqlspringframework.servlet.NqlDispatcherServlet;

import javax.servlet.ServletException;
import java.util.Properties;

/**
 * @ClassName: SpringContent
 * @Author: alan
 * @Description:
 * @Date: 2020/4/15 21:40
 * @Version: 1.0
 */
public class SpringContent {

    public static void main(String[] args) throws ServletException {
        ClassLoader classLoader = SpringContent.class.getClassLoader();

        Properties properties = new Properties();


        System.out.println("123");
    }
}
