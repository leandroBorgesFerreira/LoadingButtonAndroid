package br.com.simplepass.loading_button_lib.customViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.ViewGroup;

import br.com.simplepass.loading_button_lib.R;
import br.com.simplepass.loading_button_lib.Utils;
import br.com.simplepass.loading_button_lib.UtilsJava;
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularAnimatedDrawable;
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularRevealAnimatedDrawable;
import br.com.simplepass.loading_button_lib.interfaces.AnimatedButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;


/**
 * Made by Leandro Ferreira.
 *
 */
public class CircularProgressButton extends AppCompatButton implements AnimatedButton, CustomizableByCode {
    private enum State {
        PROGRESS, IDLE, DONE, STOPED
    }

    //private CircularAnimatedDrawable mAnimatedDrawable;
    private GradientDrawable mGradientDrawable;

    private boolean mIsMorphingInProgress;
    private State mState;
    private CircularAnimatedDrawable mAnimatedDrawable;
    private CircularRevealAnimatedDrawable mRevealDrawable;
    private AnimatorSet mAnimatorSet;

    private int mFillColorDone;

    private Bitmap mBitmapDone;

    private Params mParams;

    private boolean doneWhileMorphing;

    /**
     *
     * @param context
     */
    public CircularProgressButton(Context context) {
        super(context);

        init(context, null, 0, 0);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public CircularProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CircularProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    @TargetApi(23)
    public CircularProgressButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Commom initializer method.
     *
     * @param context Context
     * @param attrs Atributes passed in the XML
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        mParams = new Params();

        mParams.mPaddingProgress = 0f;

        if(attrs == null) {
            loadGradientDrawable(UtilsJava.getDrawable(getContext(), R.drawable.shape_default));
        } else{
            int[] attrsArray = new int[] {
                    android.R.attr.background, // 0
            };

            TypedArray typedArray =  context.obtainStyledAttributes(attrs, R.styleable.CircularProgressButton, defStyleAttr, defStyleRes);
            TypedArray typedArrayBG = context.obtainStyledAttributes(attrs, attrsArray, defStyleAttr, defStyleRes);

            loadGradientDrawable(typedArrayBG.getDrawable(0));

            mParams.mInitialCornerRadius = typedArray.getDimension(
                    R.styleable.CircularProgressButton_initialCornerAngle, 0);
            mParams.mFinalCornerRadius = typedArray.getDimension(
                    R.styleable.CircularProgressButton_finalCornerAngle, 100);
            mParams.mSpinningBarWidth = typedArray.getDimension(
                    R.styleable.CircularProgressButton_spinning_bar_width, 10);
            mParams.mSpinningBarColor = typedArray.getColor(R.styleable.CircularProgressButton_spinning_bar_color,
                    Utils.Companion.getColorWrapper(context, android.R.color.black));
            mParams.mPaddingProgress = typedArray.getDimension(R.styleable.CircularProgressButton_spinning_bar_padding, 0);

            typedArray.recycle();
            typedArrayBG.recycle();
        }

        mState = State.IDLE;

        mParams.mText = this.getText().toString();
        mParams.mDrawables = this.getCompoundDrawablesRelative();
        if (mGradientDrawable != null) {
            setBackground(mGradientDrawable);
        }
    }

    @Override
    public void setBackgroundColor (int color) {
        mGradientDrawable.setColor(color);
    }

    @Override
    public void setBackgroundResource (@ColorRes int resid) {
        mGradientDrawable.setColor(ContextCompat.getColor(getContext(), resid));
    }

    private void loadGradientDrawable(Drawable drawable) {
        try {
            mGradientDrawable = (GradientDrawable) drawable;
        } catch (ClassCastException e) {
            if(drawable instanceof ColorDrawable){
                ColorDrawable colorDrawable = (ColorDrawable) drawable;

                mGradientDrawable = new GradientDrawable();
                mGradientDrawable.setColor(colorDrawable.getColor());
            } else if(drawable instanceof StateListDrawable){
                StateListDrawable stateListDrawable = (StateListDrawable) drawable;

                try {
                    mGradientDrawable = (GradientDrawable) stateListDrawable.getCurrent();
                } catch (ClassCastException e1) {
                    throw new RuntimeException("Error reading background... Use a shape or a color in xml!", e1.getCause());
                }
            }
        }
    }

