package com.dev.jzw.helper.v7;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dev.jzw.helper.R;
import com.dev.jzw.helper.util.DisplayUtil;


/**
 * Created by 景占午 on 2017/3/9.
 */

public class JzwRecyclerDivider extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerHeight = 10;//分割线高度，默认为10px  5dp
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    /**
     * 26      * 默认分割线：高度为2px，颜色为灰色
     * 27      *
     * 28      * @param context
     * 29      * @param orientation 列表方向
     * 30
     */
    public JzwRecyclerDivider(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        mDividerHeight = DisplayUtil.dip2px(context, 5);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.jzw_gray_bg));
        mPaint.setStyle(Paint.Style.FILL);
        a.recycle();
    }

    /**
     * 43      * 自定义分割线
     * 44      *
     * 45      * @param context
     * 46      * @param orientation 列表方向
     * 47      * @param drawableId  分割线图片
     * 48
     */
    public JzwRecyclerDivider(Context context, int height) {
        mDivider = new ColorDrawable(context.getResources().getColor(R.color.jzw_gray_bg));
        mDividerHeight = height;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.jzw_gray_bg));
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 56      * 自定义分割线
     * 57      *
     * 58      * @param context
     * 59      * @param orientation   列表方向
     * 60      * @param dividerHeight 分割线高度
     * 61      * @param dividerColor  分割线颜色
     * 62
     */
    public JzwRecyclerDivider(Context context, int dividerHeight, int dividerColor) {
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }


    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDividerHeight);
    }

    private int getOrientation(RecyclerView parent) {
        LinearLayoutManager layoutManager;
        try {
            layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        } catch (ClassCastException e) {
            throw new IllegalStateException("DividerDecoration can only be used with a " +
                    "LinearLayoutManager.", e);
        }
        return layoutManager.getOrientation();
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (getOrientation(parent) == VERTICAL_LIST) {
            drawHorizontal(c, parent);
        } else {
            drawVertical(c, parent);
        }
    }

    //绘制横向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //绘制纵向 item 分割线
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
