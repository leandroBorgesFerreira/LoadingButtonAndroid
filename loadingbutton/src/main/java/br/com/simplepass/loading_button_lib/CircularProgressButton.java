package br.com.simplepass.loading_button_lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Classe botão
 */
public class CircularProgressButton extends Button {
    private enum State {
        PROGRESS, IDLE, DONE, STOPED
    }

    private Context mContext;

    //private CircularAnimatedDrawable mAnimatedDrawable;
    private GradientDrawable mGradientDrawable;

    private boolean mIsMorphingInProgress;
    private State mState;
    private CircularAnimatedDrawable mAnimatedDrawable;
    private CircularRevealAnimatedDrawable mRevealDrawable;
    private AnimatorSet mAnimatorSet;
    private Bitmap mReadyImage;

    private Params mParams;

    public CircularProgressButton(Context context) {
        super(context);
        init(context, null);
    }

    public CircularProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public CircularProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @TargetApi(23)
    public CircularProgressButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mContext = context;

        mParams = new Params();

        mParams.setPaddingProgress(0f);

        if(attrs == null) {
            mGradientDrawable = (GradientDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.shape_default, null);
        } else{
            int[] attrsArray = new int[] {
                    android.R.attr.background, // 0
            };

            TypedArray typedArray =  context.obtainStyledAttributes(attrs, R.styleable.CircularProgressButton);
            TypedArray typedArrayBG = context.obtainStyledAttributes(attrs, attrsArray);

            try {
                mGradientDrawable = (GradientDrawable) typedArrayBG.getDrawable(0);

            } catch (ClassCastException e) {
                Drawable drawable = typedArrayBG.getDrawable(0);

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

            mParams.setInitialCornerRadius(typedArray.getDimension(
                    R.styleable.CircularProgressButton_initialCornerAngle, 0));
            mParams.setFinalCornerRadius(typedArray.getDimension(
                    R.styleable.CircularProgressButton_finalCornerAngle, 100));
            mParams.setSpinningBarWidth(typedArray.getDimension(
                    R.styleable.CircularProgressButton_spinning_bar_width, 10));
            mParams.setSpinningBarColor(typedArray.getColor(R.styleable.CircularProgressButton_spinning_bar_color,
                    ContextCompat.getColor(context, android.R.color.black)));
            mParams.setPaddingProgress(typedArray.getDimension(R.styleable.CircularProgressButton_spinning_bar_padding, 0));

            typedArray.recycle();
            typedArrayBG.recycle();
        }

        mState = State.IDLE;

        mParams.setText(this.getText().toString());
        setBackground(mGradientDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (mState == State.PROGRESS && !mIsMorphingInProgress) {
            drawIndeterminateProgress(canvas);
        } else if(mState == State.DONE){
            drawDoneAnimation(canvas);
        }
    }


    private void drawIndeterminateProgress(Canvas canvas) {
        //Todo: Init this animatedDrawable in the onCreate.
        if (mAnimatedDrawable == null || !mAnimatedDrawable.isRunning()) {
            int offset = (getWidth() - getHeight()) / 2;
            mAnimatedDrawable = new CircularAnimatedDrawable(this,
                    mParams.getSpinningBarWidth(),
                    mParams.getSpinningBarColor());

            int left = offset + mParams.getPaddingProgress().intValue();
            int right = getWidth() - offset - mParams.getPaddingProgress().intValue();
            int bottom = getHeight() - mParams.getPaddingProgress().intValue();
            int top = mParams.getPaddingProgress().intValue();

            mAnimatedDrawable.setBounds(left, top, right, bottom);
            mAnimatedDrawable.setCallback(this);
            mAnimatedDrawable.start();
        } else {
            mAnimatedDrawable.draw(canvas);
        }
    }

    public void stopAnimation(){
        if(mState == State.PROGRESS && !mIsMorphingInProgress) {
            mState = State.STOPED;
            mAnimatedDrawable.stop();
        }
    }

    public void doneLoagingAnimation(int fillColor, Bitmap bitmap){
        if(mState != State.PROGRESS) {
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

    public void drawDoneAnimation(Canvas canvas){
        mRevealDrawable.draw(canvas);
    }

    public void revertAnimation(){
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

        int toHeight =  mParams.getInitialHeight();
        int toWidth = mParams.getInitialWidth();

        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(mGradientDrawable,
                        "cornerRadius",
                        mParams.getFinalCornerRadius(),
                        mParams.getInitialCornerRadius());

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
        mAnimatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setClickable(true);
                mIsMorphingInProgress = false;
                setText(mParams.getText());
            }
        });

        mIsMorphingInProgress = true;
        mAnimatorSet.start();
    }

