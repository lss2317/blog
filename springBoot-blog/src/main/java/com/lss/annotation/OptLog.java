package com.lss.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {


    /**
     * @return 系统模块
     */
    String ApiModule() default "";

    /**
     * @return 操作描述
     */
    String ApiDesc() default "";

    /**
     * @return 操作类型
     */
    String optType() default "";
}