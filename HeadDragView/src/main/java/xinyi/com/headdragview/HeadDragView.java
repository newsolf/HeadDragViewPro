package xinyi.com.headdragview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by 陈章 on 2017/6/13 0013.
 * func:
 * 一款自定义View，可以控制body的拖拽遮挡head以节约空间。
 */

public class HeadDragView extends LinearLayout{
    private static final String TAG = "HeadDragView";
    private ViewDragHelper dragHelper;
    private View mHeadContent;
    private View mBodyContent;
    private int mHeight;
    private int mWidth;
    private int mHeadHeight;
    private int mHeadWidth;
    private int mBodyHeight;
    private int mBodyWidth;

    private Status status = Status.OPEN; // 默认是关闭状态
    private OnDragUpdateListener onDragUpdateListener; // 拖拽监听

    public HeadDragView(Context context) {
        this(context,null);
    }

    public HeadDragView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeadDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        // 1. ViewDragHelper拖拽辅助类的初始化
        dragHelper = ViewDragHelper.create(this, 1.0f, callback);
    }


    // 定义三种状态
    public  enum Status {
        CLOSE,  // 关闭状态
        DRAGING,  // 拖拽状态
        OPEN // 打开状态
    }

    // 定义自己的接口监听
    public interface OnDragUpdateListener {
        void onOpen();
        void onClose();
        void onDraging(float percent);
    }


    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public OnDragUpdateListener getOnDragUpdateListener() {
        return onDragUpdateListener;
    }

    public void setOnDragUpdateListener(OnDragUpdateListener onDragUpdateListener) {
        this.onDragUpdateListener = onDragUpdateListener;
    }




    // 3. 重写回调监听
    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        // a. 返回值决定了当前child是否可以被拖拽
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // child 被拖拽的子View
            // pointerId 多点触摸手指id
            return true;
        }

        // 获取视图水平方向拖拽范围, child被拖拽的子View,  必须 > 0, 用于动画执行时长的计算
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mHeadHeight;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if(child == mBodyContent){
                // 拖拽主面板
                top = fixTop(top);
            }
            return top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        // c. 当视图位置变化时调用, 处理伴随动画, 更新状态, 执行监听
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            // changedView 被移动的View
            // left 当前移动到的水平位置
            // dx 刚刚发生的水平变化量
//			System.out.println("onViewPositionChanged: left: " + left + " dx: "+ dx);

            // 拖拽的是左面板, 让左面板位置不动, 让自己的变化量传递给主面板
            if(changedView == mHeadContent){
                // 左面板位置不动
                mHeadContent.layout(0, 0, mHeadWidth, mHeadHeight);
                // 移动主面板
                int newTop = mBodyContent.getTop() + dy;
                // 限定范围
                newTop = fixTop(newTop);
                mBodyContent.layout(0, newTop,mBodyWidth, newTop + mBodyHeight);
            }

            dispathDragEvent();

            invalidate(); // 为了兼容低版本, 手动刷新界面.
        }

        private int fixTop(int top) {
            if(top < 0){
                return 0; // 限定左边范围
            }else if (top > mHeadHeight) {
                return mHeadHeight; // 限定右边范围
            }
            return top;
        }



        // d. 当视图被释放时调用
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // releasedChild 被释放的子View
            // xvel 松手时候, 水平方向的速度, 向右+,  向左-

            System.out.println("onViewReleased: " + yvel);

            // 判断所有打开情况
            if(yvel == 0 && mBodyContent.getTop() > mHeadHeight * 0.5f){
                open();
            }else if (yvel > 0) {
                open();
            }else {
                close();
            }
        }

    };

    // 执行伴随动画, 更新状态, 执行监听
    protected void dispathDragEvent() {
        // 时间轴.0.0f -> 1.0f
        float percent = mBodyContent.getTop()  * 1.0f / mHeadHeight;

        System.out.println("percent: " + percent);

        //- 左面板: 缩放动画, 平移动画, 透明度
        animViews(percent);

        if(onDragUpdateListener != null){
            onDragUpdateListener.onDraging(percent);
        }

        // 记录上一次的状态
        Status lastStatus = status;
        // 获取最新状态
        status = updateStatus(percent);

        // 状态发生变化的时候执行监听, 进行状态的对比
        if(lastStatus != status){
            if(status == Status.OPEN){
                // 状态发生变化, 并且最新状态是打开状态, 执行监听.
                if(onDragUpdateListener != null){
                    onDragUpdateListener.onOpen();
                }
            } else if (status == Status.CLOSE) {
                // 状态发生变化, 并且最新状态是关闭状态, 执行监听.
                if(onDragUpdateListener != null){
                    onDragUpdateListener.onClose();
                }
            }
        }
    }

    // 获取最新状态
    private Status updateStatus(float percent) {
        if(percent == 0){
            return Status.CLOSE;
        }else if (percent == 1.0f) {
            return Status.OPEN;
        }

        return Status.DRAGING;
    }


    private void animViews(float percent) {
        // 透明度 0.2f -> 1.0f
        ViewHelper.setAlpha(mHeadContent, evaluate(percent, 0.2f, 1.0f));

        //		- 背景: 亮度变化, 黑色 -> 透明色
        if(mHeadContent.getBackground() != null){
            mHeadContent.getBackground().setColorFilter((Integer)evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
        }
    }

    // 颜色过度器
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }

    /**
     * TypeEvaluator 类型估值器.
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }


    protected void close() {
        close(true);
    }
    public void close(boolean isSmooth){
        System.out.println("关闭");
        int finalTop = 0;
        if(isSmooth){
            // 平滑动画
//			Scroller
            // 1. 触发一个平滑动画
            if(dragHelper.smoothSlideViewTo(mBodyContent, 0, finalTop)){
                // true 当前动画还没有结束, 没有指定位置, 需要重绘界面.
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }else {
            mBodyContent.layout(0, finalTop,   mBodyWidth, finalTop + mBodyHeight);
        }
    }

    protected void open() {
        open(true);
    }

    public void open(boolean isSmooth){
        System.out.println("打开");
        int finalTop = mHeadHeight;
        if(isSmooth){
            // 平滑动画
//			Scroller
            // 1. 触发一个平滑动画
            if(dragHelper.smoothSlideViewTo(mBodyContent, 0, finalTop)){
                // true 当前动画还没有结束, 没有指定位置, 需要重绘界面.
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }else {
            mBodyContent.layout(0, finalTop, mBodyWidth, finalTop + mBodyHeight);
        }

    }


    // 2. 维持平滑动画的继续
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    // 2. 转交 触摸拦截判断, 触摸事件
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(android.view.MotionEvent event) {
        // DOWN -> MOVE -> UP

        try {
            dragHelper.processTouchEvent(event);
        } catch (Exception e) {
        }

        return true; // 当前ViewGroup可以处理event事件.
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeadContent = getChildAt(0);
        mBodyContent = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeadWidth = mHeadContent.getMeasuredWidth();
        mHeadHeight = mHeadContent.getMeasuredHeight();

        mBodyWidth = mBodyContent.getMeasuredWidth();
        mBodyHeight = mBodyContent.getMeasuredHeight();

        // 获取当前Draglyout的宽高, 也可以直接获取mMainContent的宽高(占满父控件), 值一样
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        Log.d(TAG, "mHeadWidth: " + mHeadWidth);
        Log.d(TAG, "mHeadHeight: " + mHeadHeight);

    }
}
