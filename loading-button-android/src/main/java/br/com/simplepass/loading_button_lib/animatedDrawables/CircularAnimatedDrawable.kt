package br.com.simplepass.loading_button_lib.animatedDrawables

import android.animation.*
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

const val MIN_PROGRESS = 0F
const val MAX_PROGRESS = 100F
private const val ANGLE_ANIMATOR_DURATION = 2000L
private const val SWEEP_ANIMATOR_DURATION = 700L
private const val PROGRESS_ANIMATOR_DURATION = 200L
private const val MIN_SWEEP_ANGLE = 50f

class CircularAnimatedDrawable(
    private val animatedView: View,
    private val borderWidth: Float,
    arcColor: Int
) : Drawable(), Animatable {

    private val fBounds = RectF()
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
    private var shownProgress: Float = 0F

    private var progressType = ProgressType.INDETERMINATE
    private val determinateInterpolator = AccelerateDecelerateInterpolator()

    private val indeterminateAnimator = AnimatorSet().apply {
        playTogether(
            angleValueAnimator(LinearInterpolator()),
            sweepValueAnimator(determinateInterpolator)
        )
    }

    private fun toggleSweep() {
        modeAppearing = !modeAppearing

        if (modeAppearing) {
            currentGlobalAngleOffset = (currentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }

    private fun angleValueAnimator(interpolator: TimeInterpolator): ValueAnimator =
        ValueAnimator.ofFloat(0F, 360F).apply {
            setInterpolator(interpolator)
            duration = ANGLE_ANIMATOR_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation -> currentGlobalAngle = animation.animatedValue as Float }
        }

    private fun sweepValueAnimator(interpolator: TimeInterpolator): ValueAnimator =
        ValueAnimator.ofFloat(0F, 360F - 2 * MIN_SWEEP_ANGLE).apply {
            setInterpolator(interpolator)
            duration = SWEEP_ANIMATOR_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation ->
                currentSweepAngle = animation.animatedValue as Float

                if (currentSweepAngle < 5) {
                    shouldDraw = true
                }

                if (shouldDraw) {
                    animatedView.invalidate()
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator) {
                    toggleSweep()
                    shouldDraw = false
                }
            })
        }

    private fun disposeAnimator(animator: Animator) {
        animator.run {
            end()
            removeAllListeners()
            cancel()
        }
    }

    private fun getAngles(): Pair<Float, Float> =
        when (progressType) {
            ProgressType.DETERMINATE   -> {
                -90f to shownProgress
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

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        fBounds.run {
            left = bounds.left.toFloat() + borderWidth / 2F + .5F
            right = bounds.right.toFloat() - borderWidth / 2F - .5F
            top = bounds.top.toFloat() + borderWidth / 2F + .5F
            bottom = bounds.bottom.toFloat() - borderWidth / 2F - .5F
        }
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
        disposeAnimator(indeterminateAnimator)
    }

    fun setProgress(newProgress: Float) {
        if (progressType == ProgressType.INDETERMINATE) {
            stop()
            progressType = ProgressType.DETERMINATE
        }

        if (shownProgress == newProgress) {
            return
        }

        shownProgress = when {
            newProgress > MAX_PROGRESS -> MAX_PROGRESS
            newProgress < MIN_PROGRESS -> MIN_PROGRESS
            else                       -> newProgress
        }

        invalidateSelf()
    }
}
