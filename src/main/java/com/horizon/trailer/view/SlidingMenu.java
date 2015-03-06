package com.horizon.trailer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.horizon.trailer.R;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends HorizontalScrollView {

    private LinearLayout mWrapper;
    private ViewGroup mMennu;
    private ViewGroup mContent;

    private int mMenuWidth;
    private int mScreenWidth;
    // dp
    private int mMenuLeftPadding;

    private boolean once;
    private boolean isOpen;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    /**
     * 未使用自定义属性时 调用
     *
     * @param context
     * @param attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 自定义属性且使用了 调用
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    @SuppressLint("ServiceCast")
    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 获取自定义的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.slidemenu, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.slidemenu_rightPadding:
                    mMenuLeftPadding = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 50, context
                                            .getResources().getDisplayMetrics()));
            }
        }
        a.recycle();

        WindowManager wm = (WindowManager) context
                .getSystemService(context.WINDOW_SERVICE);

        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    /**
     * 决定内部view 子view 的宽和高 以及 自己的宽和高 多次调用 为了 节省计算量 可以设置布尔值 计算一次即可
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            mWrapper = (LinearLayout) getChildAt(0);
            mMennu = (ViewGroup) mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);
            mMenuWidth = mMennu.getLayoutParams().width = mScreenWidth
                    - mMenuLeftPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            // mWrapper 不需要设置宽高 由于本身是linearlayout 子View可以决定父View的宽高
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 决定子view放置的位置 设置偏移量 隐藏menu
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            scrollTo(mMenuWidth, 0);
        }
    }

    /**
     * 控制移动效果
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2) {
                    smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else {
                    smoothScrollTo(0, 0);
                    isOpen = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 滚动发生时调用
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // l 为偏移量 X
        // 调用属性动画 设置TranslationX
        ViewHelper.setTranslationX(mMennu, l*0.7f);

        // 缩放
//		float leftScaleF = 0.7f + 0.3f * l * 1.0f / mMenuWidth;
//		float rightScaleF = 1.0f - 0.3f * l * 1.0f / mMenuWidth;
//
//		System.out.println(leftScaleF);
//		ViewHelper.setScaleX(mMennu, rightScaleF);
//		ViewHelper.setScaleY(mMennu, rightScaleF);
//		ViewHelper.setScaleX(mContent, leftScaleF);
//		ViewHelper.setScaleY(mContent, leftScaleF);
        // 设置缩放中心点
//		ViewHelper.setPivotX(mContent, 0);
//		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);

    }

    public void toggle() {
        if (isOpen) {
            close();
        } else {
            open();
        }

    }

    private void open() {
        if (isOpen)
            return;
        smoothScrollTo(0, 0);
        isOpen = true;
    }

    private void close() {
        if (!isOpen)
            return;
        smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

}