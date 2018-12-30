package br.com.simplepass.loading_button_lib.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.*
import android.os.Build
import android.os.Handler
import android.view.View
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import br.com.simplepass.loading_button_lib.updateHeight
import br.com.simplepass.loading_button_lib.updateWidth

internal enum class State {
    BEFORE_DRAW, IDLE, MORPHING, MORPHING_REVERT, PROGRESS, DONE, STOPPED
}

internal class ProgressButtonPresenter(private val view: CircularProgressButton) {
    var state: State = State.BEFORE_DRAW

    private var waitingToStartProgress = false
    private var waitingToStartDone: Boolean = false

    private fun auxiliaryConfig() {
        view.initialWidth = view.width
    }

    fun morphStart() {
        view.run {
            isClickable = false
            text = null
            setCompoundDrawables(null, null, null, null)
        }

        waitingToStartProgress = false
        state = State.MORPHING
    }

    fun morphEnd() {
        view.isClickable = true

        if (waitingToStartDone) {
            waitingToStartDone = false

            Handler().postDelayed({ view.startRevealAnimation() }, 50)
        }

        state = State.PROGRESS
    }

    fun morphRevertStart() {
        view.isClickable = false
        state = State.MORPHING
    }

    fun morphRevertEnd() {
        view.isClickable = true
        state = State.IDLE
        view.text = view.initialText

        //Todo: Fix this!
        //        setCompoundDrawablesRelative(mParams.mDrawables[0], mParams.mDrawables[1], mParams.mDrawables[2], mParams.mDrawables[3])
    }

    fun onDraw(canvas: Canvas) {
        if (state == State.BEFORE_DRAW) {
            state = State.IDLE
            auxiliaryConfig()
        }

        when (state) {
            State.IDLE     -> {
                if (waitingToStartProgress) {
                    view.morphAnimator.start()
                }
            }
            State.PROGRESS -> view.drawProgress(canvas)
            State.DONE     -> view.drawDoneAnimation(canvas)
            else           -> {
            }
        }
    }

    fun startAnimation() {
        if (state != State.IDLE) {
            return
        }

        if (state == State.BEFORE_DRAW) {
            waitingToStartProgress = true
            return
        }

        view.startMorphAnimation()
    }

    fun stopAnimation() {
        state = State.STOPPED

        if (state == State.PROGRESS) {
            view.stopProgressAnimation()
        }
    }

    fun revertAnimation() {
        if (state == State.IDLE) {
            return
        }

        when (state) {
            State.IDLE, State.MORPHING_REVERT -> return
            State.MORPHING                    -> {
                view.stopMorphAnimation()
            }
            State.PROGRESS                    -> {
                view.stopProgressAnimation()
            }
            else                              -> {
            }
        }
    }

    fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap) {
        view.doneFillColor = fillColor
        view.doneImage = bitmap

        when (state) {
            State.PROGRESS -> {
                view.stopProgressAnimation()
                view.startRevealAnimation()
            }
            State.MORPHING -> {
                waitingToStartDone = true
            }
            else           -> {
            }
        }

        state = State.DONE
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
