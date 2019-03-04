package com.xiaomi.permissionmanager;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import com.xiaomi.permissionmanager.permission.ActivityPermission;
import com.xiaomi.permissionmanager.permission.ClickEventPermission;
import com.xiaomi.permissionmanager.permission.PermissionActivity;

import testalarm.xiaomi.com.permissionmanager.R;

public class MainActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processActivityPermission();
        processClickEventPermission();
    }

    @ActivityPermission(
            permissions = {Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION},
            permissionText = "相机、位置",
            finishActivityIfDenied = true)
    void init() {
        Toast.makeText(this, "init", Toast.LENGTH_SHORT).show();
    }

    @ClickEventPermission(
            resId = R.id.tv,
            permissions = {Manifest.permission.READ_PHONE_STATE},
            permissionText = "手机信息")
    void onClick() {
        Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show();
    }
}
