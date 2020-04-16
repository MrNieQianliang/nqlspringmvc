package com.bridge.nqlspringframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: NqlAutowired
 * @Author: alan
 * @Description:
 * @Date: 2020/4/15 17:42
 * @Version: 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NqlAutowired {
    String value() default "";
}
