package com.dev.jzw.helper.selector;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.dev.jzw.helper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/5/4 0004
 * @change
 * @describe 多级列表选择器
 **/
@SuppressLint("NewApi")
public class SelectorProvider implements AdapterView.OnItemClickListener {
    /**
     * 层级深度
     */
    private int mDepth;
    /**
     * 各个tab的源数据,key==tab的索引
     */
    private ArrayMap<Integer, List<ISelectorEntry>> mSourceData;
    /**
     * 保存当前选中的item数据按照tab的先后顺序排列
     */
    private List<ISelectorEntry> mSelectedData;
    /**
     * 保存大年选中tab的iten的name值
     */
    private ArrayMap<Integer, String> mSelectedNames;

    /**
     * 保存当前选中的item在列表中的索引，key==tab索引，value==position
     */
    private ArrayMap<Integer, Integer> mItemIndexMap;

    /**
     * tab对应的view
     */
    private ArrayMap<Integer, View> mTabViewMap;

    /**
     * 当前选中的tab
     */
    private int mCurrentTabIndex = 0; //默认是第一级

    /**
     * 数据适配器集合
     */
    private ArrayMap<Integer, SelectorAdapter> mAdapters;

    /**
     * 源数据
     */
    private List<ISelectorEntry> mDatas;

    private static final int INDEX_INVALID = -1;
    private Context mContext;
    private LayoutInflater inflater;

    private View mView;
    private LinearLayout layout_tab;
    private TextView btnTakeData;
    private ProgressBar progressBar;
    private TextView tvText;

    private View indicatorView;
    private ListView listView;
    private View resultView;
    //iten选择监听器
    private OnSelectorListener listener;
    private OnDialogCloseListener dialogCloseListener;

    //UI部分
    private ImageView iv_colse;
    private int selectedColor;
    private int unSelectedColor;

    /**
     * 是否显示结果view
     */
    private boolean mShowResultView;

    /**
     * 默认构造函数，
     *
     * @param context
     * @param depth   列表的层级深度
     */
    public SelectorProvider(Context context, int depth) {
        mContext = context;
        mDepth = depth;
        inflater = LayoutInflater.from(context);
        init();
        initAdapters();
    }

    private void initViews() {
        mView = inflater.inflate(R.layout.selector_main, null);
        tvText = mView.findViewById(R.id.tv_selector_text);
        btnTakeData = mView.findViewById(R.id.btn_commit);
        progressBar = mView.findViewById(R.id.progressBar);//进度条
        iv_colse = mView.findViewById(R.id.iv_colse);
        listView = mView.findViewById(R.id.listView);//listview
        layout_tab = mView.findViewById(R.id.layout_tab);
        indicatorView = mView.findViewById(R.id.indicator); //指示器

        resultView = mView.findViewById(R.id.layout_result);
        resultView.setVisibility(mShowResultView ? View.VISIBLE : View.GONE);
    }

