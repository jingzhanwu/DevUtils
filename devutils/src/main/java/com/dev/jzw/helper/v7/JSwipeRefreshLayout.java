package com.dev.jzw.helper.v7;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.dev.jzw.helper.R;


/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/1/30 0030
 * @change
 * @describe 自定义下拉刷新控件
 **/
public class JSwipeRefreshLayout extends SwipeRefreshLayout {

    private OnPullRefreshListener mListener;

    public JSwipeRefreshLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public JSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        setColorSchemeResources(R.color.jzw_green_deep);
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (isRefreshing()) {
            setRefreshing(false);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                JSwipeRefreshLayout.this.setRefreshing(true);
                if (mListener != null) {
                    mListener.onRefresh();
                }
            }
        }, 100);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnPullRefreshListener(OnPullRefreshListener listener) {
        this.mListener = listener;
        if (mListener != null) {
            setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mListener.onRefresh();
                }
            });
        }
    }

    public interface OnPullRefreshListener {
        void onRefresh();
    }
}
