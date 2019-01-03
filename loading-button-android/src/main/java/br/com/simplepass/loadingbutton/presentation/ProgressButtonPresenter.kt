package br.com.simplepass.loadingbutton.presentation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import br.com.simplepass.loadingbutton.customViews.ProgressButton

internal enum class State {
    BEFORE_DRAW, IDLE, MORPHING, MORPHING_REVERT, PROGRESS, DONE, STOPPED
}

internal class ProgressButtonPresenter(private val view: ProgressButton) {
    var state: State = State.BEFORE_DRAW

    private var waitingToStartProgress = false
    private var waitingToStartDone: Boolean = false

    fun morphStart() {
        view.run {
            hideInitialState()
            setClickable(false)
            setCompoundDrawables(null, null, null, null)
        }

        waitingToStartProgress = false
        state = State.MORPHING
    }

    fun morphEnd() {
        if (waitingToStartDone) {
            waitingToStartDone = false

            Handler().postDelayed({ view.startRevealAnimation() }, 50)
        }

        state = State.PROGRESS
    }

    fun morphRevertStart() {
        view.setClickable(false)
        state = State.MORPHING
    }

    fun morphRevertEnd() {
        view.setClickable(true)
        view.recoverInitialState()
        state = State.IDLE

        // Todo: Fix this!
        // setCompoundDrawablesRelative(mParams.mDrawables[0], mParams.mDrawables[1], mParams.mDrawables[2], mParams.mDrawables[3])
    }

    fun onDraw(canvas: Canvas) {
        if (state == State.BEFORE_DRAW) {
            state = State.IDLE
            view.saveInitialState()
        }

        when (state) {
            State.IDLE -> {
                if (waitingToStartProgress) {
                    view.startMorphAnimation()
                }
            }
            State.PROGRESS -> view.drawProgress(canvas)
            State.DONE -> view.drawDoneAnimation(canvas)
            else -> {
            }
        }
    }

    fun startAnimation() {
        if (state == State.BEFORE_DRAW) {
            waitingToStartProgress = true
            return
        }

        if (state != State.IDLE) {
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
            State.MORPHING -> {
                view.stopMorphAnimation()
            }
            State.PROGRESS -> {
                view.stopProgressAnimation()
            }
            else -> {
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
            else -> {
            }
        }

        state = State.DONE
    }
}