    @Override
    public void setSpinningBarColor(int color) {
        mParams.mSpinningBarColor = color;
        if (mAnimatedDrawable != null) {
            mAnimatedDrawable.setLoadingBarColor(color);
        }
    }

    @Override
    public void setSpinningBarWidth(float width) {
        mParams.mSpinningBarWidth = width;
    }

    @Override
    public void setDoneColor(int color) {
        mParams.mDoneColor = color;
    }

    @Override
    public void setPaddingProgress(float padding) {
        mParams.mPaddingProgress = padding;
    }

    @Override
    public void setInitialHeight(int height) {
        mParams.mInitialHeight = height;
    }

    @Override
    public void setInitialCornerRadius(float radius) {
        mParams.mInitialCornerRadius = radius;
    }

    @Override
    public void setFinalCornerRadius(float radius) {
        mParams.mFinalCornerRadius = radius;
    }

    /**
     * This method is called when the button and its dependencies are going to draw it selves.
     *
     * @param canvas Canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mState == State.PROGRESS && !mIsMorphingInProgress) {
            drawIndeterminateProgress(canvas);
        } else if(mState == State.DONE){
            drawDoneAnimation(canvas);
        }
    }

    /**
     * If the mAnimatedDrawable is null or its not running, it get created. Otherwise its draw method is
     * called here.
     *
     * @param canvas Canvas
     */
    private void drawIndeterminateProgress(Canvas canvas) {
        if (mAnimatedDrawable == null || !mAnimatedDrawable.isRunning()) {
            mAnimatedDrawable = new CircularAnimatedDrawable(this,
                    mParams.mSpinningBarWidth,
                    mParams.mSpinningBarColor);

            int offset = (getWidth() - getHeight()) / 2;

            int left = offset + mParams.mPaddingProgress.intValue();
            int right = getWidth() - offset - mParams.mPaddingProgress.intValue();
            int bottom = getHeight() - mParams.mPaddingProgress.intValue();
            int top = mParams.mPaddingProgress.intValue();

            mAnimatedDrawable.setBounds(left, top, right, bottom);
            mAnimatedDrawable.setCallback(this);
            mAnimatedDrawable.start();
        } else {
            mAnimatedDrawable.draw(canvas);
        }
    }


    /**
     * Stops the animation and sets the button in the STOPED state.
     */
    public void stopAnimation() {
        if(mState == State.PROGRESS && !mIsMorphingInProgress) {
            mState = State.STOPED;
            mAnimatedDrawable.stop();
        }
    }

    /**
     * Call this method when you want to show a 'completed' or a 'done' status. You have to choose the
     * color and the image to be shown. If your loading progress ended with a success status you probrably
     * want to put a icon for "sucess" and a blue color, otherwise red and a failure icon. You can also
     * show that a music is completed... or show some status on a game... be creative!
     *
     * @param fillColor The color of the background of the button
     * @param bitmap The image that will be shown
     */
    public void doneLoadingAnimation(int fillColor, Bitmap bitmap) {
        if(mState != State.PROGRESS) {
            return;
        }

        if(mIsMorphingInProgress) {
            doneWhileMorphing = true;
            mFillColorDone = fillColor;
            mBitmapDone = bitmap;
            return;
        }

        mState = State.DONE;
        mAnimatedDrawable.stop();

        mRevealDrawable = new CircularRevealAnimatedDrawable(this, fillColor, bitmap);

        int left = 0;
        int right = getWidth() ;
        int bottom = getHeight();
        int top = 0;

        mRevealDrawable.setBounds(left, top, right, bottom);
        mRevealDrawable.setCallback(this);
        mRevealDrawable.start();
    }

    /**
     * Method called on the onDraw when the button is on DONE status
     *
     * @param canvas Canvas
     */
    private void drawDoneAnimation(Canvas canvas){
        mRevealDrawable.draw(canvas);
    }

    public void revertAnimation(){
        revertAnimation(null);
    }

