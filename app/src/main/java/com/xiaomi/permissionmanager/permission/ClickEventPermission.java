package com.xiaomi.permissionmanager.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zenghao on 19-3-3.
 * 发生点击事件时的相关权限操作
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ClickEventPermission {
    int resId(); //点击控件id
    String[] permissions(); //需要的权限
    String permissionText() default ""; //权限授予失败跳转权限设置页对话框提示文案的权限名称
    boolean finishActivityIfDenied() default false; //用户拒绝授予权限后是否退出activity
}
