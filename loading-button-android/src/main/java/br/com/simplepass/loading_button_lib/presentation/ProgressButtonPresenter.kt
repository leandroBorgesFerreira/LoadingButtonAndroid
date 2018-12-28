package br.com.simplepass.loading_button_lib.presentation

import android.animation.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.*
import android.os.Build
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularProgressAnimatedDrawable
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularRevealAnimatedDrawable
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import br.com.simplepass.loading_button_lib.disposeAnimator
import br.com.simplepass.loading_button_lib.updateHeight
import br.com.simplepass.loading_button_lib.updateWidth

internal enum class State {
    BEFORE_DRAW, IDLE, MORPHING, MORPHING_REVERT, PROGRESS, DONE, STOPPED
}

internal class ProgressButtonPresenter(private val view: CircularProgressButton) {
    var state: State = State.BEFORE_DRAW

    var finalCorner = 0F
    var initialCorner = 0F

    var spinningBarWidth = 10F
    var spinningBarColor = ContextCompat.getColor(view.context, android.R.color.black)

    var paddingProgress = 0F

    private val finalWidth: Int by lazy { finalHeight }
    private var initialWidth = 0

    private val finalHeight: Int by lazy { view.height }
    private val initialHeight: Int by lazy { view.height }

    lateinit var drawable: GradientDrawable
    private val initialText = view.text

    private var waitingToStartProgress = false
    private var waitingToStartDone: Boolean = false

    private var doneFillColor: Int = ContextCompat.getColor(view.context, android.R.color.black)
    private lateinit var doneImage: Bitmap

    private val morphAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawable, initialCorner, finalCorner),
                widthAnimator(view, initialWidth, finalWidth),
                heightAnimator(view, initialHeight, finalHeight)
            )

            addListener(morphListener(::morphStart, ::morphEnd))
        }
    }

    private val morphRevertAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawable, finalCorner, initialCorner),
                widthAnimator(view, finalWidth, initialWidth),
                heightAnimator(view, finalHeight, initialHeight)
            )

            addListener(morphListener(::morphRevertStart, ::morphRevertEnd))
        }
    }

    private val progressAnimatedDrawable: CircularProgressAnimatedDrawable by lazy {
        view.run {
            CircularProgressAnimatedDrawable(this, spinningBarWidth, spinningBarColor).apply {
                val offset = (width - height) / 2

                val left = offset + paddingProgress.toInt()
                val right = width - offset - paddingProgress.toInt()
                val bottom = height - paddingProgress.toInt()
                val top = paddingProgress.toInt()

                setBounds(left, top, right, bottom)
                callback = this@run
            }
        }
    }

    private val revealAnimatedDrawable: CircularRevealAnimatedDrawable by lazy {
        view.run {
            CircularRevealAnimatedDrawable(this, doneFillColor, doneImage).apply {
                setBounds(0, 0, width, height)
                callback = this@run
            }
        }
    }

    private fun auxiliaryConfig() {
        initialWidth = view.width
    }

    private fun morphStart() {
        view.run {
            isClickable = false
            text = null
            setCompoundDrawables(null, null, null, null)
        }

        waitingToStartProgress = false
        state = State.MORPHING
    }

    private fun morphEnd() {
        view.isClickable = true

        if (waitingToStartDone) {
            waitingToStartDone = false

            Handler().postDelayed({ startRevealAnimation() }, 50)
        }

        state = State.PROGRESS
    }

    private fun morphRevertStart() {
        view.isClickable = false
        state = State.MORPHING
    }

    private fun morphRevertEnd() {
        view.isClickable = true
        state = State.IDLE
        view.text = initialText

        //Todo: Fix this!
        //        setCompoundDrawablesRelative(mParams.mDrawables[0], mParams.mDrawables[1], mParams.mDrawables[2], mParams.mDrawables[3])
    }

    private fun drawProgress(canvas: Canvas) {
        progressAnimatedDrawable.run {
            if (isRunning) {
                draw(canvas)
            } else {
                start()
            }
        }
    }

    fun onDraw(canvas: Canvas) {
        if (state == State.BEFORE_DRAW) {
            state = State.IDLE
            auxiliaryConfig()
        }

        when (state) {
            State.IDLE     -> {
                if (waitingToStartProgress) {
                    morphAnimator.start()
                }
            }
            State.PROGRESS -> drawProgress(canvas)
            State.DONE     -> drawDoneAnimation(canvas)
            else           -> {
            }
        }
    }

    private fun startRevealAnimation() {
        revealAnimatedDrawable.start()
    }

    private fun drawDoneAnimation(canvas: Canvas) {
        revealAnimatedDrawable.draw(canvas)
    }

    fun startAnimation() {
        if (state != State.IDLE) {
            return
        }

        if (state == State.BEFORE_DRAW) {
            waitingToStartProgress = true
            return
        }

        morphAnimator.start()
    }

    fun stopAnimation() {
        state = State.STOPPED

        if (state == State.PROGRESS) {
            progressAnimatedDrawable.stop()
        }
    }

    fun revertAnimation(onAnimationEndListener: () -> Unit = {}) {
        if (state == State.IDLE) {
            return
        }

        when (state) {
            State.IDLE, State.MORPHING_REVERT -> return
            State.MORPHING                    -> {
                morphAnimator.end()
            }
            State.PROGRESS                    -> {
                progressAnimatedDrawable.stop()
            }
            else                              -> {
            }
        }

        morphRevertAnimator.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEndListener()
                }
            })
        }.start()
    }

    fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap) {
        doneFillColor = fillColor
        doneImage = bitmap

        when (state) {
            State.PROGRESS -> {
                progressAnimatedDrawable.stop()
                startRevealAnimation()
            }
            State.MORPHING -> {
                waitingToStartDone = true
            }
            else           -> {
            }
        }

        state = State.DONE
    }

    fun dispose() {
        morphAnimator.disposeAnimator()
        morphRevertAnimator.disposeAnimator()
    }
}

internal fun parseGradientDrawable(drawable: Drawable): GradientDrawable =
    when (drawable) {
        is GradientDrawable  -> drawable
        is ColorDrawable     -> GradientDrawable().apply { setColor(drawable.color) }
        is InsetDrawable     -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                drawable.drawable?.let { innerDrawable ->
                    parseGradientDrawable(innerDrawable)
                }
                    ?: throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            } else {
                throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            }
        }
        is StateListDrawable -> {
            if (drawable.current is GradientDrawable) {
                drawable.current as GradientDrawable
            } else {
                throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            }
        }
        else                 -> throw RuntimeException("Error reading background... Use a shape or a color in xml!")

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
