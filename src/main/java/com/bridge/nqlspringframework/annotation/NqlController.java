package com.bridge.nqlspringframework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: NqlController
 * @Author: alan
 * @Description:
 * @Date: 2020/4/15 17:45
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NqlController {
    String value() default "";
}
