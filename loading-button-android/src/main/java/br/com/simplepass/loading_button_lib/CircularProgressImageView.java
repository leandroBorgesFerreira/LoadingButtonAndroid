package br.com.simplepass.loading_button_lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by hinovamobile on 01/11/16.
 */
public class CircularProgressImageView extends ImageView {
    private enum State {PROGRESS, IDLE, DONE, STOPED}

    private State mState;
    private CircularAnimatedDrawableImage mAnimatedDrawable;

    private Params mParams;

    public CircularProgressImageView(Context context) {
        super(context);
    }

    public CircularProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public CircularProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @TargetApi(23)
    public CircularProgressImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    /**
     * Commom initializer method.
     *
     * @param context Context
     * @param attrs Atributes passed in the XML
     */
    private void init(Context context, AttributeSet attrs) {
        mState = State.IDLE;

        mParams = new Params();

        mParams.setPaddingProgress(0f);

        TypedArray typedArray =  context.obtainStyledAttributes(attrs, R.styleable.CircularProgressImageView);

        mParams.mSpinningBarWidth = typedArray.getDimension(
                R.styleable.CircularProgressImageView_loading_spinning_bar_width, 10);
        mParams.mSpinningBarColor = typedArray.getColor(R.styleable.CircularProgressImageView_loading_spinning_bar_color,
                ContextCompat.getColor(context, android.R.color.black));
        mParams.mPaddingProgress = typedArray.getDimension(R.styleable.CircularProgressImageView_loading_spinning_bar_padding, 0);

        typedArray.recycle();
    }

    /**
     * This method is called when the button and its dependencies are going to draw it selves.
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (mState == State.PROGRESS) {
            drawIndeterminateProgress(canvas);
        } else if(mState == State.DONE){
            //drawDoneAnimation(canvas);
        }
    }

    /**
     * If the mAnimatedDrawable is null or its not running, it get created. Otherwise its draw method is
     * called here.
     *
     * @param canvas
     */
    private void drawIndeterminateProgress(Canvas canvas) {
        //Todo: Init this animatedDrawable in the onCreate.
        if (mAnimatedDrawable == null || !mAnimatedDrawable.isRunning()) {
            int offset = (getWidth() - getHeight()) / 2;
            mAnimatedDrawable = new CircularAnimatedDrawableImage(this,
                    mParams.mSpinningBarWidth,
                    mParams.mSpinningBarColor);

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
     * Method called to start the animation. Morphs in to a ball and then starts a loading spinner.
     */
    public void startAnimation(){
        if (mAnimatedDrawable == null || !mAnimatedDrawable.isRunning()) {
            int offset = (getWidth() - getHeight()) / 2;
            mAnimatedDrawable = new CircularAnimatedDrawableImage(this,
                    mParams.mSpinningBarWidth,
                    mParams.mSpinningBarColor);

            int left = offset + mParams.mPaddingProgress.intValue();
            int right = getWidth() - offset - mParams.mPaddingProgress.intValue();
            int bottom = getHeight() - mParams.mPaddingProgress.intValue();
            int top = mParams.mPaddingProgress.intValue();

            mAnimatedDrawable.setBounds(left, top, right, bottom);
            mAnimatedDrawable.setCallback(this);
            mAnimatedDrawable.start();
        } else{
            throw new IllegalStateException("It's already running!");
        }
    }

    /**
     * Class with all the params to configure the button.
     */
    private class Params {
        private Float mPaddingProgress;
        private float mSpinningBarWidth;
        private int mSpinningBarColor;

        public Params() {
        }

        public Float getPaddingProgress() {
            return mPaddingProgress;
        }

        public void setPaddingProgress(Float mPaddingProgress) {
            this.mPaddingProgress = mPaddingProgress;
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
    }
}
