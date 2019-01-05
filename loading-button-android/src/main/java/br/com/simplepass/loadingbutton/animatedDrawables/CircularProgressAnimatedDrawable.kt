package br.com.simplepass.loadingbutton.animatedDrawables

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import br.com.simplepass.loadingbutton.disposeAnimator

const val MIN_PROGRESS = 0F
const val MAX_PROGRESS = 100F
private const val ANGLE_ANIMATOR_DURATION = 2000L
private const val SWEEP_ANIMATOR_DURATION = 700L
private const val MIN_SWEEP_ANGLE = 50f

internal class CircularProgressAnimatedDrawable(
    private val progressButton: ProgressButton,
    private val borderWidth: Float,
    arcColor: Int
) : Drawable(), Animatable {

    private val fBounds: RectF by lazy {
        RectF().apply {
            left = bounds.left.toFloat() + borderWidth / 2F + .5F
            right = bounds.right.toFloat() - borderWidth / 2F - .5F
            top = bounds.top.toFloat() + borderWidth / 2F + .5F
            bottom = bounds.bottom.toFloat() - borderWidth / 2F - .5F
        }
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
        color = arcColor
    }

    private var currentGlobalAngle: Float = 0F
    private var currentSweepAngle: Float = 0F
    private var currentGlobalAngleOffset: Float = 0F

    private var modeAppearing: Boolean = false

    private var shouldDraw: Boolean = true

    var progressType = ProgressType.INDETERMINATE

    var progress: Float = 0F
        set(value) {
            if (progressType == ProgressType.INDETERMINATE) {
                stop()
                progressType = ProgressType.DETERMINATE
            }

            if (field == value) {
                return
            }

            field = when {
                value > MAX_PROGRESS -> MAX_PROGRESS
                value < MIN_PROGRESS -> MIN_PROGRESS
                else -> value
            }

            progressButton.invalidate()
        }

    private val indeterminateAnimator = AnimatorSet().apply {
        playTogether(
            angleValueAnimator(LinearInterpolator()),
            sweepValueAnimator(AccelerateDecelerateInterpolator())
        )
    }

    private fun toggleSweep() {
        modeAppearing = !modeAppearing

        if (modeAppearing) {
            currentGlobalAngleOffset = (currentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }

    private fun angleValueAnimator(timeInterpolator: TimeInterpolator): ValueAnimator =
        ValueAnimator.ofFloat(0F, 360F).apply {
            interpolator = timeInterpolator
            duration = ANGLE_ANIMATOR_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation -> currentGlobalAngle = animation.animatedValue as Float }
        }

    private fun sweepValueAnimator(timeInterpolator: TimeInterpolator): ValueAnimator =
        ValueAnimator.ofFloat(0F, 360F - 2 * MIN_SWEEP_ANGLE).apply {
            interpolator = timeInterpolator
            duration = SWEEP_ANIMATOR_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation ->
                currentSweepAngle = animation.animatedValue as Float

                if (currentSweepAngle < 5) {
                    shouldDraw = true
                }

                if (shouldDraw) {
                    progressButton.invalidate()
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator) {
                    toggleSweep()
                    shouldDraw = false
                }
            })
        }

    private fun getAngles(): Pair<Float, Float> =
        when (progressType) {
            ProgressType.DETERMINATE -> {
                -90f to progress
            }
            ProgressType.INDETERMINATE -> {
                if (modeAppearing) {
                    (currentGlobalAngle - currentGlobalAngleOffset) to currentSweepAngle + MIN_SWEEP_ANGLE
                } else {
                    (currentGlobalAngle - currentGlobalAngleOffset + currentSweepAngle) to
                        360f - currentSweepAngle - MIN_SWEEP_ANGLE
                }
            }
        }

    fun setLoadingBarColor(color: Int) {
        paint.color = color
    }

    override fun isRunning(): Boolean = indeterminateAnimator.isRunning

    override fun start() {
        if (isRunning) {
            return
        }

        indeterminateAnimator.start()
    }

    override fun stop() {
        if (!isRunning) {
            return
        }

        indeterminateAnimator.end()
    }

    override fun draw(canvas: Canvas) {
        val (startAngle, sweepAngle) = getAngles()
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    fun dispose() {
        indeterminateAnimator.disposeAnimator()
    }
}
