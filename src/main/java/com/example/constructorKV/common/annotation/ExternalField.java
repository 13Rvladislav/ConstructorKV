package com.example.constructorKV.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExternalField {
    String displayName() default "";
    String category() default "Основная информация";
    int displayOrder() default 0;
    boolean isKey() default false;
    boolean isNavigation() default false;
    String relatedEntity() default "";
    boolean isRequired() default true;     // ← true = отображается, false = скрыто
    String type() default "";
}