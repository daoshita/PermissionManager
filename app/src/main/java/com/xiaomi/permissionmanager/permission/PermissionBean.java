package com.xiaomi.permissionmanager.permission;

import java.lang.reflect.Method;

/**
 * Created by zenghao on 19-3-3.
 *
 */

public class PermissionBean {
    private String permissionText;
    private boolean finishActivityIfDenied;
    private Method method;

    public PermissionBean(String permissionText, boolean finishActivityIfDenied,  Method method) {
        this.permissionText = permissionText;
        this.finishActivityIfDenied = finishActivityIfDenied;
        this.method = method;
    }

    public String getPermissionText() {
        return permissionText;
    }

    public boolean getFinishActivityIfDenied() {
        return finishActivityIfDenied;
    }

    public Method getMethod() {
        return method;
    }
}
