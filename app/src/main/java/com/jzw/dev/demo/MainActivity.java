package com.jzw.dev.demo;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dev.jzw.helper.picture.OnDeleteItemListener;
import com.dev.jzw.helper.picture.PictureActivity;
import com.dev.jzw.helper.picture.PictureView;
import com.dev.jzw.helper.util.FileUtil;
import com.dev.jzw.helper.util.PermissionUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] per = PermissionUtil.checkReadWritePermission(this);
        if (per != null && per.length > 0) {
            ActivityCompat.requestPermissions(this, per, PermissionUtil.READ_WRITE_REQUESTCODE);
        }

        final ArrayList<String> list = new ArrayList<>();
        list.add("http://www.taopic.com/uploads/allimg/140320/235013-14032020515270.jpg");
        list.add("http://pic33.photophoto.cn/20141130/0036036820486922_b.jpg");
        list.add("http://pic33.photophoto.cn/20141207/0036036318497670_b.jpg");
        list.add("http://pic29.photophoto.cn/20131007/0036036837990605_b.jpg");
        list.add("http://pic27.photophoto.cn/20130621/0036036342621752_b.jpg");

        findViewById(R.id.btn_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureView.with(MainActivity.this)
                        .setUrls(list, 0)
                        .enableDownload(true)
                        .enableDelete(true)
                        .setOnDeleteItemListener(new OnDeleteItemListener() {
                            @Override
                            public void onDelete(int position) {

                            }
                        })
                        .create();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.READ_WRITE_REQUESTCODE) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                boolean b = FileUtil.deleteExternalDir();
            }
        }
    }
}
