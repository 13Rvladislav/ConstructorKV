package com.example.constructorKV.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExternalEntity {
    String name() default "";
    String displayName() default "";
    String description() default "";
}