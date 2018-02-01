package com.dev.jzw.helper.v7;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dev.jzw.helper.util.DisplayUtil;

/**
 * @anthor created by jignzhanwu
 * @date 2017/12/5 0005
 * @change
 * @describe RecyclerView 的分割线，支持线性布局和网格布局
 * 网格布局中支持分割线填充满和不填充满两种。支持改变分割线高度和颜色,
 * 设置分割线为图片
 **/
public class JRecyclerDivider extends RecyclerView.ItemDecoration {
    /**
     * divider的高度  单位是dp
     */
    private int mSpace = 5;  //值是dp
    /**
     * diveder的颜色  默认是淡灰色
     */
    private int mColor = Color.argb(0, 0xf2, 0xf2, 0xf2);
    /**
     * divider 的图片资源
     */
    private Drawable mDivider;
    private Paint mPaint;
    /**
     * 当时GridView 时标记是否要填充满，默认不填充满
     */
    private int type;
    private Context mContext;

    public int getColor() {
        return mColor;
    }

    public void setColor(@ColorRes int color) {
        this.mColor = color;
    }

    /**
     * 默认构造函数，默认是5dp的高度
     *
     * @param context
     */
    public JRecyclerDivider(Context context) {
        this.mContext = context;
        init(5, mColor, null, -1);
    }


    /**
     * 只传divider高度
     *
     * @param context
     * @param space   高度 单位dp
     */
    public JRecyclerDivider(Context context, int space) {
        this.mContext = context;
        init(space, 0, null, -1);
    }

    /**
     * @param context
     * @param space   divider 高度 单位dp
     * @param color   divider 颜色
     */
    public JRecyclerDivider(Context context, int space, int color) {
        this.mContext = context;
        init(space, color, null, -1);
    }

    /**
     * @param context
     * @param space   divider 高度 单位dp
     * @param color   divider 颜色
     * @param type    Gridview 时  分割线是否要填充满，默认不填充满
     *                值是0，要设置填充满时 type传大于0的值即可
     */
    public JRecyclerDivider(Context context, int space, int color, int type) {
        this.mContext = context;
        init(space, color, null, type);
    }

    /**
     * @param context
     * @param space    divider 的高度 单位dp
     * @param drawable divider 的图片资源
     */
    public JRecyclerDivider(Context context, int space, Drawable drawable) {
        this.mContext = context;
        init(space, 0, drawable, -1);
    }

    /**
     * 初始化 构造画笔
     * 默认 space 的值是dp
     *
     * @param space
     * @param color
     * @param divider
     * @param type
     */
    private void init(int space, int color, Drawable divider, int type) {
        mSpace = space > 0 ? space : 5;
        mColor = color > 0 ? color : Color.argb(0, 0xf2, 0xf2, 0xf2);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(DisplayUtil.dip2px(mContext, mSpace));
        if (type >= 0) {
            this.type = type;
        }
        mDivider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() != null) {
            if (parent.getLayoutManager() instanceof LinearLayoutManager && !(parent.getLayoutManager() instanceof GridLayoutManager)) {
                if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    outRect.set(mSpace, 0, mSpace, 0);
                } else {
                    outRect.set(0, mSpace, 0, mSpace);
                }
            } else {
                outRect.set(mSpace, mSpace, mSpace, mSpace);
            }
        }

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getLayoutManager() != null) {
            if (parent.getLayoutManager() instanceof LinearLayoutManager && !(parent.getLayoutManager() instanceof GridLayoutManager)) {
                if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    drawHorizontal(c, parent);
                } else {
                    drawVertical(c, parent);
                }
            } else {
                if (type == 0) {
                    drawGrideview(c, parent);
                } else {
                    drawGrideview1(c, parent);
                }
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
            final int right = left + mSpace;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //绘制横向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mSpace;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }

        }
    }

    //绘制grideview item 分割线 不是填充满的
    private void drawGrideview(Canvas canvas, RecyclerView parent) {
        GridLayoutManager linearLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        int childSize = parent.getChildCount();
        int other = parent.getChildCount() / linearLayoutManager.getSpanCount() - 1;
        if (other < 1) {
            other = 1;
        }
        other = other * linearLayoutManager.getSpanCount();
        if (parent.getChildCount() < linearLayoutManager.getSpanCount()) {
            other = parent.getChildCount();
        }
        int top, bottom, left, right, spancount;
        spancount = linearLayoutManager.getSpanCount() - 1;
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (i < other) {
                top = child.getBottom() + layoutParams.bottomMargin;
                bottom = top + mSpace;
                left = (layoutParams.leftMargin + mSpace) * (i + 1);
                right = child.getMeasuredWidth() * (i + 1) + left + mSpace * i;
                if (mDivider != null) {
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
                if (mPaint != null) {
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
            if (i != spancount) {
                top = (layoutParams.topMargin + mSpace) * (i / linearLayoutManager.getSpanCount() + 1);
                bottom = (child.getMeasuredHeight() + mSpace) * (i / linearLayoutManager.getSpanCount() + 1) + mSpace;
                left = child.getRight() + layoutParams.rightMargin;
                right = left + mSpace;
                if (mDivider != null) {
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
                if (mPaint != null) {
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            } else {
                spancount += 4;
            }
        }
    }

    /***/
    private void drawGrideview1(Canvas canvas, RecyclerView parent) {
        GridLayoutManager linearLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        int childSize = parent.getChildCount();
        int top, bottom, left, right, spancount;
        spancount = linearLayoutManager.getSpanCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            //画横线
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getBottom() + layoutParams.bottomMargin;
            bottom = top + mSpace;
            left = layoutParams.leftMargin + child.getPaddingLeft() + mSpace;
            right = child.getMeasuredWidth() * (i + 1) + left + mSpace * i;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
            //画竖线
            top = (layoutParams.topMargin + mSpace) * (i / linearLayoutManager.getSpanCount() + 1);
            bottom = (child.getMeasuredHeight() + mSpace) * (i / linearLayoutManager.getSpanCount() + 1) + mSpace;
            left = child.getRight() + layoutParams.rightMargin;
            right = left + mSpace;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }

            //画上缺失的线框
            if (i < spancount) {
                top = child.getTop() + layoutParams.topMargin;
                bottom = top + mSpace;
                left = (layoutParams.leftMargin + mSpace) * (i + 1);
                right = child.getMeasuredWidth() * (i + 1) + left + mSpace * i;
                if (mDivider != null) {
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
                if (mPaint != null) {
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
            if (i % spancount == 0) {
                top = (layoutParams.topMargin + mSpace) * (i / linearLayoutManager.getSpanCount() + 1);
                bottom = (child.getMeasuredHeight() + mSpace) * (i / linearLayoutManager.getSpanCount() + 1) + mSpace;
                left = child.getLeft() + layoutParams.leftMargin;
                right = left + mSpace;
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
}
