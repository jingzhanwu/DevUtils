package com.jzw.dev.demo;

import android.content.Intent;
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
        list.add("http://10.168.31.224:8888/group1/M00/00/13/Cqgf4Fv_UuuARpsyAJDjCInS09Y621.jpg");
        list.add("http://s4.sinaimg.cn/mw690/001ve3i3zy6SziUdgH143&690");
        list.add("http://pic2.16pic.com/00/17/06/16pic_1706842_b.jpg");
        list.add("http://pic28.photophoto.cn/20130704/0036036875112446_b.jpg");
        list.add("http://pic1.16pic.com/00/04/83/16pic_483177_b.jpg");
        list.add("http://photo.16pic.com/00/10/09/16pic_1009413_b.jpg");
        list.add("http://b-ssl.duitang.com/uploads/item/201508/06/20150806223218_xAJQS.jpeg");

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

        findViewById(R.id.btn_selector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, TestProvider.class);
                startActivity(intent);
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
