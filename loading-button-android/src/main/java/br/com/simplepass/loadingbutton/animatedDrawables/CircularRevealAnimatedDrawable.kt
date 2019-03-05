package br.com.simplepass.loadingbutton.animatedDrawables

import android.animation.*
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import br.com.simplepass.loadingbutton.disposeAnimator

private const val REVEAL_DURATION = 120L
private const val ALPHA_ANIMATION_DURATION = 80L

internal class CircularRevealAnimatedDrawable(
    private val progressButton: ProgressButton,
    fillColor: Int,
    image: Bitmap
) : Drawable(), Animatable {

    private var currentRadius = 0F
    private var isFilled = false
    private var imageReadyAlpha = 0

    private val finalRadius: Float by lazy { (bounds.right - bounds.left).toFloat() / 2 }
    private val centerWidth: Float by lazy { (bounds.right + bounds.left).toFloat() / 2 }
    private val centerHeight: Float by lazy { (bounds.bottom + bounds.top).toFloat() / 2 }

    private val readyImage: Bitmap by lazy {
        Bitmap.createScaledBitmap(image, bitMapWidth().toInt(), bitMapHeight().toInt(), false)
    }

    private val bitMapXOffset: Float by lazy {
        (centerWidth - bitMapWidth() / 2).toFloat()
    }

    private val bitMapYOffset: Float by lazy {
        ((centerHeight - bitMapHeight() / 2)).toFloat()
    }

    private val conclusionAnimation: AnimatorSet by lazy {
        AnimatorSet().apply {
            playSequentially(
                revealAnimator(finalRadius, DecelerateInterpolator()),
                alphaAnimator()
            )
        }
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = fillColor
    }

    private val imageReadyPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.TRANSPARENT
    }

    private fun bitMapWidth(): Double = ((bounds.right - bounds.left) * 0.6)

    private fun bitMapHeight(): Double = ((bounds.bottom - bounds.top) * 0.6)

    private fun revealAnimator(radius: Float, timeInterpolator: TimeInterpolator): Animator =
        ValueAnimator.ofFloat(0F, radius).apply {
            interpolator = timeInterpolator
            duration = REVEAL_DURATION

            addUpdateListener { animation ->
                currentRadius = animation.animatedValue as Float
                progressButton.invalidate()
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    isFilled = true
                }
            })
        }

    private fun alphaAnimator(): Animator =
        ValueAnimator.ofInt(0, 255).apply {
            duration = ALPHA_ANIMATION_DURATION
            addUpdateListener { animation ->
                imageReadyAlpha = animation.animatedValue as Int
                progressButton.invalidate()
            }
        }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(centerWidth, centerHeight, currentRadius, paint)

        if (isFilled) {
            imageReadyPaint.alpha = imageReadyAlpha
            canvas.drawBitmap(readyImage, bitMapXOffset, bitMapYOffset, imageReadyPaint)
        }
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun isRunning(): Boolean = conclusionAnimation.isRunning

    override fun start() {
        conclusionAnimation.start()
    }

    override fun stop() {
        conclusionAnimation.end()
    }

    fun dispose() {
        conclusionAnimation.disposeAnimator()
    }
}
