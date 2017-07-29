package com.ccr.ratingbarlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 在此写用途
 *
 * @Author: Acheng
 * @Email: 345887272@qq.com
 * @Date: 2017-07-28 11:07
 * @Version: V1.0 <描述当前版本功能>
 */

public class AchengRatingBar extends View {

    /**
     * 默认评分最大值
     */
    private final static int DEFAULT_MAX_COUNT = 5;
    /**
     * 默认评分最小跨度
     */
    private final static float DEFAULT_MIN_STEP = 1f;
    /**
     * 默认图片间隔(dp)
     */
    private final static int DEFAULT_SPAN_SIZE = 8;
    /**
     * 默认分数
     */
    private final static float DEFAULT_RATING = 1f;

    /**
     * 评分最大值
     */
    private int mMaxCount;
    /**
     * 已评分的图片
     */
    private Bitmap mFront;
    /**
     * 未评分的图片
     */
    private Bitmap mBack;
    /**
     * 评分最小间隔
     */
    private float mMinStep;
    /**
     * 各个图片之间的间距
     */
    private int mSpanSize;
    /**
     * 评分
     */
    private float mRating;
    /**
     * ImageRatingView的宽度
     */
    private int mViewWidth;
    /**
     * ImageRatingView的高度
     */
    private int mViewHeight;
    /**
     * 图片展示出来的宽度
     */
    private int mBitmapDstWidth;
    /**
     * 图片展示出来的高度
     */
    private int mBitmapDstHeight;
    /**
     * 是否可触摸操作
     */
    private boolean mTouchable;
    /**
     * 评分变化监听器
     */
    private OnRatingChangedListener mListener;

    public AchengRatingBar(Context context) {
        this(context, null);
    }

