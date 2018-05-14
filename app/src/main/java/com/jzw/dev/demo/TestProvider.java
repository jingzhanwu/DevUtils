package com.jzw.dev.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;


import com.dev.jzw.helper.selector.SelectorProvider;
import com.dev.jzw.helper.selector.ISelectorEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by Administrator
 * @date 2018/5/11 0011
 * @change
 * @describe describe
 **/
public class TestProvider extends AppCompatActivity {

    private LinearLayout content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);
        content = findViewById(R.id.content);
        setData();
    }


    public void setData() {
        List<ISelectorEntry> list = new ArrayList<>();
        DicTestInfo dic = new DicTestInfo("001", "景占午", "1", "西安");
        dic.setList(getData());

        DicTestInfo dic1 = new DicTestInfo("002", "马超", "1", "西安");
        dic1.setList(getData());

        DicTestInfo dic2 = new DicTestInfo("003", "烟雨梦", "1", "西安");
        dic2.setList(getData());

        DicTestInfo dic3 = new DicTestInfo("004", "刘成", "1", "西安");
        dic3.setList(getData());

        DicTestInfo dic4 = new DicTestInfo("005", "薛兵", "1", "西安");
        dic4.setList(getData());

        list.add(dic);
        list.add(dic1);
        list.add(dic2);
        list.add(dic3);
        list.add(dic4);


        SelectorProvider provider = new SelectorProvider(this, 3);
        provider.setShowResultView(false);
        provider.setData(list);

        provider.setOnSelectListener(new SelectorProvider.OnSelectorListener() {
            @Override
            public void onSelected(List<ISelectorEntry> datas) {
                DicTestInfo dic5 = (DicTestInfo) datas.get(0);
                System.out.println("回调结果》》" + dic5.getOrgName());
            }
        });

        provider.setOnDialogCloseListener(new SelectorProvider.OnDialogCloseListener() {
            @Override
            public void dialogclose() {
                //关闭回调
                finish();
            }
        });
        View view = provider.getSelectorView();
        content.addView(view, 0);
    }

    public List<DicTestInfo> getData() {
        List<DicTestInfo> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            DicTestInfo dic = new DicTestInfo(String.valueOf(new Random().nextInt(100)), "景占午" + new Random().nextInt(100), "1", "西安");
            List<DicTestInfo> childList = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                DicTestInfo child = new DicTestInfo(String.valueOf(new Random().nextInt(300)), "成成" + new Random().nextInt(200), "1", "西安");
                childList.add(child);
            }

            dic.setList(childList);

            list.add(dic);
        }
        return list;
    }
}
