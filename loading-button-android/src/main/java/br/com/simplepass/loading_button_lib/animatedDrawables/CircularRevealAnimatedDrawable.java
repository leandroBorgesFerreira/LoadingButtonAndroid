package br.com.simplepass.loading_button_lib.animatedDrawables;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Leandro Ferreira on 22/09/16.
 *
 * An animation of the 'Done' status. It makes a circular reveal of a background color and the
 * than show the image passed in the constructor.
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
    private float bitMapXOffset;
    private float bitMapYOffset;

    /**
     *
     * @param view The view that if being animated
     * @param fillColor The color of the background that will the revealed
     * @param bitmap The animage that will be shown in the end of the animation.
     */
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

    /**
     * The method is called when bounds change
     *
     * @param bounds
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int bitMapWidth = (int) ((bounds.right - bounds.left) * 0.6);
        int bitMapHeight = (int) ((bounds.bottom - bounds.top) * 0.6);

        bitMapXOffset =(float) (((bounds.right - bounds.left) - bitMapWidth)/2);
        bitMapYOffset =(float) (((bounds.bottom - bounds.top) - bitMapHeight)/2);

        mReadyImage = Bitmap.createScaledBitmap(mReadyImage, bitMapWidth, bitMapHeight, false);

        mFinalRadius = (bounds.right - bounds.left)/2;
        mCenterWidth = (bounds.right + bounds.left)/2;
        mCenterHeith = (bounds.bottom + bounds.top)/2;
    }

    /**
     * Setup all the animations. There are a reveal animation to show the button background
     * and a alpha animation to show the bitmap.
     */
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

    /**
     * Starts the animation
     */
    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        setupAnimations();
        isRunning = true;
        mRevealInAnimation.start();
    }

    /**
     * Stops the animation
     */
    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        isRunning = false;
        mRevealInAnimation.cancel();
    }

    /**
     *
     * @return Return if its running or not.
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mCenterWidth, mCenterHeith, mRadius, mPaint);

        if(mIsFilled) {
            mPaintImageReady.setAlpha(mImageReadyAlpha);
            canvas.drawBitmap(mReadyImage, bitMapXOffset, bitMapYOffset, mPaintImageReady);
        }
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(ColorFilter colorFilter) {}

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void dispose() {
        if (mRevealInAnimation != null) {
            mRevealInAnimation.end();
            mRevealInAnimation.removeAllUpdateListeners();
            mRevealInAnimation.cancel();
        }

        mRevealInAnimation = null;
    }
}