    public AchengRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AchengRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        correctedRatingValue();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageRatingView);
        mMinStep = a.getFloat(R.styleable.ImageRatingView_minStep, DEFAULT_MIN_STEP);
        mMaxCount = a.getInt(R.styleable.ImageRatingView_maxCount, DEFAULT_MAX_COUNT);
        mFront = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.ImageRatingView_frontImage, R.drawable.ic_star_amber_a200_24dp));
        mBack = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.ImageRatingView_backImage, R.drawable.ic_star_white_24dp));
        mSpanSize = a.getDimensionPixelSize(R.styleable.ImageRatingView_spanSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_SPAN_SIZE, getResources().getDisplayMetrics()));
        mRating = a.getFloat(R.styleable.ImageRatingView_rating, DEFAULT_RATING);
        mTouchable = a.getBoolean(R.styleable.ImageRatingView_touchable, false);
        mBitmapDstWidth = a.getDimensionPixelSize(R.styleable.ImageRatingView_imageWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                0, getResources().getDisplayMetrics()));
        mBitmapDstHeight = a.getDimensionPixelSize(R.styleable.ImageRatingView_imageHeight, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                0, getResources().getDisplayMetrics()));
        a.recycle();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mFront.getWidth() != mBack.getWidth() || mFront.getHeight() != mBack.getHeight()) {
            new Throwable("the front and back bitmap's width or height must be equal");
            setMeasuredDimension(mFront.getWidth(), mFront.getHeight());
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;

        }

        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
            mBitmapDstWidth = (mViewWidth - mSpanSize * (mMaxCount - 1)) / mMaxCount;
        } else {
            //如果是wrap_content则等于bitmap数量*图片宽度+间隔宽度*（bitmap数量-1）
            if (mBitmapDstWidth == 0) {
                mBitmapDstWidth = mFront.getWidth();
            }
            mViewWidth = mMaxCount * mBitmapDstWidth + mSpanSize * (mMaxCount - 1);
        }

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
            mBitmapDstHeight = mViewHeight > mFront.getHeight() ? mFront.getHeight() : mViewHeight;
        } else {
            if (mBitmapDstHeight == 0) {
                mBitmapDstHeight = mFront.getHeight();
            }
            mViewHeight = mBitmapDstHeight;
        }
        setMeasuredDimension(mViewWidth, mViewHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect src = new Rect(0, 0, mFront.getWidth(), mFront.getHeight());
        drawFront(canvas, src);
        drawBack(canvas, src);
        if (mRating != mMaxCount) {
            drawLastFront(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mTouchable)
            return false;
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            rating(x);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (x < 0 || x > mViewWidth || y < 0 || y > mViewHeight)
                return false;
            rating(x);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (x < 0 || x > mViewWidth || y < 0 || y > mViewHeight)
                return false;
            rating(x);
        }
        if (mListener != null) {
            mListener.ratingChanged(mRating);
        }
        return true;
    }

    /**
     * 评分操作
     *
     * @param x
     */
    private void rating(float x) {
        int position = (int) (x / (mViewWidth + mSpanSize) * mMaxCount);
        if (x >= mBitmapDstWidth * (position + 1) + mSpanSize * position) {
            mRating = position + 1;
        } else {
            float width = x - mBitmapDstWidth * position - mSpanSize * position;
            mRating = position + width / mBitmapDstWidth;
        }
        correctedRatingValueForTouch();
        invalidate();
    }


    /**
     * 画出已打分的部分,(除了最后一个)
     *
     * @param canvas
     * @param src
     */
    private void drawFront(Canvas canvas, Rect src) {
        for (int i = 0; i < (int) mRating; i++) {
            int left = i * (mBitmapDstWidth + mSpanSize);
            int right = (i + 1) * mBitmapDstWidth + i * mSpanSize;
            Rect dst = new Rect(left, 0, right, mBitmapDstHeight);
            canvas.drawBitmap(mFront, src, dst, null);
        }
    }

    /**
     * 画出未打分的部分
     *
     * @param canvas
     * @param src
     */
    private void drawBack(Canvas canvas, Rect src) {
        for (int i = (int) (mRating + 1); i < mMaxCount; i++) {
            int left = i * (mBitmapDstWidth + mSpanSize);
            int right = (i + 1) * mBitmapDstWidth + i * mSpanSize;
            Rect dst = new Rect(left, 0, right, mBitmapDstHeight);
            canvas.drawBitmap(mBack, src, dst, null);
        }
    }

    /**
     * 打分的最后那个特殊处理
     *
     * @param canvas
     */
    private void drawLastFront(Canvas canvas) {
        //先画front部分
        //rating小数点前的值。
        int rating = (int) mRating;
        float frontPart = mRating - rating;
        int frontWidth = (int) (frontPart * mFront.getWidth());
        Rect srcFront = new Rect(0, 0, frontWidth, mFront.getHeight());
        int leftFront = rating * (mBitmapDstWidth + mSpanSize);
        int rightFront = (int) ((rating + frontPart) * mBitmapDstWidth + rating * mSpanSize);
        Rect dstFront = new Rect(leftFront, 0, rightFront, mBitmapDstHeight);
        canvas.drawBitmap(mFront, srcFront, dstFront, null);

        //back部分
        int backWidth = (int) (frontPart * mFront.getWidth());
        Rect srcBack = new Rect(backWidth, 0, mFront.getWidth(), mFront.getHeight());
        int leftBack = (int) ((rating + frontPart) * mBitmapDstWidth + rating * mSpanSize);
        int rightBack = (rating + 1) * mBitmapDstWidth + rating * mSpanSize;
        Rect dstBack = new Rect(leftBack, 0, rightBack, mBitmapDstHeight);
        canvas.drawBitmap(mBack, srcBack, dstBack, null);
    }


    /**
     * 修正rating值
     */
    private void correctedRatingValue() {
        if (mRating > mMaxCount) {
            mRating = mMaxCount;
        } else if (mRating < 0) {
            mRating = 0;
        }
        //最后一个跨度 进行四舍五入的判断
        float pointAfter = mRating % mMinStep > mMinStep / 2 ? mMinStep : 0;
        //除去最后个跨度
        float pointFront = mRating - mRating % mMinStep;
        mRating = pointAfter + pointFront;
    }

    /**
     * 触摸时，修正rating值
     */
    private void correctedRatingValueForTouch() {
        if (mRating % mMinStep == 0)
            return;
        mRating = mRating - mRating % mMinStep + mMinStep;
    }

    /**
     * 设置评分
     *
     * @param rating
     */
    public void setRating(float rating) {
        mRating = rating;
        correctedRatingValue();
        invalidate();
    }

    /**
     * 获得当前评分
     *
     * @return
     */
    public float getRating() {
        return mRating;
    }

    /**
     * 设置是否可触摸
     *
     * @param touchable
     */
    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    /**
     * 评分变化监听
     */
    public interface OnRatingChangedListener {

        void ratingChanged(float rating);
    }

    /**
     * 设置评分监听
     * @param l
     */
    public void setOnRatingChangedListener(OnRatingChangedListener l) {
        mListener = l;
    }
}
