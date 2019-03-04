package com.xiaomi.permissionmanager.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zenghao on 19-2-22.
 * 进入activity时相关权限操作
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActivityPermission {
    String[] permissions();
    String permissionText() default "";
    boolean finishActivityIfDenied() default false;
}
