package com.jzw.dev.demo;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dev.jzw.helper.util.FileUtil;
import com.dev.jzw.helper.util.PermissionUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String[] per = PermissionUtil.checkReadWritePermission(this);
        if (per.length > 0) {
            ActivityCompat.requestPermissions(this, per, PermissionUtil.READ_WRITE_REQUESTCODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.READ_WRITE_REQUESTCODE) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                boolean b = FileUtil.deleteExternalDir();
                System.out.println("是否删除成功》" + b);
            }
        }
    }
}
