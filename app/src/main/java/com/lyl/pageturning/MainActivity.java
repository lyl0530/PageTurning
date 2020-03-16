package com.lyl.pageturning;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.lyl.pageturning.util.SDCardUtils;
import com.lyl.pageturning.view.PageTurningView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "lym123";
    ArrayList<String> permissionList = new ArrayList<>();
    private static String[] permissions = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private PageTurningView myView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//
        myView = findViewById(R.id.myView);

        //检测是否有写的权限
        //判断手机版本,如果低于6.0 则不用申请权限,直接拍照
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[0]);
            }
            if (checkSelfPermission(permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[1]);
            }

            if (!permissionList.isEmpty()) {
                String[] permissions1 = permissionList.toArray(new String[permissionList.size()]);
                requestPermissions(permissions1, 1);
            } else {
                myView.updateContent(SDCardUtils.getString());
            }
        } else {
            myView.updateContent(SDCardUtils.getString());
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]){
                    myView.updateContent(SDCardUtils.getString());
                    Log.d(TAG, "success: ");
                } else {
                    Log.d(TAG, "fail: ");
                }
                break;
            default:
                break;
        }
    }
}
