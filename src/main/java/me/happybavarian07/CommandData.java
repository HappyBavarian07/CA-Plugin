package me.happybavarian07;/*
 * @Author HappyBavarian07
 * @Date 12.11.2021 | 17:55
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandData {
    boolean playerRequired() default true;
}
