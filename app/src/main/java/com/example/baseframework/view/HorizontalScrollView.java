package com.example.baseframework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Mr.X on 2016/12/24.
 * 水平滑动的ScrollView仿ViewPager的整页滑动方式
 */

public class HorizontalScrollView extends ViewGroup {
    //共有多少页
    private int mPageSize;
    //宽度
    private int mPagerWidth;
    //当前显示的View
    private int mChildIndex;
    //上次滑动的X,Y
    private int mLastX;
    //上次滑动的X,Y（）
    private int mLastInterceptX;
    private int mLastInterceptY;
    private Scroller mScroller;
    //收拾速度监听
    private VelocityTracker mVelocityTracker;
    //能识别的最小滑动距离
    private int mTouchSlop;
    private static final String TAG = "HorizontalScrollView_LOG";

    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollView(Context context) {
        this(context, null);
    }

    private OnPageChangeListener onPageChangeListener;

    public void setOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        onPageChangeListener = pageChangeListener;
    }


    public int getPageSize() {
        return mPageSize;
    }

    public int getCurrentItem() {
        return mChildIndex;
    }

    public void setCurrentItem(final int item) {
        post(new Runnable() {
            @Override
            public void run() {
                mChildIndex = item;
                int dx = (item) * mPagerWidth;
                scrollTo(dx, 0);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onWidthChange(mChildIndex);
                }
            }
        });
    }

    public void smoothScrollToItem(int position) {
        int dx = getScrollX() - position * mPagerWidth;
        mChildIndex = position;
        smoothScrollBy(-dx);
    }

    public void setNoScroll(boolean isScroll) {
        this.isScroll = isScroll;
    }

    private boolean isScroll = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isScroll) {
            return false;
        }
        boolean isIntercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    isIntercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastInterceptX;
                int deltaY = y - mLastInterceptY;
                if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) >= mTouchSlop) {
                    //横向滑动的距离大于竖直滑动距离并且大于最小能识别成滑动的距离
                    isIntercept = true;
                } else {
                    isIntercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                break;
        }

        mLastInterceptX = x;
        mLastInterceptY = y;
        mLastX = x;
        //Log.i(TAG, "isIntercept===" + isIntercept);
        return isIntercept;
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isScroll) {
            return false;
        }
        int x = (int) event.getX();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                if (mChildIndex == 0 && deltaX > 0) {
                    scrollToLeft(getScrollX(), deltaX);
                } else if (mChildIndex == mPageSize - 1 && deltaX < 0) {
                    scrollToRight(getScrollX() - (mPageSize - 1) * mPagerWidth, deltaX);
                } else {
                    scrollBy(-deltaX, 0);
                    pageScrolled();
                }
                pageScrolled();
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                //设置速度的基准
                mVelocityTracker.computeCurrentVelocity(1000);
                //滑动手速
                float velocityX = mVelocityTracker.getXVelocity();
                int oldIndex = mChildIndex;
                if (Math.abs(velocityX) > 100) {
                    //速度大于100说明是想翻页,然后判断方向
                    if (scrollX > 0 && scrollX < (mPageSize - 1) * getWidth()) {
                        mChildIndex = velocityX > 0 ? mChildIndex - 1 : mChildIndex + 1;
                    }
                } else {
                    //即使滑动速度没有大于50，但是滑动距离已经大于当前View半个屏幕也算是翻页
                    mChildIndex = (scrollX + mPagerWidth / 2) / mPagerWidth;
                    //Log.i(TAG, "mChildIndex===" + mChildIndex);
                }
                mChildIndex = Math.max(0, Math.min(mChildIndex, mPageSize - 1));
                //剩余的滑动量
                int dx = mChildIndex * mPagerWidth - scrollX;
                smoothScrollBy(dx);
                mVelocityTracker.clear();
                if (oldIndex != mChildIndex) {
                    Log.i(TAG, "当前页======：" + mChildIndex);
                    if (onPageChangeListener != null) {
                        onPageChangeListener.onPageSelected(mChildIndex);
                    }
                }
                break;
        }
        mLastX = x;
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = 0;
        int measureHeight = 0;
        final int childCount = getChildCount();
        //宽
        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpaceMode = MeasureSpec.getMode(widthMeasureSpec);
        //高
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpaceMode = MeasureSpec.getMode(heightMeasureSpec);
        measureChildren(MeasureSpec.makeMeasureSpec(widthSpaceSize, widthSpaceMode), heightMeasureSpec);
        if (childCount == 0) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else if (widthSpaceMode == MeasureSpec.AT_MOST && heightSpaceMode == MeasureSpec.AT_MOST) {
            //宽高都是自适应
            final View childrenView = getChildAt(0);
            measureWidth = childrenView.getMeasuredWidth() * childCount;
            measureHeight = childrenView.getMeasuredHeight();
            setMeasuredDimension(measureWidth, measureHeight);
        } else if (heightSpaceMode == MeasureSpec.AT_MOST) {
            final View childView = getChildAt(0);
            measureHeight = childView.getMeasuredHeight();
            setMeasuredDimension(widthSpaceSize, measureHeight);
        } else if (widthSpaceMode == MeasureSpec.AT_MOST) {
            final View childView = getChildAt(0);
            measureWidth = childView.getMeasuredWidth() * childCount;
            setMeasuredDimension(measureWidth, heightSpaceSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        mPageSize = childCount;
        mPagerWidth = getWidth();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = mPagerWidth;
                final int childHeight = childView.getMeasuredHeight();
                childView.layout(childLeft, 0, childLeft + childWidth, childHeight);
                childLeft += childWidth;
            }
        }
    }

    private boolean isLimit = true;
    /**
     * 滑动阻尼感
     *
     * @param scrollX
     * @param destX
     */
    private void scrollToLeft(int scrollX, int destX) {
        if(isLimit && scrollX <= 0){
            return;
        }
        float x = -scrollX / 600f;
        //Log.i(TAG, "XXX==" + x);
        if (destX < 5) {
            if (x > 0.5f) {
                destX /= 2;
            } else {
                destX = (int) (destX - destX * x);
            }
        } else {
            destX = (int) (destX - destX * x);
        }
        scrollBy(-destX, 0);
    }

    /**
     * 滑动阻尼感
     *
     * @param scrollX
     * @param destX
     */
    private void scrollToRight(int scrollX, int destX) {
        if(isLimit && scrollX >= 0){
            return;
        }
        float x = -scrollX / 600f;

        if (destX < 5) {
            if (x > 0.5f) {
                destX /= 2;
            } else {
                destX = (int) (destX + destX * x);
            }
        } else {
            destX = (int) (destX + destX * x);
        }
        scrollBy(-destX, 0);
    }

    private void pageScrolled() {
        int scrollX = getScrollX();
        if (onPageChangeListener != null) {
            if (scrollX > 0 && scrollX < (mPageSize - 1) * getWidth()) {
                onPageChangeListener.onPageChange((float)scrollX/getWidth());
            }
        }
    }

    /**
     * 弹性滑动
     *
     * @param dx
     */
    private void smoothScrollBy(int dx) {
        mScroller.startScroll(getScrollX(), 0, dx, 0, 550);
        invalidate();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            pageScrolled();
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        //回收资源
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    public interface OnPageChangeListener {
        void onPageSelected(int position);

        void onWidthChange(int position);

        void onPageChange(float change);
    }
}
