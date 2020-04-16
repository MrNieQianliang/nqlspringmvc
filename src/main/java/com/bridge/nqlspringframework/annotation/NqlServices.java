package com.bridge.nqlspringframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: NqlServices
 * @Author: alan
 * @Description:
 * @Date: 2020/4/15 17:46
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NqlServices {
    String value() default "";
}
