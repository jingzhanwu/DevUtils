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
 * 自定义 itemview  多用在个人中心
 * Created by 景占午 on 2017/10/11 0011.
 */

public class ArrowItemView extends RelativeLayout {

    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvTitle;

    private int backColor = getResources().getColor(R.color.jzw_white);
    private int titleColor = getResources().getColor(R.color.jzw_black);
    private int rightResource = 0;
    private int leftResource = 0;
    private float titleSize = 15;
    private String title = "";
    private boolean isShowRightView = true;
    private boolean isShowLeftView = true;

    public ArrowItemView(Context context) {
        super(context);
        init(context, null);
    }

    public ArrowItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
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
        LayoutInflater.from(context).inflate(R.layout.arrow_item, this);
        ivLeft = findViewById(R.id.iv_arrow_item_left);
        ivRight = findViewById(R.id.iv_arrow_item_right);
        tvTitle = findViewById(R.id.tv_arrow_item_text);
        if (attrs != null) {
            //获取自定义的属性
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.JZW_ArrowItemView);

            title = attr.getString(R.styleable.JZW_ArrowItemView_jzw_arrow_title);
            titleSize = attr.getDimension(R.styleable.JZW_ArrowItemView_jzw_arrow_titleSize, 15);
            backColor = attr.getColor(R.styleable.JZW_ArrowItemView_jzw_arrow_backColor, getResources().getColor(R.color.jzw_white));
            titleColor = attr.getColor(R.styleable.JZW_ArrowItemView_jzw_arrow_titleColor, getResources().getColor(R.color.jzw_black));

            leftResource = attr.getResourceId(R.styleable.JZW_ArrowItemView_jzw_arrow_leftResources, 0);
            rightResource = attr.getResourceId(R.styleable.JZW_ArrowItemView_jzw_arrow_rightResources, R.drawable.icon_right_arrow);
            isShowLeftView = attr.getBoolean(R.styleable.JZW_ArrowItemView_jzw_arrow_showLeftView, true);
            isShowRightView = attr.getBoolean(R.styleable.JZW_ArrowItemView_jzw_arrow_showRightView, true);

            //如果没有设置左边图标则设置不显示
            if (leftResource <= 0) {
                isShowLeftView = false;
            }
            //设置item的背景色
            setBackgroundColor(backColor);
            //左右 图片是否显示
            showLeftView(isShowLeftView);
            showRightView(isShowRightView);

            //设置左右图标的资源
            setLeftViewResource(leftResource);
            setRightViewResource(rightResource);
            //设置item显示文字内容，大小,颜色
            setTitleText(title);
            setTitleColor(titleColor);
            setTitleSize(titleSize);
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
        ivLeft.setImageResource(res);
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
        if (size > 0) {
            tvTitle.setTextSize(size);
        }
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setOnItemClickListener(OnClickListener listener) {
        if (listener != null) {
            this.setOnClickListener(listener);
        }
    }

    public void showLeftView(boolean show) {
        if (ivLeft != null) {
            ivLeft.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showRightView(boolean show) {
        if (ivRight != null) {
            ivRight.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public View getArrowView() {
        return this;
    }
}
