package com.jaiky.imagespickers.preview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.view.View.OnTouchListener;

//自定义ImageView,需要找到ImageView加载完图片的一个回调，所以实现了OnGlobalLayoutListener这个接口
public class ZoomImageView extends ImageView implements OnGlobalLayoutListener,
        OnScaleGestureListener, OnTouchListener {

    public boolean mOnce = false;// 表示第一次加载图片的时候

    /**
     * 初始化时缩放的值
     */
    private float mInitScale;

    /**
     * 双击放大时到达的值
     */
    private float mMidScale;

    /**
     * 放大的最大值
     */
    private float mMaxScale;

    /**
     * 用于图片缩放和移动的对象
     */
    private Matrix matrix;

    /**
     * 捕获用户多指触控缩放的比例类
     */
    private ScaleGestureDetector mScaleGestureDetector;

    // --------------------自由移动使用的变量
    /**
     * 记录上一次多点触控的数量
     */
    private int mLastPointerCount;

    // 记录最后一次多点触控的中心点
    private float mLastX;
    private float mLastY;

    /**
     * 系统默认的判断移动的距离
     */
    private int mTouchSlop;

    /**
     * 是否移动
     */
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;

    private boolean isCheckTopAndBottom;

    // -------------------------双击放大与缩小
    /**
     * 监听双击事件类
     */
    private GestureDetector mGestureDetector;

    /**
     * 用来判断图片是否正在缩放
     */
    public boolean isAutoScale;

    /**
     * 是否单击
     */
    public  boolean isSingle;


    private OnIsSingleListener mOnIsSingleListener;

    // 所有的事件写到这里面就OK了
    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        matrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        // 需要实现OnScaleGestureListener接口
        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        setOnTouchListener(this);
        // 得到系统设定的移动比较值
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // 对检测双击事件类进行实例化
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    // 双击时
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        // 如果正在放大，不处理用户的双击事件
                        if (isAutoScale)
                            return true;
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < mMidScale) {
							/*
							 * matrix.postScale(mMidScale/getScale(),mMidScale/
							 * getScale(),x,y); setImageMatrix(matrix);
							 */
                            postDelayed(new AutoScaleRunnable(mMidScale, x, y),
                                    16);
                            isAutoScale = true;
                        } else {
							/*
							 * matrix.postScale(mInitScale/getScale(),mInitScale/
							 * getScale(),x,y); setImageMatrix(matrix);
							 */
                            postDelayed(
                                    new AutoScaleRunnable(mInitScale, x, y), 16);
                            isAutoScale = true;
                        }
                        return true;
                    }
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if(mOnIsSingleListener!=null)
                            mOnIsSingleListener.onSingleClick();
                        isSingle=true;
                        return true;
                    }
                });
    }

    /**
     * 自动缩放与缩小进程
     *
     * @author csj
     *
     */
    private class AutoScaleRunnable implements Runnable {
        /**
         * 缩放的目标值
         */
        private float mTargetScale;
        // 缩放的中心点
        private float x;
        private float y;
        // 缩放的梯度值
        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        private float tmpScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            // 如果缩放的大小小于目标值
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            // 进行缩放，
            matrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(matrix);
            float currentScale = getScale();
            if (tmpScale > 1.0f && currentScale < mTargetScale
                    || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                // 再次调用此Runnable对象，每16毫秒实现一次的定时操作
                postDelayed(this, 16);
            }
            // 设置为我们的目标值
            else {
                float scale = mTargetScale / currentScale;
                matrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(matrix);
                isAutoScale = false;
            }
        }
    }

    // 调用三个参数的方法
    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // 调用两个参数的方法
    public ZoomImageView(Context context) {
        this(context, null);
    }

    // 当VIEW加载Window的时候调用
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 注册接口
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    // 当VIEW移除出Window的时候调用，批注允许您选择性地取消特定代码段（即，类或方法）中的警告
    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 移除接口
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    // 全部布局加载完成时调用
    /**
     * 获取ImageView加载完成时的图片
     */
    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            // 得到控件的宽和高，
            int width = getWidth();
            int height = getHeight();

            // 得到我们的图片，以及它的宽和高
            Drawable d = getDrawable();
            if (d == null)
                return;

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;// 默认缩放的值

            // 图片的宽度大于控件的宽度且图片的高度小于控件的高度
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }

            // 图片的高度大于控件的高度且图片的宽度小于控件的宽度
            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }

            if (dw > width && dh > height) {
                // 取缩放的最小值，这样图片才能完全显示
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            if (dw < width && dh < height) {
                // 取放大的最大 值
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            mInitScale = scale;// 设置初始化时的缩放值
            mMaxScale = mInitScale * 4;// 设置图片最大的缩放值
            mMidScale = mInitScale * 2;// 设置双击图片时的防放大值

            // 将图片移动至控件的中心
            int dx = width / 2 - dw / 2;// x轴移动的距离
            int dy = height / 2 - dh / 2;// y轴移动的距离

            matrix.postTranslate(dx, dy);// 设置图片移动的距离
            matrix.postScale(mInitScale, mInitScale, width / 2, height / 2); // 设置图片的缩放,后两个参数的是缩放的中心点
            setImageMatrix(matrix);

            mOnce = true;
        }
    }

    /**
     * 获取当前图片的缩放值
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    // 缩放的区间：mInitScale到mMaxScale
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scale = getScale();
        // 获得手势缩放的值
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }
        // 当前图片的缩放值小于最大的缩放值且检测到手势想放大，或者当前图片的缩放值大于最小的缩放值且检测到手势想缩小
        if ((scale < mMaxScale && scaleFactor > 1.0f)
                || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }

            // detector.getFocusX(),detector.getFocusY()获取缩放时的中心坐标
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(),
                    detector.getFocusY());

            checkBorderAndCenterWhenScale();

            setImageMatrix(matrix);
        }
        return true;
    }

    /**
     * 获得图片放大和缩小以后的宽和高，以及左右上下
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix mMatrix = matrix;
        RectF rectF = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }
        return rectF;
    }


    /**
     * 在缩放的时候，进行边界控制和位置控制,防止出现白边
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        // 进行移动的X和Y的距离
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        // 计算白边并且移动
        if (rect.width() >= width) {
            // 判断图片的左边是否有白边
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            // 判断图片的右边边是否有白边
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }

        if (rect.height() >= height) {
            // 判断图片的上边是否有白边
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            // 判断图片的下边是否有白边
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }

        // 如果宽度和高度小于控件的宽和高，则让其居中
        if (rect.width() < width) {
            deltaX = getWidth() / 2f - rect.right + rect.width() / 2f;
        }
        if (rect.height() < height) {
            deltaY = getHeight() / 2f - rect.bottom + rect.height() / 2f;
        }
        matrix.postTranslate(deltaX, deltaY);
        setImageMatrix(matrix);

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 双击的时候不执行移动事件
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 把事件交给mScaleGestureDetector去处理,event包涵手指触控的坐标
        mScaleGestureDetector.onTouchEvent(event);
        // 缩放中心点的中心位置
        float x = 0;
        float y = 0;
        // 拿到多点触控的数量
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;

        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                RectF rect = getMatrixRectF();
                if (rect.width() > getWidth() + 0.01
                        || rect.height() > getHeight() + 0.01) {
                    if ((rect.width() == getWidth() + Math.abs(rect.left))||(Math.abs(rect.left)<0.01)) {
                    }
                    // 阻止父级VIEW截获touch事件
                    // 判断父级是否是ViewPager
                    else if (getParent() instanceof ViewPager)
                    {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                }
                break;

            // 移动时
            case MotionEvent.ACTION_MOVE:
                RectF rect2 = getMatrixRectF();
                // x轴和y轴的移动量
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (rect2.width() > getWidth() + 0.01
                        || rect2.height() > getHeight() + 0.01) {
                    if ((rect2.width() == getWidth() + Math.abs(rect2.left) && dx < 0)||(Math.abs(rect2.left)<0.01)&&dx >0) {
                    }
                    // 阻止父级VIEW截获touch事件
                    // 判断父级是否是ViewPager
                    else if (getParent() instanceof ViewPager)
                    {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                }

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag) {
                    RectF rectF = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        // 如果宽度小于控件宽度，不允许横向移动
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        // 如果高度小于控件高度，不允许横向移动
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        // 让图片移动
                        matrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(matrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastPointerCount = 0;

                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 当移动时，进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();
        // 上边有白边
        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }
        // 下边有白边
        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }
        // 左边有白边
        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }
        // 右边有白边
        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }
        matrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 判断是否移动
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {

        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
    /**
     * 声明一个接口，等单击的时候回调
     * @author Administrator
     *
     */
    public interface OnIsSingleListener
    {
        public void onSingleClick();
    }

    public void setOnIsSingleListener(OnIsSingleListener isSingleListener)
    {
        mOnIsSingleListener=isSingleListener;
    }

}
