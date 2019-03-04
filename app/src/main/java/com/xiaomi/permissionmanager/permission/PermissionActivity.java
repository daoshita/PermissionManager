package com.xiaomi.permissionmanager.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PermissionActivity extends Activity {
    private SparseArray<PermissionBean> mPermissionBeans;
    private static final int DEFAULT_REQUEST_CODE = 2019;

    /**
     * 处理
     */
    protected void processActivityPermission() {
        Method[] methods = getClass().getDeclaredMethods();
        for (final Method method : methods) {
            ActivityPermission permission = method.getAnnotation(ActivityPermission.class);
            method.setAccessible(true);
            if (permission != null) {
                checkPermission(method, permission.permissions(), permission.permissionText(), DEFAULT_REQUEST_CODE, permission.finishActivityIfDenied());
                return;
            }
        }
    }

    protected void processClickEventPermission() {
        Method[] methods = getClass().getDeclaredMethods();
        for (final Method method : methods) {
            final ClickEventPermission onClick = method.getAnnotation(ClickEventPermission.class);
            if (onClick != null) {
                method.setAccessible(true);
                findViewById(onClick.resId()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkPermission(method, onClick.permissions(), onClick.permissionText(), onClick.resId(), onClick.finishActivityIfDenied());
                    }
                });
            }
        }
    }

    private void checkPermission(final Method method, String[] permissions, final String permissionText, final int resId, final boolean finishActivityIfDenied) {
        switch (PermissionUtils.checkPermissions(this, permissions, resId)) {
            case PermissionUtils.PERMISSION_AUTHORIZED:
                try {
                    method.invoke(this, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            case PermissionUtils.PERMISSION_SHOW_DIALOG:
                PermissionUtils.showPermissionSettingsDialog(this, permissionText,
                        new PermissionUtils.PermissionDialogCancelCallback() {
                            @Override
                            public void callback() {
                                if (finishActivityIfDenied) {
                                    PermissionActivity.this.finish();
                                }
                            }
                        });
                break;
            case PermissionUtils.PERMISSION_WAIT_REQUEST_RESULT:
                if (mPermissionBeans == null) {
                    mPermissionBeans = new SparseArray<>();
                }
                mPermissionBeans.append(resId, new PermissionBean(permissionText, finishActivityIfDenied, method));
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionBeans != null) {
            final PermissionBean permissionBean = mPermissionBeans.get(requestCode);
            if (permissionBean != null) {
                boolean hasDeniedPermission = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            PermissionUtils.showPermissionSettingsDialog(this, permissionBean.getPermissionText(),
                                    new PermissionUtils.PermissionDialogCancelCallback() {
                                        @Override
                                        public void callback() {
                                            if (permissionBean.getFinishActivityIfDenied()) {
                                                finish();
                                            }
                                        }
                                    });
                            return;
                        } else {
                            hasDeniedPermission = true;
                        }
                    }
                }
                if (hasDeniedPermission) {
                    if (permissionBean.getFinishActivityIfDenied()) {
                        finish();
                    }
                } else {
                    try {
                        permissionBean.getMethod().invoke(this, null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
