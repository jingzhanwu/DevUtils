package com.dev.jzw.helper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.jzw.helper.R;


/**
 * 自定义APP 的titlebar
 * Created by 景占午 on 2017/10/11 0011.
 */

public class CustomTitleBar extends RelativeLayout {

    private ImageView ivBack;
    private ImageView ivRight;
    private TextView tvTitle;

    private int backResource = 0;
    private int rightResource = 0;
    private int titleColor = getResources().getColor(R.color.jzw_black);
    private int backColor = getResources().getColor(R.color.jzw_white);
    private float titleSize = 16;
    private String title = "";
    private boolean isShowLeftView = true;
    private boolean isShowRightView = false;

    public CustomTitleBar(Context context) {
        super(context);
        init(context, null);
    }

    public CustomTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    public void init(Context context, AttributeSet attrs) {
        //获取到titlebar的布局文件
        LayoutInflater.from(context).inflate(R.layout.custom_title_bar, this);
        ivBack = findViewById(R.id.iv_back);
        ivRight = findViewById(R.id.iv_title_right);
        tvTitle = findViewById(R.id.tv_title);
        if (attrs != null) {
            //获取自定义的属性
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.JZW_TitleBar);
            title = attr.getString(R.styleable.JZW_TitleBar_jzw_bar_title);
            titleColor = attr.getColor(R.styleable.JZW_TitleBar_jzw_bar_titleColor, getResources().getColor(R.color.jzw_black));
            titleSize = attr.getDimension(R.styleable.JZW_TitleBar_jzw_bar_titleSize, 18);
            backResource = attr.getResourceId(R.styleable.JZW_TitleBar_jzw_bar_backResources, R.drawable.icon_back);
            backColor = attr.getColor(R.styleable.JZW_TitleBar_jzw_bar_backColor, getResources().getColor(R.color.jzw_white));

            rightResource = attr.getResourceId(R.styleable.JZW_TitleBar_jzw_bar_rightResources, 0);
            isShowRightView = attr.getBoolean(R.styleable.JZW_TitleBar_jzw_bar_showRightView, false);
            isShowLeftView = attr.getBoolean(R.styleable.JZW_TitleBar_jzw_bar_showLeftView, true);

            if (rightResource <= 0) {
                isShowRightView = false;
            }

            setBackgroundColor(backColor);
            showLeftView(isShowLeftView);
            showRightView(isShowRightView);
            setLeftViewResource(backResource);
            setRightViewResource(rightResource);

            setTitleText(title);
            setTitleSize(titleSize);
            setTitleColor(titleColor);
            //释放资源
            attr.recycle();
        }
    }


    /**
     * 测量view的高度，默认设置的高度是50dp，如果在使用时动态设置了高度，
     * 则需要手动测量一下，把父布局的高度同时设置到直接子布局（也就是真正的布局）
     * 上，不然会出现item设置了高度，而控件一直不居中，原因就是子布局的大小
     * 实际上是默认的大小没有改变
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取布局mode和测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //测量所有chidView
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //获取直接子view，也就是布局view
        View child = getChildAt(0);
        if (heightMode == MeasureSpec.AT_MOST) {
            //如果高是wrapcontent，则使用布局默认的测量的高度（50dp
            setMeasuredDimension(widthSize, child.getMeasuredHeight());
        } else if (heightMode == MeasureSpec.EXACTLY) {
            //如果高度设置的是具体的值，则将高度同时设置到子view上
            ViewGroup.LayoutParams plp = getLayoutParams();
            ViewGroup.LayoutParams clp = child.getLayoutParams();
            clp.height = plp.height;
            child.setLayoutParams(clp);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setLeftViewResource(int res) {
        ivBack.setImageResource(res);
    }

    public void setRightViewResource(int res) {
        ivRight.setImageResource(res);
    }

    public void setTitleText(int res) {
        tvTitle.setText(getResources().getString(res));
    }

    public void setTitleText(String str) {
        tvTitle.setText(str);
    }

    public void setTitleSize(float size) {
        tvTitle.setTextSize(size);
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setLeftButtonOnClick(OnClickListener listener) {
        if (ivBack != null && listener != null) {
            ivBack.setOnClickListener(listener);
        }
    }

    public void setRightButtonOnClick(OnClickListener listener) {
        if (ivRight != null && listener != null) {
            ivRight.setOnClickListener(listener);
        }
    }

    public void showRightView(boolean show) {
        if (ivRight != null) {
            ivRight.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showLeftView(boolean show) {
        if (ivBack != null) {
            ivBack.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public ImageView getRightView() {
        return ivRight;
    }

    public ImageView getLeftView() {
        return ivBack;
    }

    public TextView getTitleView() {
        return tvTitle;
    }

}
