package com.bridge.demo.mvc.action;

import com.bridge.nqlspringframework.annotation.NqlController;
import com.bridge.nqlspringframework.annotation.NqlRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: DemoAction
 * @Author: alan
 * @Description:
 * @Date: 2020/4/15 16:51
 * @Version: 1.0
 */
@NqlController
@NqlRequestMapping("/test")
public class DemoAction {

    @NqlRequestMapping("/nqltest")
    public void getNqltest(HttpServletRequest req, HttpServletResponse resp){
        try {
            resp.getWriter().write("Hello Nql Ni Hao");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