    public void startAnimation(){
        if(mState != State.IDLE){
            return;
        }

        this.setText(null);
        setClickable(false);

        if(mIsMorphingInProgress){
            mAnimatorSet.cancel();
        }

        mState = State.PROGRESS;

        mParams.setInitialWidth(getWidth());
        mParams.setInitialHeight(getHeight());

        int toHeight =  (int) (mParams.getInitialHeight() * 1.2);
        int toWidth = toHeight; //Largura igual altura faz um circulo perfeito

        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(mGradientDrawable,
                        "cornerRadius",
                        mParams.getInitialCornerRadius(),
                        mParams.getFinalCornerRadius());

        ValueAnimator widthAnimation = ValueAnimator.ofInt(mParams.getInitialWidth(), toWidth);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = val;
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator heightAnimation = ValueAnimator.ofInt(mParams.getInitialHeight(), toHeight);
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
            }
        });

        mIsMorphingInProgress = true;
        mAnimatorSet.start();

    }

    private class Params{
        private float mSpinningBarWidth;
        private int mSpinningBarColor;
        private int mDoneColorColor;
        private Float mPaddingProgress;
        private Integer mInitialHeight;
        private int mInitialWidth;
        private String mText;
        private float mInitialCornerRadius;
        private float mFinalCornerRadius;

        public Params() {}

        public int getDoneColorColor() {
            return mDoneColorColor;
        }

        public void setDoneColorColor(int mDoneColorColor) {
            this.mDoneColorColor = mDoneColorColor;
        }

        public float getSpinningBarWidth() {
            return mSpinningBarWidth;
        }

        public void setSpinningBarWidth(float mSpinningBarWidth) {
            this.mSpinningBarWidth = mSpinningBarWidth;
        }

        public int getSpinningBarColor() {
            return mSpinningBarColor;
        }

        public void setSpinningBarColor(int mSpinningBarColor) {
            this.mSpinningBarColor = mSpinningBarColor;
        }

        public Float getPaddingProgress() {
            return mPaddingProgress;
        }

        public void setPaddingProgress(Float mPaddingProgress) {
            this.mPaddingProgress = mPaddingProgress;
        }

        public Integer getInitialHeight() {
            return mInitialHeight;
        }

        public void setInitialHeight(Integer mInitialHeight) {
            this.mInitialHeight = mInitialHeight;
        }

        public int getInitialWidth() {
            return mInitialWidth;
        }

        public void setInitialWidth(int mInitialWidth) {
            this.mInitialWidth = mInitialWidth;
        }

        public String getText() {
            return mText;
        }

        public void setText(String text) {
            this.mText = text;
        }

        public float getInitialCornerRadius() {
            return mInitialCornerRadius;
        }

        public void setInitialCornerRadius(float mInitialCornerRadius) {
            this.mInitialCornerRadius = mInitialCornerRadius;
        }

        public float getFinalCornerRadius() {
            return mFinalCornerRadius;
        }

        public void setFinalCornerRadius(float mFinalCornerRadius) {
            this.mFinalCornerRadius = mFinalCornerRadius;
        }
    }
}


/*
* ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(
                mGradientDrawable,
                "color",
                ContextCompat.getColor(mContext, R.color.white),
                ContextCompat.getColor(mContext, R.color.black));

        colorAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setText("\u2713");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        colorAnimator.setDuration(300);
        colorAnimator.start();
* */