    private void init() {
        initViews();
        //添加tabview
        mTabViewMap = new ArrayMap<>();
        layout_tab.removeAllViews();
        for (int i = 0; i < mDepth; i++) {
            final int index = i;
            TextView tabView = new TextView(mContext, null, 0, R.style.tab);
            tabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentTabIndex = index;
                    SelectorProvider.this.tabClickChange();
                }
            });
            mTabViewMap.put(i, tabView);
            //添加到viewGroup中
            layout_tab.addView(tabView, i);
        }
        listView.setOnItemClickListener(this);
        iv_colse.setOnClickListener(new onCloseClickListener());
        btnTakeData.setOnClickListener(new onSelectClickListener());
        updateIndicator();
    }

    public void initAdapters() {
        mAdapters = new ArrayMap<>();
        for (int i = 0; i < mTabViewMap.size(); i++) {
            SelectorAdapter adapter = new SelectorAdapter(new ArrayList<ISelectorEntry>(), i);
            mAdapters.put(i, adapter);
        }
    }

    public void setData(List<ISelectorEntry> data) {
        if (mSourceData == null) {
            mSourceData = new ArrayMap<>();
        } else {
            mSourceData.clear();
        }
        mSourceData.put(mCurrentTabIndex, data);
        mDatas = data;

        //初始化保存数据的map
        mItemIndexMap = new ArrayMap<>();
        mSelectedData = new ArrayList<>();
        for (int i = 0; i < mTabViewMap.size(); i++) {
            mItemIndexMap.put(i, INDEX_INVALID);
            mSelectedData.add(i, null);
        }

        changeData(mCurrentTabIndex, mDatas);
    }

    /**
     * 创建Handler 更新列表
     */
    @SuppressWarnings("unchecked")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            int what = msg.what;
            //要显示的数据
            List<ISelectorEntry> data = (List<ISelectorEntry>) msg.obj;
            if (Lists.notEmpty(data)) {
                mSourceData.put(what, data);
                SelectorAdapter adapter = mAdapters.get(what);
                adapter.setData(data);
                listView.setAdapter(adapter);
                //当前显示数据的tab
                mCurrentTabIndex = what;
            }

            updateTabsVisibility();
            updateProgressVisibility();
            updateIndicator();

            return true;
        }
    });

    /**
     * tab项点击事件处理
     */
    public void tabClickChange() {
        //设置对应的适配器到列表
        listView.setAdapter(mAdapters.get(mCurrentTabIndex));
        //获取当前选中的tab的列表iten索引
        int position = mItemIndexMap.get(mCurrentTabIndex).intValue();
        if (position != INDEX_INVALID) {
            listView.setSelection(position);
        }
        updateTabsVisibility();
        updateIndicator();
    }

    /**
     * 更新tab显示
     */
    private void updateTabsVisibility() {
        for (int i = 0; i < mTabViewMap.size(); i++) {
            TextView tabView = (TextView) mTabViewMap.get(i);
            tabView.setVisibility(Lists.notEmpty(mSourceData.get(i)) ? View.VISIBLE : View.GONE);

            tabView.setEnabled(mCurrentTabIndex != i);
        }
        if (selectedColor != 0 && unSelectedColor != 0) {
            updateTabTextColor();
        }
    }

    /**
     * 更新字体的颜色
     */
    private void updateTabTextColor() {
        for (int i = 0; i < mTabViewMap.size(); i++) {
            TextView tabView = (TextView) mTabViewMap.get(i);
            if (mCurrentTabIndex != i) {
                tabView.setTextColor(mContext.getResources().getColor(selectedColor));
            } else {
                tabView.setTextColor(mContext.getResources().getColor(unSelectedColor));
            }
        }
    }


    /**
     * 更新tab 指示器
     */
    private void updateIndicator() {
        mView.post(new Runnable() {
            @Override
            public void run() {
                TextView tabView = (TextView) mTabViewMap.get(mCurrentTabIndex);
                buildIndicatorAnimatorTowards(tabView).start();
            }
        });
    }


    /**
     * tab 来回切换的动画
     *
     * @param tab
     * @return
     */
    private AnimatorSet buildIndicatorAnimatorTowards(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(indicatorView, "X", indicatorView.getX(), tab.getX());

        final ViewGroup.LayoutParams params = indicatorView.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                indicatorView.setLayoutParams(params);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);

        return set;
    }


    /**
     * 设置字体选中的颜色
     */
    public void setTextSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * 设置字体没有选中的颜色
     */
    public void setTextUnSelectedColor(int unSelectedColor) {
        this.unSelectedColor = unSelectedColor;
    }

    /**
     * 设置字体的大小
     */
    public void setTextSize(float dp) {
        for (Map.Entry<Integer, View> entry : mTabViewMap.entrySet()) {
            ((TextView) entry.getValue()).setTextSize(dp);
        }
    }

    /**
     * 设置字体的背景
     */
    public void setBackgroundColor(int colorId) {
        layout_tab.setBackgroundColor(mContext.getResources().getColor(colorId));
    }

    /**
     * 设置指示器的背景
     */
    public void setIndicatorBackgroundColor(int colorId) {
        indicatorView.setBackgroundColor(mContext.getResources().getColor(colorId));
    }

    /**
     * 设置指示器的背景
     */
    public void setIndicatorBackgroundColor(String color) {
        indicatorView.setBackgroundColor(Color.parseColor(color));
    }

    /**
     * 设置是否显示结果view
     *
     * @param show
     */
    public void setShowResultView(boolean show) {
        mShowResultView = show;
        if (resultView != null) {
            resultView.setVisibility(mShowResultView ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 选择结果回调
     */
    class onSelectClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onSelected(mSelectedData);
            }
            if (dialogCloseListener != null) {
                dialogCloseListener.dialogclose();
            }
        }
    }

    /**
     * 点击右边关闭dialog监听
     */
    class onCloseClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (dialogCloseListener != null) {
                dialogCloseListener.dialogclose();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //获取选中item的数据
        ISelectorEntry data = mAdapters.get(mCurrentTabIndex).getItem(position);
        //将数据与tab对应并保存到map
        mSelectedData.add(mCurrentTabIndex, data);
        //保存对应的item位置
        mItemIndexMap.put(mCurrentTabIndex, position);

        //更新tabView的文本
        for (int i = mCurrentTabIndex; i < mTabViewMap.size(); i++) {
            TextView tabView = (TextView) mTabViewMap.get(i);
            if (i == mCurrentTabIndex) {
                tabView.setText(data.getSelectorName());
            } else {
                tabView.setText("请选择");
            }
        }

        if (mSelectedNames == null) {
            mSelectedNames = new ArrayMap<>();
        }
        mSelectedNames.put(mCurrentTabIndex, data.getSelectorName());

        //更新选中的数据 显示顶部
        StringBuffer buffer = new StringBuffer();
        buffer.delete(0, buffer.length());
        for (Map.Entry<Integer, String> map : mSelectedNames.entrySet()) {
            buffer.append(map.getValue());
            buffer.append(" ");
        }

        tvText.setText(buffer.toString());
        //更新本tab列表
        mAdapters.get(mCurrentTabIndex).notifyDataSetChanged();

        //如果没有下一级则直接返回
        if (Lists.isEmpty(data.getSelectorChildreen())) {
            return;
        }

        //更新显示下一级列表
        changeData(mCurrentTabIndex + 1, data.getSelectorChildreen());

        //清空下一级数据
        int adapterCount = mAdapters.size();
        if (mCurrentTabIndex + 1 <= adapterCount) {
            for (int i = mCurrentTabIndex + 1; i < adapterCount; i++) {
                mAdapters.get(i).clearData();
                mItemIndexMap.put(i, INDEX_INVALID);
                mSelectedData.add(i, null);
            }
        }

    }

    /**
     * 更新列表数据
     *
     * @param tabIndex 要更新数据的tab位置
     * @param data
     */
    public void changeData(int tabIndex, List<ISelectorEntry> data) {
        progressBar.setVisibility(View.VISIBLE);
        handler.sendMessage(Message.obtain(handler, tabIndex, data));
    }

    /**
     * 更新进度条
     */
    private void updateProgressVisibility() {
        ListAdapter adapter = listView.getAdapter();
        int itemCount = adapter.getCount();
        progressBar.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * 获得view
     *
     * @return
     */
    public View getSelectorView() {
        return mView;
    }


    /**
     * 列表适配器
     */
    class SelectorAdapter extends BaseAdapter {

        private List<ISelectorEntry> data;
        private int tab = 0;

        public SelectorAdapter(List<ISelectorEntry> data, int tabFlag) {
            this.data = data;
            this.tab = tabFlag;
        }

        public void setData(List<ISelectorEntry> dataList) {
            clearData();
            data = dataList;
        }

        public void clearData() {
            if (data != null) {
                data.clear();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public ISelectorEntry getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selector_item_area, parent, false);

                holder = new Holder();
                holder.textView = convertView.findViewById(R.id.textView);
                holder.imageViewCheckMark = convertView.findViewById(R.id.imageViewCheckMark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            ISelectorEntry item = getItem(position);
            holder.textView.setText(item.getSelectorName());

            int index = mItemIndexMap.get(tab).intValue();
            boolean checked = index != INDEX_INVALID && mSourceData.get(tab).get(index).getSelectorId().equals(item.getSelectorId());
            holder.textView.setEnabled(!checked);
            holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class Holder {
            TextView textView;
            ImageView imageViewCheckMark;
        }
    }

    /**
     * 设置iten 选择监听器
     *
     * @param listener
     */
    public void setOnSelectListener(OnSelectorListener listener) {
        this.listener = listener;
    }

    /**
     * item 选择监听器
     */
    public interface OnSelectorListener {
        /**
         * 返回数据
         *
         * @param datas
         */
        void onSelected(List<ISelectorEntry> datas);
    }


    public interface OnDialogCloseListener {
        void dialogclose();
    }

    /**
     * 设置close监听
     */
    public void setOnDialogCloseListener(OnDialogCloseListener listener) {
        this.dialogCloseListener = listener;
    }
}
