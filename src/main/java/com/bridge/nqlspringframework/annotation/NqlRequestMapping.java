package com.bridge.nqlspringframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: NqlRequestMapping
 * @Author: alan
 * @Description:
 * @Date: 2020/4/15 17:48
 * @Version: 1.0
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NqlRequestMapping {
    String value() default "";
}
