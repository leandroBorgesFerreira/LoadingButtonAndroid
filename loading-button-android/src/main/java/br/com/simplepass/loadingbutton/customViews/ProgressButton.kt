package br.com.simplepass.loadingbutton.customViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import br.com.simplepass.loadingbutton.R
import br.com.simplepass.loadingbutton.animatedDrawables.CircularProgressAnimatedDrawable
import br.com.simplepass.loadingbutton.animatedDrawables.CircularRevealAnimatedDrawable
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.presentation.State
import br.com.simplepass.loadingbutton.updateHeight
import br.com.simplepass.loadingbutton.updateWidth
import br.com.simplepass.loadingbutton.utils.addLifecycleObserver
import br.com.simplepass.loadingbutton.utils.parseGradientDrawable

interface ProgressButton : Drawable.Callback, LifecycleObserver {
    var paddingProgress: Float
    var spinningBarWidth: Float
    var spinningBarColor: Int

    var initialCorner: Float
    var finalCorner: Float

    val finalWidth: Int
    val finalHeight: Int

    var doneFillColor: Int
    var doneImage: Bitmap

    var drawable: GradientDrawable
    var progressType: ProgressType

    fun invalidate()

    fun getHeight(): Int
    fun getWidth(): Int
    fun getContext(): Context
    fun getState(): State

    fun setClickable(b: Boolean)
    fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?)
    fun setBackground(background: Drawable)

    fun saveInitialState()
    fun recoverInitialState()
    fun hideInitialState()

    fun startAnimation(onAnimationEndListener: () -> Unit = {})
    fun startMorphAnimation()
    fun startMorphRevertAnimation()
    fun stopMorphAnimation()
    fun stopAnimation()
    fun stopProgressAnimation()

    fun revertAnimation(onAnimationEndListener: () -> Unit = {})

    fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap)

    fun startRevealAnimation()
    fun drawProgress(canvas: Canvas)
    fun drawDoneAnimation(canvas: Canvas)

    fun setProgress(value: Float)
}

internal fun ProgressButton.init(attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
    val typedArray: TypedArray? = attrs?.run {
        getContext().obtainStyledAttributes(this, R.styleable.CircularProgressButton, defStyleAttr, 0)
    }

    val typedArrayBg: TypedArray? = attrs?.run {
        val attrsArray = intArrayOf(android.R.attr.background)
        getContext().obtainStyledAttributes(this, attrsArray, defStyleAttr, 0)
    }

    drawable = parseGradientDrawable(
        typedArrayBg?.getDrawable(0)
            ?: ContextCompat.getDrawable(getContext(), R.drawable.shape_default)!!
    )

    setBackground(drawable)

    typedArray?.let { tArray -> config(tArray) }

    typedArray?.recycle()
    typedArrayBg?.recycle()

    // all ProgressButton instances implement LifecycleObserver, so we can
    // auto-register each instance on initialization
    getContext().addLifecycleObserver(this)
}

internal fun ProgressButton.config(tArray: TypedArray) {
    initialCorner = tArray.getDimension(R.styleable.CircularProgressButton_initialCornerAngle, 0f)
    finalCorner = tArray.getDimension(R.styleable.CircularProgressButton_finalCornerAngle, 100f)

    spinningBarWidth = tArray.getDimension(R.styleable.CircularProgressButton_spinning_bar_width, 10f)
    spinningBarColor = tArray.getColor(R.styleable.CircularProgressButton_spinning_bar_color, spinningBarColor)

    paddingProgress = tArray.getDimension(R.styleable.CircularProgressButton_spinning_bar_padding, 0F)
}

internal fun ProgressButton.createProgressDrawable(): CircularProgressAnimatedDrawable =
    CircularProgressAnimatedDrawable(this, spinningBarWidth, spinningBarColor).apply {
        val offset = (finalWidth - finalHeight) / 2

        val left = offset + paddingProgress.toInt()
        val right = finalWidth - offset - paddingProgress.toInt()
        val bottom = finalHeight - paddingProgress.toInt()
        val top = paddingProgress.toInt()

        setBounds(left, top, right, bottom)
        callback = this@createProgressDrawable
    }

internal fun ProgressButton.createRevealAnimatedDrawable(): CircularRevealAnimatedDrawable =
    CircularRevealAnimatedDrawable(this, doneFillColor, doneImage).apply {
        setBounds(0, 0, finalWidth, finalHeight)
        callback = this@createRevealAnimatedDrawable
    }

internal fun cornerAnimator(drawable: GradientDrawable, initial: Float, final: Float) =
    ObjectAnimator.ofFloat(drawable, "cornerRadius", initial, final)

internal fun widthAnimator(view: View, initial: Int, final: Int) =
    ValueAnimator.ofInt(initial, final).apply {
        addUpdateListener { animation ->
            view.updateWidth(animation.animatedValue as Int)
        }
    }

internal fun heightAnimator(view: View, initial: Int, final: Int) =
    ValueAnimator.ofInt(initial, final).apply {
        addUpdateListener { animation ->
            view.updateHeight(animation.animatedValue as Int)
        }
    }

internal fun morphListener(morphStartFn: () -> Unit, morphEndFn: () -> Unit) =
    object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            morphEndFn()
        }

        override fun onAnimationStart(animation: Animator?) {
            morphStartFn()
        }
    }

internal fun CircularProgressAnimatedDrawable.drawProgress(canvas: Canvas) {
    if (isRunning) {
        draw(canvas)
    } else {
        start()
    }
}

internal fun applyAnimationEndListener(animator: Animator, onAnimationEndListener: () -> Unit) =
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEndListener()
            animator.removeListener(this)
        }
    })
