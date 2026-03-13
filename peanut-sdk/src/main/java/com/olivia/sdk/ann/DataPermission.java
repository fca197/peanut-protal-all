package com.olivia.sdk.ann;


import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于标识需要进行数据权限控制的方法
 * 通常用于服务层或控制器层的方法上，以启用数据权限过滤功能
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

}
