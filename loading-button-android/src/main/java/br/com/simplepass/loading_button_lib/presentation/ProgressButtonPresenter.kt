package br.com.simplepass.loading_button_lib.presentation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton

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
        view.text = view.initialText
        state = State.IDLE

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
