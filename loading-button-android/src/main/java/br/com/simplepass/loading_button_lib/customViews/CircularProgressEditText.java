package br.com.simplepass.loading_button_lib.customViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;

import br.com.simplepass.loading_button_lib.animatedDrawables.CircularAnimatedDrawable;
import br.com.simplepass.loading_button_lib.R;


/**
 * Created by leandro on 5/31/16.
 */
public class CircularProgressEditText extends EditText {
    private enum State {
        PROGRESS, IDLE
    }

    //private CircularAnimatedDrawable mAnimatedDrawable;
    Drawable mDrawable;
    private boolean mIsMorphingInProgress;
    private State mState;
    private CircularAnimatedDrawable mAnimatedDrawable;
    private String mButtonText;
    private Integer mInitialHeight;
    private int mInitialWidth;
    private AnimatorSet mAnimatorSet;
    private float mSpinningBarWidth;
    private int mSpinningBarColor;


    private int mPaddingProgress; //ToDo: Colocar em attr depois!


    public CircularProgressEditText(Context context) {
        super(context);

        init(context, null, 0, 0);
    }

    public CircularProgressEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    public CircularProgressEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    public CircularProgressEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        mPaddingProgress = 0;

        if(attrs == null) {
            //mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.button_shape_login, null);
        } else{
            int[] attrsArray = new int[] {
                    android.R.attr.background, // 0
            };

            TypedArray typedArray =  context.obtainStyledAttributes(attrs, R.styleable.CircularProgressButton, defStyleAttr, defStyleRes);
            TypedArray typedArrayBG = context.obtainStyledAttributes(attrs, attrsArray, defStyleAttr, defStyleRes);
            mDrawable = typedArrayBG.getDrawable(0);
            mSpinningBarWidth = typedArray.getDimension(
                    R.styleable.CircularProgressButton_spinning_bar_width, 10);
            mSpinningBarColor = typedArray.getColor(R.styleable.CircularProgressButton_spinning_bar_color,
                    getResources().getColor(android.R.color.black));

            typedArray.recycle();
            typedArrayBG.recycle();
        }

        mState = State.IDLE;

        mButtonText = this.getText().toString();

        setBackground(mDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (mState == State.PROGRESS && !mIsMorphingInProgress) {
            drawIndeterminateProgress(canvas);
        }
    }


    private void drawIndeterminateProgress(Canvas canvas) {
        if (mAnimatedDrawable == null || !mAnimatedDrawable.isRunning()) {
            int offset = (getWidth() - getHeight()) / 2;
            mAnimatedDrawable = new CircularAnimatedDrawable(this,
                    mSpinningBarWidth,
                    mSpinningBarColor);

            int left = offset + mPaddingProgress;
            int right = getWidth() - offset - mPaddingProgress;
            int bottom = getHeight() - mPaddingProgress;
            int top = mPaddingProgress;

            mAnimatedDrawable.setBounds(left, top, right, bottom);
            mAnimatedDrawable.setCallback(this);
            mAnimatedDrawable.start();
        } else {
            mAnimatedDrawable.draw(canvas);
        }
    }

    public void stopAnimation(){
        if(mState == State.PROGRESS && !mIsMorphingInProgress) {
            mAnimatedDrawable.stop();
        }
    }

    public void revertAnimation(){
        if(mAnimatedDrawable != null && mAnimatedDrawable.isRunning()){
            stopAnimation();
        }

        if(mIsMorphingInProgress){
            mAnimatorSet.cancel();
        }

        setClickable(false);

        int fromWidth = getWidth();
        int fromHeight = getHeight();

        int toHeight =  mInitialHeight;
        int toWidth = mInitialWidth;

        mState = State.IDLE;

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

                ((ShapeDrawable)mDrawable).getPaint().setStrokeWidth((Float)animation.getAnimatedValue());
            }
        });*/

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(300);
        mAnimatorSet.playTogether(widthAnimation, heightAnimation);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setClickable(true);
                mIsMorphingInProgress = false;
                setText(mButtonText);
            }
        });

        mIsMorphingInProgress = true;
        mAnimatorSet.start();
    }

    public void startAnimation() {
        if(mState != State.IDLE) {
            return;
        }

        this.setText(null);
        setClickable(false);

        if(mIsMorphingInProgress ){
            mAnimatorSet.cancel();
        }

        mInitialWidth = getWidth();
        mInitialHeight = getHeight();

        int toHeight =  (int) (mInitialHeight * 1.2);
        int toWidth = toHeight; //Largura igual altura faz um circulo perfeito

        mState = State.PROGRESS;

        ValueAnimator widthAnimation = ValueAnimator.ofInt(mInitialWidth, toWidth);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = val;
                setLayoutParams(layoutParams);
            }
        });

        ValueAnimator heightAnimation = ValueAnimator.ofInt(mInitialHeight, toHeight);
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

                ((ShapeDrawable)mDrawable).getPaint().setStrokeWidth((Float)animation.getAnimatedValue());
            }
        });*/

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(300);
        mAnimatorSet.playTogether(widthAnimation, heightAnimation);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsMorphingInProgress = false;
                setClickable(true);
            }
        });

        mIsMorphingInProgress = true;
        mAnimatorSet.start();

    }
}
