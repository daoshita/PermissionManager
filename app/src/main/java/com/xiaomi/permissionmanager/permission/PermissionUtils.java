package com.xiaomi.permissionmanager.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;

import java.util.LinkedList;

import testalarm.xiaomi.com.permissionmanager.R;

/**
 * Created by zenghao on 17-3-3.
 *
 */

public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getName();

    public static final int PERMISSION_AUTHORIZED = 0;
    public static final int PERMISSION_WAIT_REQUEST_RESULT = 1;
    public static final int PERMISSION_SHOW_DIALOG = 2;

    public static int checkPermissions(Activity activity, String[] permissions, int requestCode) {
        LinkedList<String> unauthorizedPermissions = new LinkedList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                unauthorizedPermissions.add(permissions[i]);
            }
        }
        if (unauthorizedPermissions.size() <= 0) {
            return PERMISSION_AUTHORIZED;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return PERMISSION_SHOW_DIALOG;
        }
        final String[] arr = new String[unauthorizedPermissions.size()];
        unauthorizedPermissions.toArray(arr);
        activity.requestPermissions(arr, requestCode);
        return PERMISSION_WAIT_REQUEST_RESULT;
    }

    /**
     * 弹出权限设置对话框
     */
    public static void showPermissionSettingsDialog(final Activity activity, String permission, final PermissionDialogCancelCallback callbak) {
        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.get_permission_fail);
        dialog.setMessage(Html.fromHtml(activity.getString(R.string.get_permission_fail_content) +
                "<font><b>" + permission + "</font></b>" + activity.getString(R.string.permission)));
        dialog.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoAppSettingsPage(activity);
                    }
                });
        dialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callbak.callback();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 打开app系统权限管理页面
     */
    private static void gotoAppSettingsPage(Context context) {
        try {
            // vivo 点击设置图标>加速白名单>我的app
            //      点击软件管理>软件管理权限>软件>我的app>信任该软件
            Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
            if (appIntent != null) {
                context.startActivity(appIntent);
                return;
            }

            // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
            //      点击权限隐私>自启动管理>我的app
            appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
            if (appIntent != null) {
                context.startActivity(appIntent);
                return;
            }

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent);
        } catch(Exception e) {
            Log.e(TAG, "start settings page error: ", e);
        }
    }

    public interface PermissionDialogCancelCallback {
        void callback();
    }
}