    public void revertAnimation(final OnAnimationEndListener onAnimationEndListener){
        if (mState == State.IDLE) {
            return;
        }

        mState = State.IDLE;

        if(mAnimatedDrawable != null && mAnimatedDrawable.isRunning()){
            stopAnimation();
        }

        if(mIsMorphingInProgress){
            mAnimatorSet.cancel();
        }

        setClickable(false);

        int fromWidth = getWidth();
        int fromHeight = getHeight();

        int toHeight =  mParams.mInitialHeight;
        int toWidth = mParams.mInitialWidth;
        ObjectAnimator cornerAnimation = null;
        if (mGradientDrawable != null) {
            cornerAnimation = ObjectAnimator.ofFloat(mGradientDrawable,
                            "cornerRadius",
                            mParams.mFinalCornerRadius,
                            mParams.mInitialCornerRadius);
        }

        ValueAnimator widthAnimation = ValueAnimator.ofInt(fromWidth, toWidth);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = val;
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator heightAnimation = ValueAnimator.ofInt(fromHeight, toHeight);
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = val;
                setLayoutParams(layoutParams);
            }
        });

        /*ValueAnimator strokeAnimation = ValueAnimator.ofFloat(
                getResources().getDimension(R.dimen.stroke_login_button),
                getResources().getDimension(R.dimen.stroke_login_button_loading));

        strokeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                ((ShapeDrawable)mGradientDrawable).getPaint().setStrokeWidth((Float)animation.getAnimatedValue());
            }
        });*/

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(300);
        if (mGradientDrawable != null) {
            mAnimatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        } else {
            mAnimatorSet.playTogether(widthAnimation, heightAnimation);
        }
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setClickable(true);
                mIsMorphingInProgress = false;
                setText(mParams.mText);
                setCompoundDrawablesRelative(mParams.mDrawables[0],mParams.mDrawables[1],mParams.mDrawables[2],mParams.mDrawables[3]);
                if (onAnimationEndListener!=null) {
                    onAnimationEndListener.onAnimationEnd();
                }
            }
        });

        mIsMorphingInProgress = true;
        mAnimatorSet.start();
    }

    @Override
    public void dispose() {
        if (mAnimatedDrawable != null) {
            mAnimatedDrawable.dispose();
        }

        if (mRevealDrawable != null) {
            mRevealDrawable.dispose();
        }
    }

    /**
     * Method called to start the animation. Morphs in to a ball and then starts a loading spinner.
     */
    public void startAnimation() {
        if(mState != State.IDLE) {
            return;
        }

        if(mIsMorphingInProgress) {
            mAnimatorSet.cancel();
        } else {
            mParams.mInitialWidth = getWidth();
            mParams.mInitialHeight = getHeight();
        }

        mState = State.PROGRESS;

        mParams.mText = getText().toString();

        this.setCompoundDrawables(null, null, null, null);
        this.setText(null);
        setClickable(false);

        int toHeight =  mParams.mInitialHeight;
        int toWidth = toHeight; //Making a perfect circle

        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(mGradientDrawable,
                        "cornerRadius",
                        mParams.mInitialCornerRadius,
                        mParams.mFinalCornerRadius);

        ValueAnimator widthAnimation = ValueAnimator.ofInt(mParams.mInitialWidth, toWidth);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = val;
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator heightAnimation = ValueAnimator.ofInt(mParams.mInitialHeight, toHeight);
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = val;
                setLayoutParams(layoutParams);
            }
        });

        /*ValueAnimator strokeAnimation = ValueAnimator.ofFloat(
                getResources().getDimension(R.dimen.stroke_login_button),
                getResources().getDimension(R.dimen.stroke_login_button_loading));

        strokeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                ((ShapeDrawable)mGradientDrawable).getPaint().setStrokeWidth((Float)animation.getAnimatedValue());
            }
        });*/

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(300);
        mAnimatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsMorphingInProgress = false;

                if (doneWhileMorphing) {
                    doneWhileMorphing = false;

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            doneLoadingAnimation(mFillColorDone, mBitmapDone);
                        }
                    };

                    new Handler().postDelayed(runnable, 50);
                }
            }
        });

        mIsMorphingInProgress = true;
        mAnimatorSet.start();
    }


    /**
     * Check if button is animating
     */
    public Boolean isAnimating() {
        return mState == State.PROGRESS;
    }

    /**
     * Class with all the params to configure the button.
     */
    private class Params {
        private float mSpinningBarWidth;
        private int mSpinningBarColor;
        private int mDoneColor;
        private Float mPaddingProgress;
        private Integer mInitialHeight;
        private int mInitialWidth;
        private String mText;
        private float mInitialCornerRadius;
        private float mFinalCornerRadius;
        private Drawable[] mDrawables;
    }
}
