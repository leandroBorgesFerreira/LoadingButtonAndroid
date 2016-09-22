package br.com.simplepass.loading_button_lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by hinovamobile on 22/09/16.
 */
public class CircularRevealAnimatedDrawable extends Drawable implements Animatable {

    private boolean mIsFilled;
    private Paint mPaint;
    private Paint mPaintImageReady;
    private View mAnimatedView;
    private float mRadius;
    private float mFinalRadius;
    private ValueAnimator mRevealInAnimation;
    private boolean isRunning;
    private float mCenterWidth;
    private float mCenterHeith;
    private Bitmap mReadyImage;
    private int mImageReadyAlpha;
    private Rect mBounds;

    private Context mContext;

    public CircularRevealAnimatedDrawable(View view, int fillColor, Bitmap bitmap) {
        mAnimatedView = view;
        isRunning = false;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(fillColor);

        mPaintImageReady = new Paint();
        mPaintImageReady.setAntiAlias(true);
        mPaintImageReady.setStyle(Paint.Style.FILL);
        mPaintImageReady.setColor(Color.TRANSPARENT);


        mReadyImage = bitmap;
        mImageReadyAlpha = 0;

        mRadius = 0;
    }

    public boolean isFilled() {
        return mIsFilled;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        mBounds = bounds;
        mFinalRadius = (bounds.right - bounds.left)/2;
        mCenterWidth = (bounds.right - bounds.left)/2;
        mCenterHeith = (bounds.bottom - bounds.top)/2;
    }

    private void setupAnimations(){
        final ValueAnimator alphaAnimator = ValueAnimator.ofInt(0, 255);
        alphaAnimator.setDuration(80);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mImageReadyAlpha = (int) animation.getAnimatedValue();
                invalidateSelf();
                mAnimatedView.invalidate();
            }
        });


        mRevealInAnimation = ValueAnimator.ofFloat(0, mFinalRadius);
        mRevealInAnimation.setInterpolator(new DecelerateInterpolator());
        mRevealInAnimation.setDuration(120);
        mRevealInAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius = (float) animation.getAnimatedValue();
                invalidateSelf();
                mAnimatedView.invalidate();
            }
        });
        mRevealInAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsFilled = true;

                alphaAnimator.start();
            }
        });


    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        setupAnimations();

        isRunning = true;

        mRevealInAnimation.start();
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        isRunning = false;

        mRevealInAnimation.cancel();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mCenterWidth, mCenterHeith, mRadius, mPaint);

        if(mIsFilled) {
            mPaintImageReady.setAlpha(mImageReadyAlpha);
            canvas.drawBitmap(mReadyImage, null, mBounds, mPaintImageReady);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
